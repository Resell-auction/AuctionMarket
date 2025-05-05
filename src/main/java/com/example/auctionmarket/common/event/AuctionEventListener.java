package com.example.auctionmarket.common.event;

import com.example.auctionmarket.common.websocket.WebSocketAuctionCreateRequest;
import com.example.auctionmarket.common.websocket.WebSocketClient;
import com.example.auctionmarket.domain.auction.entity.Auction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuctionEventListener {

    private final WebSocketClient webSocketClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionCreated(AuctionCreatedEvent event) {
        Auction auction = event.getAuction();

        webSocketClient.createAuctionRoom(
                new WebSocketAuctionCreateRequest(
                        auction.getId(),
                        auction.getProduct().getProductName(),
                        auction.getMinPrice(),
                        auction.getStartTime(),
                        auction.getEndTime()
                )
        );
    }
}
