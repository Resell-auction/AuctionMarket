package com.example.auctionmarket.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebSocketConfig {

    @Value("${WEBSOCKET.SERVER.URL}")
    private String websocketServerUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(websocketServerUrl)
                .build();
    }
}
