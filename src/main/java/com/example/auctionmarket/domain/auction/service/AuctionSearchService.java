package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.document.AuctionDocument;
//import com.example.auctionmarket.domain.auction.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

//@RequiredArgsConstructor
//@Service
//public class AuctionSearchService {
//
//    private final AuctionSearchRepository auctionSearchRepository;
//
//    public Page<AuctionDocument> searchAuctions(String keyword, String category, Pageable pageable) {
//        Page<AuctionDocument> result = auctionSearchRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
//
//        //카테고리 필터
//        if(StringUtils.hasText(category)) {
//            List<AuctionDocument> filtered = result.getContent().stream()
//                    .filter(doc ->doc.getCategory().equalsIgnoreCase(category))
//                    .toList();
//
//            return new PageImpl<>(filtered, pageable, filtered.size());
//        }
//
//        return result;
//    }
//}
