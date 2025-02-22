package com.offer.shortlink.project.mq.producer;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY;

/**
 * @author rwz
 * @since 2025/2/20
 * 短链接监控状态保存消息队列生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {

    private final StringRedisTemplate stringRedisTemplate;

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic}")
    private String statsRocketMQTopic;

    @Value("${message-queue.select}")
    private String messageQueueSelect;

    /**
     * 发送延迟消息短链接统计
     */
    public void send(Map<String, String> producerMap) {
        switch (messageQueueSelect) {
            case "mq":
                sendByRocketMQ(producerMap);
                break;
            case "redis":
                sendByRedis(producerMap);
                break;
        }

    }

    private void sendByRedis(Map<String, String> producerMap) {
        stringRedisTemplate.opsForStream().add(SHORT_LINK_STATS_STREAM_TOPIC_KEY, producerMap);
    }

    private void sendByRocketMQ(Map<String, String> producerMap) {
        String keys = UUID.randomUUID().toString();
        producerMap.put("keys", keys);
        Message<Map<String, String>> build = MessageBuilder
                .withPayload(producerMap)
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .build();
        SendResult sendResult;
        try {
            sendResult = rocketMQTemplate.syncSend(statsRocketMQTopic, build, 2000L);
            log.info("[消息访问统计监控] 消息发送结果：{}，消息ID：{}，消息Keys：{}", sendResult.getSendStatus(), sendResult.getMsgId(), keys);
        } catch (Throwable e) {
            log.error("[消息访问统计监控] 消息发送失败 消息体：{}", JSON.toJSONString(producerMap), e);
            //自定义行为。。。
        }
    }
}
