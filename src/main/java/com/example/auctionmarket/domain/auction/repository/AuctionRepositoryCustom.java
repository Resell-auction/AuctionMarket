package com.example.auctionmarket.domain.auction.repository;

import com.example.auctionmarket.domain.auction.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryCustom {
    Page<Auction> findBySearch(
            String keyword,
            String category,
            Pageable pageable
    );
}
