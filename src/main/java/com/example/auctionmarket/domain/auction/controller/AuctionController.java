package com.example.auctionmarket.domain.auction.controller;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    //경매 생성
    @PostMapping("/{productId}")
    public ResponseEntity<AuctionResponse> createAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @RequestBody AuctionSaveRequest request
            ){
        AuctionResponse auctionResponse = auctionService.createAuction(authUser, productId, request);
        return ResponseEntity.ok(auctionResponse);
    }

    //경매 전체 조회
    @GetMapping
    public ResponseEntity<Page<AuctionResponse>> getAuctions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<AuctionResponse> result = auctionService.getAuctions(page, size);
        return ResponseEntity.ok(result);
    }

    //경매 검색
    @GetMapping("/search")
    public ResponseEntity<Page<AuctionResponse>> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser
    ){

        Page<AuctionResponse> result = auctionService.SearchAuctions(
                keyword, category, page, size
        );

        return ResponseEntity.ok(result);
    }

    //경매 삭제
    @DeleteMapping("/{auctionId}/product/{productId}")
    public void deleteAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long auctionId,
            @PathVariable Long productId
    ){
        auctionService.deleteAuction(authUser, auctionId, productId);
    }
}
