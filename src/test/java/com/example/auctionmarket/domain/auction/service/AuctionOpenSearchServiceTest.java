package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.example.auctionmarket.domain.auction.dto.response.AuctionOpenSearchPageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.TotalHits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionOpenSearchServiceTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuctionOpenSearchService auctionOpenSearchService;

    private AuctionDocument auctionDocument;

    @BeforeEach
    void setUp() {
        auctionDocument = AuctionDocument.builder()
                .id(1L)
                .productName("테스트상품")
                .category("BOOK")
                .minPrice(10000L)
                .startTime(LocalDateTime.now().toString())
                .endTime(LocalDateTime.now().plusHours(2).toString())
                .build();
    }

    @Test
    public void openSearch저장_성공() throws Exception {
        // given
        String json = "{\"id\":1,\"productName\":\"테스트상품\"}";
        given(objectMapper.writeValueAsString(auctionDocument)).willReturn(json);

        // when
        auctionOpenSearchService.save(auctionDocument);

        // then
        verify(restHighLevelClient, times(1)).index(any(IndexRequest.class), eq(RequestOptions.DEFAULT));
    }

    @Test
    public void openSearch저장_JSON직렬화_실패() throws Exception {
        // given
        given(objectMapper.writeValueAsString(auctionDocument)).willThrow(JsonProcessingException.class);

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            auctionOpenSearchService.save(auctionDocument);
        });

        assertTrue(ex.getMessage().contains("JSON 직렬화 실패"));
    }

    @Test
    public void openSearch저장_인덱싱_실패() throws Exception {
        // given
        String json = "{}";
        given(objectMapper.writeValueAsString(auctionDocument)).willReturn(json);
        doThrow(IOException.class).when(restHighLevelClient).index(any(IndexRequest.class), eq(RequestOptions.DEFAULT));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            auctionOpenSearchService.save(auctionDocument);
        });

        assertTrue(ex.getMessage().contains("OpenSearch 인덱싱 실패"));
    }

    @Test
    public void 검색_성공() throws Exception {
        // given
        SearchHit hit = mock(SearchHit.class);
        when(hit.getSourceAsString()).thenReturn("{\"id\":1,\"productName\":\"테스트상품\"}");

        SearchHits hits = new SearchHits(new SearchHit[]{hit}, new TotalHits(1L, TotalHits.Relation.EQUAL_TO), 1.0f);
        SearchResponse response = mock(SearchResponse.class);
        when(response.getHits()).thenReturn(hits);

        SearchRequest anyRequest = any(SearchRequest.class);
        when(restHighLevelClient.search(anyRequest, eq(RequestOptions.DEFAULT))).thenReturn(response);

        AuctionDocument parsedDoc = AuctionDocument.builder().id(1L).productName("테스트상품").build();
        when(objectMapper.readValue(anyString(), eq(AuctionDocument.class))).thenReturn(parsedDoc);

        // when
        AuctionOpenSearchPageResponse<AuctionDocument> result = auctionOpenSearchService.search("테스트", null, 1, 10);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("테스트상품", result.getContent().get(0).getProductName());
    }

}
