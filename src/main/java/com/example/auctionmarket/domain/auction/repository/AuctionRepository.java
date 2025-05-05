package com.example.auctionmarket.domain.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auctionmarket.domain.auction.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
