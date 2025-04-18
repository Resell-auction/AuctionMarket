package com.example.auctionmarket.domain.auction.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.auction.dto.request.AuctionIncreasePriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionIncreasePriceResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionPageResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    //경매 생성
    @PostMapping
    public ResponseEntity<AuctionSaveResponse> createAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody AuctionSaveRequest request
            ){
        AuctionSaveResponse auctionSaveResponse = auctionService.createAuction(authUser, request);
        return ResponseEntity.ok(auctionSaveResponse);
    }

    //경매 전체 조회
    @GetMapping
    public ResponseEntity<AuctionPageResponse> getAuctions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        AuctionPageResponse result = auctionService.getAuctionsRedis(page, size);

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
                keyword, category, /*authUser,*/ page, size
        );

        return ResponseEntity.ok(result);
    }

    //경매 참여
    @PatchMapping("/{auctionId}/participation")
    public ResponseEntity<AuctionIncreasePriceResponse> increaseAuction(
            @PathVariable Long auctionId,
            @RequestBody AuctionIncreasePriceRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ){
        AuctionIncreasePriceResponse response = auctionService.increasePrice(authUser, auctionId, request.getIncreasePrice());

        return ResponseEntity.ok(response);
    }

    //경매 수정(시작 시간)
    @PatchMapping("/{auctionId}/update-starttime")
    public ResponseEntity<AuctionResponse> updateAuctionStartTime(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionUpdateTimeRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ){
        AuctionResponse response = auctionService.updateAuctionStartTime(authUser, auctionId, request);

        return ResponseEntity.ok(response);
    }

    //경매 수정(초기 가격)
    @PatchMapping("/{auctionId}/update-minprice")
    public ResponseEntity<AuctionResponse> updateMinPrice(
            @PathVariable Long auctionId,
            @RequestBody AuctionUpdateMinPriceRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ){
        AuctionResponse response = auctionService.updateMinPrice(authUser, auctionId, request);

        return ResponseEntity.ok(response);
    }

    //경매 삭제
    @DeleteMapping("/{auctionId}")
    public void deleteAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long auctionId
    ){
        auctionService.deleteAuction(authUser, auctionId);
    }
}
