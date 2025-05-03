package com.example.auctionmarket.common.websocket;

import com.example.auctionmarket.domain.auction.dto.response.AuctionJoinResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebSocketClient {

    private final WebClient webClient;

    public String createAuctionRoom(WebSocketAuctionCreateRequest request) {
        return webClient.post()
                .uri("/internal/auction/start")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WebSocketAuctionCreateResponse.class)
                .map(WebSocketAuctionCreateResponse::getWebsocketUrl)
                .block();
    }

    public String joinAuctionRoom(WebSocketAuctionJoinRequest request) {
        return webClient.post()
                .uri("/internal/auction/join")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuctionJoinResponse.class)
                .map(AuctionJoinResponse::getWebsocketUrl)
                .block();
    }
}
