package com.example.auctionmarket.domain.auction.controller;

import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.example.auctionmarket.domain.auction.dto.response.AuctionOpenSearchPageResponse;
import com.example.auctionmarket.domain.auction.service.AuctionOpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v3/auctions")
public class AuctionOpenSearchController {

    private final AuctionOpenSearchService auctionOpenSearchService;

    //opensearch 경매 검색 기능
    @GetMapping("/search")
    public AuctionOpenSearchPageResponse<AuctionDocument> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws IOException {
        return auctionOpenSearchService.search(keyword, category, page, size);
    }
}
