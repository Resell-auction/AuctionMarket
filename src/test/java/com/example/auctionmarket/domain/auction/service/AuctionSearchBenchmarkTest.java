package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.common.config.S3Config;
import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.example.auctionmarket.domain.auction.dto.response.AuctionOpenSearchPageResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.mapper.AuctionMapper;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
//import com.example.auctionmarket.domain.auction.repository.AuctionSearchRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class AuctionSearchBenchmarkTest {

    @Autowired
    private AuctionRepository auctionRepository;

//    @Autowired
//    private AuctionSearchRepository auctionSearchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private AuctionSearchService auctionSearchService;

    @Autowired
    private AuctionOpenSearchService auctionOpenSearchService;

    @BeforeEach
    void cleanUp(){
        auctionRepository.deleteAll();
//        auctionSearchRepository.deleteAll();
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
//            auctionSearchRepository.save(AuctionMapper.toDucument(auction));

            //OpenSearch 인덱싱 추가
            AuctionDocument document = AuctionDocument.builder()
                    .id(auction.getId())
                    .productName(auction.getProduct().getProductName())
                    .category(auction.getProduct().getCategory().name())
                    .minPrice(auction.getMinPrice())
                    .startTime(auction.getStartTime().toString())
                    .endTime(auction.getEndTime().toString())
                    .build();

            try{
                auctionOpenSearchService.save(document);
            }catch (IOException e){
                System.out.println("OpenSearch 인덱싱 실패: {"+e.getMessage()+"}");
            }
        }

        System.out.println("10000건의 더미 경매가 생성되었습니다.");
    }

    @Test
    public void benchmarkSearch() throws IOException {
        int size=10;
        int page=1;

        Pageable pageable = PageRequest.of(page-1, size);
        String keyword = "testproduct";
        String category = "SHOES";

        //QueryDSL을 사용한 검색 기능
        long startTime1 = System.currentTimeMillis();
        auctionRepository.findBySearch(keyword, category, pageable);
        long endTime1 = System.currentTimeMillis();
        System.out.println("QueryDSL 검색 소요 시간: "+(endTime1-startTime1)+"ms");

        //Elastic Search를 사용한 검색 기능
//        long startTime2 = System.currentTimeMillis();
//        auctionSearchService.searchAuctions(keyword, category, pageable);
//        long endTime2 = System.currentTimeMillis();
//        System.out.println("Elastic Search 검색 소요 시간: "+(endTime2-startTime2)+"ms");

        //OpenSearch를 사용한 검색 기능
        long startTime3 = System.currentTimeMillis();
        AuctionOpenSearchPageResponse<AuctionDocument> result = auctionOpenSearchService.search(keyword, category, page, size);
        long endTime3 = System.currentTimeMillis();
        System.out.println("OpenSearch 검색 소요 시간: "+(endTime3-startTime3)+"ms");
    }
}
