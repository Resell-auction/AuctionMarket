package com.example.auctionmarket.restDocsTest;

import co.elastic.clients.elasticsearch.xpack.usage.Base;
import com.example.auctionmarket.domain.auction.controller.AuctionOpenSearchController;
import com.example.auctionmarket.domain.auction.document.AuctionDocument;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionOpenSearchPageResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.service.AuctionOpenSearchService;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.auctionmarket.domain.product.enums.ProductCategory.CLOTHES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionOpenSearchController.class)
public class AuctionOpenSearchRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private AuctionOpenSearchService auctionOpenSearchService;

    @Test
    void 오픈서치_경매_검색_기능_RestDocsAPI() throws Exception {

        AuctionDocument auctionDocument= new AuctionDocument(1L, "상품1", "BAG", 10000L, "2025-05-05", "2025-05-06");
        AuctionDocument auctionDocument2= new AuctionDocument(2L, "상품2", "SHOSE", 30000L, "2025-05-05", "2025-05-06");

        List<AuctionDocument> result = new ArrayList<>();

        result.add(auctionDocument);
        result.add(auctionDocument2);

        given(auctionOpenSearchService.search(any(), any(),anyInt(),anyInt()))
                .willReturn(new AuctionOpenSearchPageResponse<>(1,2,2,1,result));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v3/auctions/search")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auction/search-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("page").description("현재 페이지 번호"),
                                fieldWithPath("size").description("페이지당 데이터 수"),
                                fieldWithPath("totalElements").description("전체 데이터 수"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("content[].id").description("상품의 고유 ID"),
                                fieldWithPath("content[].productName").description("상품 이름"),
                                fieldWithPath("content[].category").description("상품 카테고리"),
                                fieldWithPath("content[].minPrice").description("시작 가격"),
                                fieldWithPath("content[].startTime").description("경매 시작일"),
                                fieldWithPath("content[].endTime").description("경매 종료일")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 시간 연장")
                        )
                ));
    }


}
