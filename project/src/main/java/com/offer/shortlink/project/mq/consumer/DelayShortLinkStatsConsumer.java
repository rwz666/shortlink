package com.offer.shortlink.project.mq.consumer;

import com.offer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.offer.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.offer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;

/**
 * @author rwz
 * @since 2025/2/18
 * 延迟记录短链接统计组件
 */
@Deprecated
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DelayShortLinkStatsConsumer.class);
    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    public void onMessage() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("delay_short-link_stats_consumer");
                thread.setDaemon(Boolean.TRUE);
                return thread;
        });
        executorService.execute(()-> {
            RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
            RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            for (; ; ) {
                try {
                    ShortLinkStatsRecordDTO statsRecord = delayedQueue.poll();
                    if (statsRecord != null) {
                        if (messageQueueIdempotentHandler.isMessageProcessed(statsRecord.getKeys())) {
                            // 判断当前的消息是否已经逻辑执行完成（防止设置了幂等但是没有执行：如服务器宕机）
                            if (messageQueueIdempotentHandler.isAccomplished(statsRecord.getKeys())) {
                                return;
                            }
                        }
                        try {
                            shortLinkService.shortLinkStats(statsRecord);
                        }catch (Throwable e) {
                            messageQueueIdempotentHandler.delMessageProcessed(statsRecord.getKeys());
                            log.error("延迟记录短链接监控消费异常", e);
                        }
                        messageQueueIdempotentHandler.setAccomplished(statsRecord.getKeys());
                        continue;
                    }
                    LockSupport.parkUntil(500);
                }catch (Throwable ignore) {

                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        onMessage();
    }

}
