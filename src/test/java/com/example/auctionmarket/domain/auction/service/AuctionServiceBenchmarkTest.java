package com.example.auctionmarket.domain.auction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuctionServiceBenchmarkTest {

    @Autowired
    private AuctionService auctionService;

//    private final String keyword = "test";
//    private final String category = "SHOES";
//    private final int page = 1;
//    private final int size = 10;

    /*조회 기능 성능 테스트*/
    @Test
    public void benchmarkGetAuctionsRedis(){
        int repeat = 100;
        long start = System.currentTimeMillis();

        for(int i=0;i<repeat;i++){
            auctionService.getAuctionsRedis(1,10);
        }

        long end = System.currentTimeMillis();
        System.out.println("[Redis] 평균 응답 시간: " + (end-start)/(double) repeat + "ms");
    }

    @Test
    public void benchmarkGetAuctionsCaffeine(){
        int repeat = 100;
        long start = System.currentTimeMillis();

        for(int i=0;i<repeat;i++){
       //     auctionService.getAuctionsCaffeine(1,10);
        }

        long end = System.currentTimeMillis();
        System.out.println("[Caffeine] 평균 응답 시간: " + (end-start)/(double) repeat + "ms");
    }

    @Test
    public void benchmarkGetAuctionsWithoutCache(){
        int repeat = 100;
        long start = System.currentTimeMillis();

        for(int i=0;i<repeat;i++){
        //    auctionService.getAuctions(1,10);
        }

        long end = System.currentTimeMillis();
        System.out.println("[No Cache] 평균 응답 시간: " + (end-start)/(double) repeat + "ms");
    }

//    /*검색 기능 성능 비교*/
//    @Test
//    public void benchmarkSearchAuctionsRedis(){
//        int repeat = 100;
//        long start = System.currentTimeMillis();
//
//        for(int i=0;i<repeat;i++){
//            auctionService.searchAuctionsRedis(keyword,category,page,size);
//        }
//
//        long end = System.currentTimeMillis();
//        System.out.printf("[Redis::Search] 평균 응답 시간: %.2fms\n", (end - start) / (double) repeat);
//    }
//
//    @Test
//    public void benchmarkSearchAuctionsCaffeine(){
//        int repeat = 100;
//        long start = System.currentTimeMillis();
//
//        for(int i=0;i<repeat;i++){
//            auctionService.searchAuctionsCaffeine(keyword,category,page,size);
//        }
//
//        long end = System.currentTimeMillis();
//        System.out.printf("[Caffeine::Search] 평균 응답 시간: %.2fms\n", (end - start) / (double) repeat);
//    }
//
//    @Test
//    public void benchmarkSearchAuctionsNoCache(){
//        int repeat = 100;
//        long start = System.currentTimeMillis();
//
//        for(int i=0;i<repeat;i++){
//            auctionService.SearchAuctions(keyword,category,page,size);
//        }
//
//        long end = System.currentTimeMillis();
//        System.out.printf("[No Cache::Search] 평균 응답 시간: %.2fms\n", (end - start) / (double) repeat);
//    }
}
