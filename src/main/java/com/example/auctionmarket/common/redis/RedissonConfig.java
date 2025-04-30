package com.example.auctionmarket.common.redis;

import org.redisson.config.Config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379");
                // .setAddress("redis://127.0.0.1:6379")
                // .setPassword("stockage");
        return Redisson.create(config);
    }
//
//    private void setPassword(String stockage) {
//    }
}
