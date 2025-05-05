package com.example.auctionmarket.domain.auction.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPageResponse implements Serializable {
    private List<AuctionResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
