package com.offer.shortlink.project.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rwz
 * @since 2025/2/21
 * TODO
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RocketMQProducerTest {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic}")
    private String topic;

    @GetMapping("/test")
    public SendResult test() {
        Message<String> msg = MessageBuilder.withPayload("Hello RocketMQ").build();
        SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
        log.info("[消息访问统计监控] 消息发送结果：{}", sendResult.getSendStatus());
        return sendResult;
    }
}
