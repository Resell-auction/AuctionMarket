package com.example.auctionmarket.domain.auction.document;

import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
