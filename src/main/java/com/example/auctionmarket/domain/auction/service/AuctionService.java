package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionIncreasePriceResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public AuctionSaveResponse createAuction(AuthUser authUser, Long productId, AuctionSaveRequest request){
//        //로그인한 유저가 아닐 경우 예외처리
//        User currentUser = userRepository.findById(authUser.getId())
//                .orElseThrow(()->new /*예외처리 들어갈 곳*/);

        //입력한 상품이 없는 경우 예외처리
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new IllegalStateException("해당 제품은 존재하지 않습니다."));

        //경매 내용 저장(최소 가격과 경매 진행 시간)
        Auction auction = Auction.of(
                product,
                request.getMinPrice(),
                request.getStartTime(),
                request.getProgressTime()
        );

        //저장한 경매 출력
        return new AuctionSaveResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getProduct().getUserId(),
                auction.getProduct().getProductName(),
                auction.getProduct().getCategory(),
                auction.getMinPrice(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus()
        );
    }

    //경매 전체 조회
    @Transactional(readOnly = true)
    public Page<AuctionResponse> getAuctions(int page, int size){
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        return auctions.map(auction->{

            String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

            return new AuctionResponse(
                    auction.getId(),
                    auction.getProduct().getId(),
                    auction.getProduct().getUserId(),
                    auction.getProduct().getProductName(),
                    auction.getProduct().getCategory(),
                    auction.getMinPrice(),
                    auction.getMaxPrice(),
                    auction.getStartTime(),
                    auction.getEndTime(),
                    auction.getStatus(),
                    auctionMessage
            );
        });
    }

    //경매 조회 기능(검색)
    @Transactional(readOnly = true)
    public Page<AuctionResponse> SearchAuctions(
        String keyword,
        String category,
        int page, int size
    ){
        Pageable pageable = PageRequest.of(page-1, size);


        Page<Auction> auctions = auctionRepository.findBySearch(
                keyword, category, pageable
        );

        return auctions.map(auction->{
            String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

            return new AuctionResponse(
                    auction.getId(),
                    auction.getProduct().getId(),
                    auction.getProduct().getUserId(),
                    auction.getProduct().getProductName(),
                    auction.getProduct().getCategory(),
                    auction.getMinPrice(),
                    auction.getMaxPrice(),
                    auction.getStartTime(),
                    auction.getEndTime(),
                    auction.getStatus(),
                    auctionMessage
            );
        });
    }

    //경매 참여
    @Transactional
    public AuctionIncreasePriceResponse increasePrice(AuthUser authUser, Long auctionId, Long increasedPrice){

        //해당하는 경매 찾기
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new IllegalStateException("해당 경매는 존재하지 않습니다."));

        //경매가 진행 중인 상황이 아니라면 예외 처리
        if(auction.getStatus() != AuctionStatus.ONGOING){
            throw new /*예외처리*/
        }

        //유저 예외처리
        User user = userRepository.findById(authUser.getUserId)
                .orElseThrow(()->new IllegalStateException("해당 유저는 존재하지 않습니다."));

        //물품을 올린 사용자인 경우 불가
        if(authUser.getUserId() == auction.getProduct().getUserId()){
            //에외 처리
        }

        auction.increaseMaxPrice(authUser.getUserId, increasedPrice);

        return new AuctionIncreasePriceResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getConsumerId(),
                auction.getProduct().getProductName(),
                auction.getProduct().getCategory(),
                auction.getMinPrice(),
                auction.getMaxPrice()
        );
    }

    //경매 수정(시작 시간)
    @Transactional
    public AuctionResponse updateAuctionStartTime(AuthUser authUser, Long auctionId, AuctionUpdateTimeRequest request){
        //유저 예외처리
        User user = userRepository.findById(authUser.getUserId)
                .orElseThrow(()->new IllegalStateException("해당 유저는 존재하지 않습니다."));

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new IllegalStateException("해당 경매는 존재하지 않습니다."));

        //경매를 올린 사용자가 아닌 경우 불가
        if(authUser.getUserId() != auction.getProduct().getUserId()){
            //에외 처리
        }

        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new IllegalStateException("경매가 진행 중일 때에는 수정이 불가합니다!");
        }

        //입력 받은 수정할 시작 시간 저장
        auction.updateStartTime(request.getUpdateTime());

        String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

        //수정한 후의 경매 내용 출력
        return new AuctionResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getProduct().getUserId(),
                auction.getProduct().getProductName(),
                auction.getProduct().getCategory(),
                auction.getMinPrice(),
                auction.getMaxPrice(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auctionMessage
        );
    }

    //경매 수정(초기 가격)
    @Transactional
    public AuctionResponse updateMinPrice(AuthUser authUser, Long auctionId, AuctionUpdateMinPriceRequest request){
        User user = userRepository.findById(authUser.getUserId)
                .orElseThrow(()->new IllegalStateException("해당 유저는 존재하지 않습니다."));

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new IllegalStateException("해당 경매는 존재하지 않습니다."));

        //경매를 올린 사용자가 아닌 경우 불가
        if(authUser.getUserId() != auction.getProduct().getUserId()){
            //에외 처리
        }

        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new IllegalStateException("경매가 진행 중일 때에는 수정이 불가합니다!");
        }

        //입력 받은 최소가 저장
        auction.updateMinPrice(request.getMinPrice());

        String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

        //수정한 후의 경매 내용 출력
        return new AuctionResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getProduct().getUserId(),
                auction.getProduct().getProductName(),
                auction.getProduct().getCategory(),
                auction.getMinPrice(),
                auction.getMaxPrice(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auctionMessage
        );
    }

    //경매 삭제
    @Transactional
    public void deleteAuction(AuthUser authUser, Long auctionId){
        User user = userRepository.findById(authUser.getUserId)
                .orElseThrow(()->new IllegalStateException("해당 유저는 존재하지 않습니다."));

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new IllegalStateException("해당 경매는 존재하지 않습니다."));

        //경매를 올린 사용자가 아닌 경우 불가
        if(authUser.getUserId() != auction.getProduct().getUserId()){
            //에외 처리
        }

        //경매 상태 확인
        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new IllegalStateException("대기 중이거나 완료된 경매만 삭제할 수 있습니다!");
        }

        //경매 삭제
        auctionRepository.delete(auction);
    }

    /*함수 구현*/

    //경매 상태에 따른 시간 출력 함수
    @Transactional
    public String remainingTimeOfAuctionStatus(AuctionStatus auctionStatus, LocalDateTime auctionEndTime){
        String auctionMessage = new String();

        //경매 상태에 따른 출력 값 변경
        if(auctionStatus == AuctionStatus.ONGOING){
            //경매 남은 시간 계산
            Duration remaining = Duration.between(LocalDateTime.now(), auctionEndTime);
            auctionMessage = formatDuration(remaining);
        }

        if(auctionStatus == AuctionStatus.PENDING){
            auctionMessage = "경매 진행 전입니다.";
        }

        if(auctionStatus == AuctionStatus.ENDED){
            auctionMessage = "종료된 경매입니다.";
        }

        return auctionMessage;
    }

    //경매 남은 시간 출력 함수
    @Transactional
    public String formatDuration(Duration duration){
        return String.format("%d일 %d시간 %d분",
                duration.toDays(),
                duration.toHoursPart(),
                duration.toMinutesPart());
    }

    //경매 상태 변경 함수
    @Transactional
    public AuctionStatus updateStatus(LocalDateTime startTime, LocalDateTime endTime){
        if(LocalDateTime.now().isBefore(startTime)){
            return AuctionStatus.PENDING;
        }
        else if (LocalDateTime.now().isAfter(endTime)) {
            return AuctionStatus.ENDED;
        }
        else {
            return AuctionStatus.ONGOING;
        }
    }
}
