package com.example.auctionmarket.common.websocket;

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
    ResponseEntity<WebSocketAuctionCreateResponse> response = restTemplate.postForEntity(url, request, WebSocketAuctionCreateResponse.class);

    return response.getBody().getWebsocketUrl();
    }
}

//    public String notifyAuctionCreated(Long auctionId, String productName, Long minPrice) {
//        String url = "http://websocket-server.local/internal/auction/start";
//
//        Map<String, Object> request = new HashMap<>();
//        request.put("auctionId", auctionId);
//        request.put("productName", productName);
//        request.put("minPrice", minPrice);
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//        return response.getBody().get("websocketUrl").toString();
//    }
//}
