package com.example.auctionmarket.common.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpiredListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        System.out.println("🔔 만료된 키: " + expiredKey);

        // 비즈니스 로직 수행
        // 예: 특정 캐시 무효화 처리
    }
}
