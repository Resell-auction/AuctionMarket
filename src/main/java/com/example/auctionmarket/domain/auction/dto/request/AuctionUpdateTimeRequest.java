package com.example.auctionmarket.domain.auction.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionUpdateTimeRequest {

    //수정 시간
    @NotNull
    @FutureOrPresent(message = "수정할 시간은 현재 또는 미래 시간이어야 합니다.")
    private LocalDateTime updateTime;
}
