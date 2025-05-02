package com.example.auctionmarket.domain.auction.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Document(indexName = "auctions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionDocument {

    @Id
    private Long id;

    private String productName;
    private String category;
    private Long minPrice;
    private String startTime;
    private String endTime;
}
