package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionIncreasePriceResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.event.AuctionEndEvent;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionException;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.payment.service.PaymentService;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.exception.UserNotFoundException;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuctionSaveResponse createAuction(AuthUser authUser, AuctionSaveRequest request){
        //유저 예외처리
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new UserNotFoundException());

        //입력한 상품이 없는 경우 예외처리
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND));

        //물품을 올린 사용자가 아닌 경우 불가
        if(!Objects.equals(authUser.getId(), product.getUser().getId())){
            throw new AuctionException(AuctionErrorCode.NOT_AUCTION_OWNER);
        }

        //경매 내용 저장(최소 가격과 경매 진행 시간)
        Auction auction = new Auction(
                product,
                request.getMinPrice(),
                request.getStartTime(),
                request.getProgressTime()
        );

        Auction saveAuction = auctionRepository.save(auction);

        //저장한 경매 출력
        return new AuctionSaveResponse(
                saveAuction.getId(),
                saveAuction.getProduct().getId(),
                saveAuction.getProduct().getUser().getId(),
                saveAuction.getProduct().getProductName(),
                saveAuction.getProduct().getCategory(),
                saveAuction.getMinPrice(),
                saveAuction.getStartTime(),
                saveAuction.getEndTime(),
                saveAuction.getStatus()
        );
    }

    //경매 전체 조회
    @Transactional
    public Page<AuctionResponse> getAuctions(int page, int size){
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        return auctions.map(auction->{

            String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

            return new AuctionResponse(
                    auction.getId(),
                    auction.getProduct().getId(),
                    auction.getProduct().getUser().getId(),
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
    @Transactional
    public Page<AuctionResponse> SearchAuctions(
        String keyword,
        String category,
//        AuthUser authUser,
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
                    auction.getProduct().getUser().getId(),
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
                .orElseThrow(()->new AuctionException(AuctionErrorCode.AUCTION_NOT_FOUND));

        //경매가 진행 중인 상황이 아니라면 예외 처리
        if(auction.getStatus() != AuctionStatus.ONGOING){
            throw new AuctionException(AuctionErrorCode.AUCTION_NOT_STARTED);
        }

        //유저 예외처리
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new UserNotFoundException());

        //물품을 올린 사용자인 경우 불가
        if(Objects.equals(authUser.getId(), auction.getProduct().getUser().getId())){
            throw new AuctionException(AuctionErrorCode.SELF_BID_NOT_ALLOWED);
        }

        auction.increaseMaxPrice(authUser.getId(), increasedPrice);

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
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new UserNotFoundException());

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new AuctionException(AuctionErrorCode.AUCTION_NOT_FOUND));

        //경매를 올린 사용자가 아닌 경우 불가
        if(!Objects.equals(authUser.getId(), auction.getProduct().getUser().getId())){
            throw new AuctionException(AuctionErrorCode.NOT_AUCTION_OWNER);
        }

        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new AuctionException(AuctionErrorCode.AUCTION_ALREADY_STARTED);
        }

        if(auction.getStatus() == AuctionStatus.ENDED){
            throw new AuctionException(AuctionErrorCode.AUCTION_ALREADY_ENDED);
        }

        //입력 받은 수정할 시작 시간 저장
        auction.updateStartTime(request.getUpdateTime());

        String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

        //수정한 후의 경매 내용 출력
        return new AuctionResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getProduct().getUser().getId(),
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
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new UserNotFoundException());

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new AuctionException(AuctionErrorCode.AUCTION_NOT_FOUND));

        //경매를 올린 사용자가 아닌 경우 불가
        if(!Objects.equals(authUser.getId(), auction.getProduct().getUser().getId())){
            throw new AuctionException(AuctionErrorCode.NOT_AUCTION_OWNER);
        }

        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new AuctionException(AuctionErrorCode.AUCTION_ALREADY_STARTED);
        }

        if(auction.getStatus() == AuctionStatus.ENDED){
            throw new AuctionException(AuctionErrorCode.AUCTION_ALREADY_ENDED);
        }

        //입력 받은 최소가 저장
        auction.updateMinPrice(request.getMinPrice());

        String auctionMessage = remainingTimeOfAuctionStatus(auction.getStatus(), auction.getEndTime());

        //수정한 후의 경매 내용 출력
        return new AuctionResponse(
                auction.getId(),
                auction.getProduct().getId(),
                auction.getProduct().getUser().getId(),
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
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new UserNotFoundException());

        //경매 예외처리
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->new AuctionException(AuctionErrorCode.AUCTION_NOT_FOUND));

        //경매를 올린 사용자가 아닌 경우 불가
        if(!Objects.equals(authUser.getId(), auction.getProduct().getUser().getId())){
            throw new AuctionException(AuctionErrorCode.NOT_AUCTION_OWNER);
        }

        //경매 상태 확인
        if(auction.getStatus() == AuctionStatus.ONGOING){
            throw new AuctionException(AuctionErrorCode.AUCTION_ALREADY_STARTED);
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
    @Scheduled(cron = "0 * * * * *")
    public void updateStatus(){

        List<Auction> auctions = auctionRepository.findAll();

        for(Auction auction : auctions){
            if(LocalDateTime.now().isAfter(auction.getStartTime()) && LocalDateTime.now().isBefore(auction.getEndTime())){
                auction.setStatus(AuctionStatus.ONGOING);
            }

            else if (LocalDateTime.now().isAfter(auction.getEndTime())) {
                auction.setStatus(AuctionStatus.ENDED);

                if (auction.getConsumerId() != null) {
                    paymentService.createPayment(auction.getId());

                    // 나중에 동기 비동기시 변형해서 사용
//                    eventPublisher.publishEvent(new AuctionEndEvent(
//                            auction.getId(),
//                            auction.getConsumerId(),
//                            auction.getMaxPrice()
//                    ));
//                    paymentService.createPayment(auction);
                }
            }

            else if(LocalDateTime.now().isBefore(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.PENDING);
            }
        }
    }
}
