package com.example.auctionmarket.domain.auction.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionSaveRequest {

    private Long productId;

    private Long minPrice;

    //시작 시간
    @NotNull
    @FutureOrPresent(message = "시작 시간은 현재 또는 미래 시간이어야 합니다.")
    private LocalDateTime startTime;

    //분 단위로 입력 받는 경매 지속 시간
    @Min(value = 1, message = "경매 지속 시간은 최소 1분 이상이여야 합니다.")
    private Long progressTime;


}
