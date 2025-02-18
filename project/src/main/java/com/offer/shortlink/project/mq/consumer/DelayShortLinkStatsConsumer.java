package com.offer.shortlink.project.mq.consumer;

import com.offer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.offer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
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
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {

    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;

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
                        shortLinkService.shortLinkStats(null, null, statsRecord);
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
