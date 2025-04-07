package com.example.auctionmarket.domain.auction.repository;

import com.example.auctionmarket.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {

}
