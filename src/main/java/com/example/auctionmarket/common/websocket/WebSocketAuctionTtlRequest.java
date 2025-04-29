package com.example.auctionmarket.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketAuctionTtlRequest {
    private Long auctionId;
    private Long ttlMinutes;
}
