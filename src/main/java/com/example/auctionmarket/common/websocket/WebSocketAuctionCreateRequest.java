package com.example.auctionmarket.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WebSocketAuctionCreateRequest {
    private Long auctionId;
    private String productName;
    private Long minPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
