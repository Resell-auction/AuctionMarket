package com.example.auctionmarket.domain.auction.entity;

import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionException;
import com.example.auctionmarket.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {
    private Product testProduct;
    private LocalDateTime testStartTime;
    private Auction auction;

    @BeforeEach
    void setUp(){
        testProduct = new Product();
        testStartTime = LocalDateTime.now().plusHours(1);
        auction = new Auction(testProduct, 10000L, testStartTime, 30L);
    }

    @Test
    @DisplayName("생성자 테스트")
    public void auctionTest(){
        assertThat(auction).isNotNull();
        assertThat(auction.getProduct()).isEqualTo(testProduct);
        assertThat(auction.getMinPrice()).isEqualTo(10000L);
        assertThat(auction.getMaxPrice()).isEqualTo(10000L);
        assertThat(auction.getStartTime()).isEqualTo(testStartTime);
        assertThat(auction.getDuration()).isEqualTo(Duration.ofMinutes(30));
        assertThat(auction.getEndTime()).isEqualTo(testStartTime.plusMinutes(30));
        assertThat(auction.getStatus()).isEqualTo(AuctionStatus.PENDING);
        assertThat(auction.getConsumerId()).isNull();
    }

    @Test
    @DisplayName("입찰 가격 증가")
    public void increaseMaxPrice_Success(){
        //given
        Long consumerId = 1L;
        Long newBidPrice = 15000L;

        //when
        auction.increaseMaxPrice(consumerId, newBidPrice);

        //then
        assertThat(auction.getMaxPrice()).isEqualTo(newBidPrice);
        assertThat(auction.getConsumerId()).isEqualTo(consumerId);
    }

    @Test
    @DisplayName("입찰 가격 증가 예외처리")
    public void increaseMaxPrice_Fail(){
        //given
        Long consumerId = 1L;
        Long initialMaxPrice = auction.getMaxPrice();
        Long invalidBidPrice = initialMaxPrice-1000L;

        //when & then
        assertThatThrownBy(()->auction.increaseMaxPrice(consumerId, invalidBidPrice))
                .isInstanceOf(AuctionException.class)
                .hasMessageContaining(AuctionErrorCode.INVALID_BID_PRICE.getDefaultMessage());
    }

    @Test
    @DisplayName("시작 시간 수정")
    public void updateStartTime_Success(){
        //given
        LocalDateTime newStartTime = LocalDateTime.now().plusHours(2);

        //when
        auction.updateStartTime(newStartTime);

        //then
        assertThat(auction.getStartTime()).isEqualTo(newStartTime);
        assertThat(auction.getEndTime()).isEqualTo(newStartTime.plusMinutes(30));
    }

    @Test
    @DisplayName("최소 가격 수정")
    public void updateMinPrice_Success(){
        //given
        Long newMinPrice = 20000L;

        //when
        auction.updateMinPrice(newMinPrice);

        //then
        assertThat(auction.getMinPrice()).isEqualTo(newMinPrice);
        assertThat(auction.getMaxPrice()).isEqualTo(newMinPrice);
    }

    @Test
    @DisplayName("상태 변화")
    public void updateStatus_Success(){
        //given
        AuctionStatus newStatus = AuctionStatus.ONGOING;

        //when
        auction.updateStatus(newStatus);

        //then
        assertThat(auction.getStatus()).isEqualTo(newStatus);
    }
}