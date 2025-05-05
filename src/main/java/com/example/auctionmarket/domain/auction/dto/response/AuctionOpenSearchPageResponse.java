package com.example.auctionmarket.domain.auction.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionOpenSearchPageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> content;
}
