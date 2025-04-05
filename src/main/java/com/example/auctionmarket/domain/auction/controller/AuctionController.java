package com.example.auctionmarket.domain.auction.controller;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.auction.dto.request.AuctionIncreasePriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionIncreasePriceResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
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
    public ResponseEntity<AuctionSaveResponse> createAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @RequestBody AuctionSaveRequest request
            ){
        AuctionSaveResponse auctionSaveResponse = auctionService.createAuction(authUser, productId, request);
        return ResponseEntity.ok(auctionSaveResponse);
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

    //경매 참여
    @PutMapping("/{auctionId}/auction")
    public ResponseEntity<AuctionIncreasePriceResponse> increaseAuction(
            @PathVariable Long auctionId,
            @RequestBody AuctionIncreasePriceRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ){
        AuctionIncreasePriceResponse response = auctionService.increasePrice(authUser, auctionId, request.getIncreasePrice());

        return ResponseEntity.ok(response);
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
