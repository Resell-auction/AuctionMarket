package com.example.auctionmarket.domain.auction.mapper;

import java.time.format.DateTimeFormatter;

import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.example.auctionmarket.domain.auction.entity.Auction;

public class AuctionMapper {
    public static AuctionDocument toDucument(Auction auction) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return AuctionDocument.builder()
                .id(auction.getId())
                .productName(auction.getProduct().getProductName())
                .category(auction.getProduct().getCategory().name())
                .minPrice(auction.getMinPrice())
                .startTime(auction.getStartTime().format(formatter))
                .endTime(auction.getEndTime().format(formatter))
                .build();
    }
}
