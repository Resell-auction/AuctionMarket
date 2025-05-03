package com.example.auctionmarket.common.event;

import com.example.auctionmarket.common.websocket.WebSocketAuctionCreateRequest;
import com.example.auctionmarket.common.websocket.WebSocketClient;
import com.example.auctionmarket.domain.auction.entity.Auction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventListener {

    private final WebSocketClient webSocketClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionCreated(AuctionCreatedEvent event) {
        Auction auction = event.getAuction();

        log.info("트랜잭션 커밋 완료 > 소캣 서버에 경매장 생성요청");

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
