package com.example.auctionmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling // @Scheduled 사용을 위한 어노테이션
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) //page 직렬화를 위한 어노테이션
//@EnableElasticsearchRepositories(basePackages = "com.example.auctionmarket.domain.auction.repository")
public class AuctionMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionMarketApplication.class, args);
	}

}
