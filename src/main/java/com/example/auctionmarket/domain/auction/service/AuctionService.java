package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Transactional
    public AuctionResponse createAuction(AuthUser authUser, Long productId, AuctionSaveRequest request){
        //로그인한 유저가 아닐 경우 예외처리
        User currentUser = userRepository.findById(authUser.getId())
                .orElseThrow(()->new /*예외처리 들어갈 곳*/);

        //상품에 입력한 상품이 없는 경우 예외처리
        Product product = productRepository.findById(productId)
                .orElseThrow(()->/*예외처리*/);

        //경매 내용 저장(최소 가격과 경매 진행 시간)
        Auction auction = Auction.of(
                request.getMinPrice(),
                request.getProgressTime()
        );

        return new AuctionResponse(
                auction.getId(),
                productId,
                product.getProductName(),
                product.getCategory(),
                auction.getMinPrice(),
                auction.getDuration()
        );
    }

    //경매 전체 조회
    @Transactional(readOnly = true)
    public Page<AuctionResponse> getAuctions(int page, int size){
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        return auctions.map(auction->{
            return new AuctionResponse(
                    auction.getId(),
                    productId,
                    product.getProductName(),
                    product.getCategory(),
                    auction.getMinPrice(),
                    auction.getDuration()
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
            return new AuctionResponse(
                    auction.getId(),
                    product.getId,
                    product.getProductName(),
                    product.getCategory(),
                    auction.getMinPrice(),
                    auction.getDuration()
            );
        });
    }

    //경매 삭제
    @Transactional
    public void deleteAuction(AuthUser authUser, Long auctionId, Long productId){
        //로그인 유저인지 확인
        User user = User.fromAuthUser(authUser);

        //해당 물품이 존재하는지 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(()->/*예외처리 코드*/);

        //해당 경매가 존재하는지 확인
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(()->/*예외처리 코드*/);
    }

}
