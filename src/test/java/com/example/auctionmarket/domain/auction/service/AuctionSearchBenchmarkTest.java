package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.mapper.AuctionMapper;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.auction.repository.AuctionSearchRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
public class AuctionSearchBenchmarkTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionSearchRepository auctionSearchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionSearchService auctionSearchService;

    @BeforeEach
    void cleanUp(){
        auctionRepository.deleteAll();
        auctionSearchRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @Transactional
    public void generateDummyAuctions(){
        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(()-> new RuntimeException("No user found"));

        for(int i=0; i<10000; i++){

            Product product = new Product(
                    user,
                    "Test Product "+i,
                    "content",
                    ProductCategory.SHOES
            );
            productRepository.save(product);

            Auction auction = new Auction(
                    product,
                    1000L + i,
                    LocalDateTime.now().plusHours(i),
                    60L
            );
            auctionRepository.save(auction);
            auctionSearchRepository.save(AuctionMapper.toDucument(auction));
        }

        System.out.println("10000건의 더미 경매가 생성되었습니다.");
    }

    @Test
    public void benchmarkSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "testproduct";
        String category = "SHOES";

        //QueryDSL을 사용한 검색 기능
        long startTime1 = System.currentTimeMillis();
        auctionRepository.findBySearch(keyword, category, pageable);
        long endTime1 = System.currentTimeMillis();
        System.out.println("QueryDSL 검색 소요 시간: "+(endTime1-startTime1)+"ms");

        //Elastic Search를 사용한 검색 기능
        long startTime2 = System.currentTimeMillis();
        auctionSearchService.searchAuctions(keyword, category, pageable);
        long endTime2 = System.currentTimeMillis();
        System.out.println("Elastic Search 검색 소요 시간: "+(endTime2-startTime2)+"ms");
    }
}
