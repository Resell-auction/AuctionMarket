package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;

import org.opensearch.common.unit.Fuzziness;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionOpenSearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private static final String INDEX_NAME = "auctions";

    public void save(AuctionDocument auction) throws IOException {
        IndexRequest request = new IndexRequest(INDEX_NAME)
                .id(String.valueOf(auction.getId()))
                .source(objectMapper.writeValueAsString(auction), XContentType.JSON);

        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    public List<AuctionDocument> search(String keyword, String category, int page, int size) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if(keyword != null && !keyword.isBlank()){
            boolQuery.should(QueryBuilders.matchPhrasePrefixQuery("productName", keyword))
                    .should(QueryBuilders.fuzzyQuery("productName", keyword).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        if(category != null && !category.isBlank()){
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }

        int from = (page - 1) * size;

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(boolQuery)
                .from(from)
                .size(size);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME).source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<AuctionDocument> result = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits().getHits()) {
            AuctionDocument auction = objectMapper.readValue(hit.getSourceAsString(), AuctionDocument.class);
            result.add(auction);
        }

        return result;
    }
}
