package com.example.auctionmarket.common.websocket;

import com.example.auctionmarket.domain.auction.dto.response.AuctionJoinResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WebSocketClient {

    private final RestTemplate restTemplate;

    @Value("${WEBSOCKET.SERVER.URL}")
    private String websocketUrl;

    @PostConstruct
    public void init() {
        System.out.println("💡 WebSocket URL: " + websocketUrl);
    }

    public String createAuctionRoom(WebSocketAuctionCreateRequest request) {
        String url = websocketUrl + "/internal/auction/start";
        ResponseEntity<com.example.auctionmarket.common.websocket.WebSocketAuctionCreateResponse> response = restTemplate.postForEntity(url, request, com.example.auctionmarket.common.websocket.WebSocketAuctionCreateResponse.class);

        return response.getBody().getWebsocketUrl();
    }

    public String joinAuctionRoom(WebSocketAuctionJoinRequest request) {
        String url = websocketUrl + "/internal/auction/join";
        ResponseEntity<AuctionJoinResponse> response = restTemplate.postForEntity(url, request, AuctionJoinResponse.class);

        return response.getBody().getWebsocketUrl();
    }
}
