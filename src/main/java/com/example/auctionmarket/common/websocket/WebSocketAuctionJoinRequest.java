package com.example.auctionmarket.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class WebSocketAuctionJoinRequest {
    private Long auctionId;
    private Long consumerId;
    private String nickname;
}
