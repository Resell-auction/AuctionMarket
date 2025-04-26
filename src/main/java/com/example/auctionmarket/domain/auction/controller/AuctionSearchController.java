package com.example.auctionmarket.domain.auction.controller;

//import com.example.auctionmarket.domain.auction.document.AuctionDocument;
//import com.example.auctionmarket.domain.auction.service.AuctionSearchService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v2")
//public class AuctionSearchController {
//
//    private final AuctionSearchService auctionSearchService;
//
//    @GetMapping("/auctions/search")
//    public Page<AuctionDocument> searchAuctions(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String category,
//            @PageableDefault(size = 10) Pageable pageable
//            ){
//        return auctionSearchService.searchAuctions(keyword, category, pageable);
//    }
//}
