package com.offer.shortlink.project.mq.consumer;

import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author rwz
 * @since 2025/2/21
 * rocketMQ测试类
 */
@Component

public class RocketMQConsumerTest implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("rocketMQ message: " + s);
    }
}
