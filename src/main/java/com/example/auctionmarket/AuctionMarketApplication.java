package com.example.auctionmarket;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableElasticsearchRepositories(basePackages = "com.example.auctionmarket.domain.auction.repository")
public class AuctionMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionMarketApplication.class, args);
	}
}
