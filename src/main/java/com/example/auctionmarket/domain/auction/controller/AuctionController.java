package com.example.auctionmarket.domain.auction.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.auction.dto.request.AuctionEndRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionPageResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionJoinResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    //경매 생성
    @PostMapping
    public Response<AuctionSaveResponse> createAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody AuctionSaveRequest request
            ){
        AuctionSaveResponse auctionSaveResponse = auctionService.createAuction(authUser, request);
        return Response.of(auctionSaveResponse);
    }

    //경매 전체 조회
    @GetMapping
    public Response<AuctionPageResponse> getAuctions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        AuctionPageResponse result = auctionService.getAuctionsRedis(page, size);

        return Response.of(result);
    }

    // 경매 참여
    @PostMapping("/{auctionId}/join")
    public Response<AuctionJoinResponse> joinAuction(@PathVariable Long auctionId, @AuthenticationPrincipal AuthUser authUser){
        AuctionJoinResponse response = auctionService.join(authUser, auctionId);
        return Response.of(response);
    }

    //경매 수정(시작 시간)
    @PatchMapping("/{auctionId}/update-starttime")
    public Response<AuctionResponse> updateAuctionStartTime(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionUpdateTimeRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ){
        AuctionResponse response = auctionService.updateAuctionStartTime(authUser, auctionId, request);

        return Response.of(response);
    }

    //경매 수정(초기 가격)
    @PatchMapping("/{auctionId}/update-minprice")
    public Response<AuctionResponse> updateMinPrice(
            @PathVariable Long auctionId,
            @RequestBody AuctionUpdateMinPriceRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ){
        AuctionResponse response = auctionService.updateMinPrice(authUser, auctionId, request);

        return Response.of(response);
    }

    //경매 삭제
    @DeleteMapping("/{auctionId}")
    public void deleteAuction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long auctionId
    ){
        auctionService.deleteAuction(authUser, auctionId);
    }

    @PostMapping("/end")
    public void handleAuctionEnd(@RequestBody AuctionEndRequest request) {
        auctionService.endAuction(request.getAuctionId());
    }
}
