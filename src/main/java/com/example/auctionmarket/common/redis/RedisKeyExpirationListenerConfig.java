package com.example.auctionmarket.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisKeyExpirationListenerConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 채널 등록
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@0__:expired"));

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisKeyExpiredListener listener) {
        return new MessageListenerAdapter(listener);
    }
}
