package com.example.auctionmarket.domain.auction.enums;

public enum AuctionStatus {
    PENDING("경매 시작 전 대기"),
    ONGOING("경매 진행 중"),
    ENDED("경매 종료");

    private final String description;

    AuctionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
