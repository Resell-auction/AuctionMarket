package com.example.auctionmarket.restDocsTest;


import com.example.auctionmarket.domain.auction.controller.AuctionController;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.service.AuctionService;
import com.example.auctionmarket.domain.product.enums.ProductCategory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
public class AuctionRestDocsTest extends BaseRestDocsTest {

    @MockBean
    private AuctionService auctionService;

    @Test
    void 경매_생성_RestDocsAPI() throws Exception {
        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionSaveRequest auctionSaveRequest = new AuctionSaveRequest(1L, 1000L, auctionTime, 100L);

        given(auctionService.createAuction(any(), any()))
                .willReturn(new AuctionSaveResponse(
                        1L, 1L, 1L, "productName", ProductCategory.ACCESSORY, 1000L, auctionTime, auctionTime, AuctionStatus.PENDING,"url")
                );

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auctions")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auctionSaveRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("create-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("경매 ID"),
                                fieldWithPath("productId").description("경매에 등록된 상품 ID"),
                                fieldWithPath("userId").description("경매를 생성한 사용자 ID"),
                                fieldWithPath("productName").description("경매에 등록된 상품 이름"),
                                fieldWithPath("category").description("경매에 등록된 상품 카테고리"),
                                fieldWithPath("minPrice").description("경매 시작 최소 가격"),
                                fieldWithPath("startTime").description("경매 시작 시간"),
                                fieldWithPath("endTime").description("경매 종료 시간"),
                                fieldWithPath("status").description("경매 상태")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 시간 연장")
                        )
                ));
    }

    @Test
    void 경매_전체_조회_RestDocsAPI() throws Exception {
        // GIVEN
        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionResponse auctionResponse = new AuctionResponse(1L, 1L, 1L, "product1", ProductCategory.ACCESSORY, 1000L, 10000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");
        Page<AuctionResponse> result = new PageImpl<>(List.of(auctionResponse, auctionResponse));

        given(auctionService.getAuctions(1, 10))
                .willReturn(result);

        // WHEN + THEN
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/auctions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-auctions",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("content[].id").description("경매 ID"),
                                fieldWithPath("content[].productId").description("경매 상품 ID"),
                                fieldWithPath("content[].userId").description("경매를 생성한 사용자 ID"),
                                fieldWithPath("content[].productName").description("경매 상품 이름"),
                                fieldWithPath("content[].category").description("경매 상품 카테고리"),
                                fieldWithPath("content[].minPrice").description("경매 시작 최소 가격"),
                                fieldWithPath("content[].maxPrice").description("경매 낙찰 최대 가격"),
                                fieldWithPath("content[].startTime").description("경매 시작 시간"),
                                fieldWithPath("content[].endTime").description("경매 종료 시간"),
                                fieldWithPath("content[].status").description("경매 상태"),
                                fieldWithPath("content[].remainingTime").description("경매 종료까지 남은 시간"),
                                fieldWithPath("pageable").description("페이지 정보"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 데이터 수"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("first").description("첫 페이지 여부"),
                                fieldWithPath("size").description("요청한 페이지 크기"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort").description("정렬 정보"),
                                fieldWithPath("sort.empty").description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬되었는지 여부"),
                                fieldWithPath("sort.unsorted").description("정렬되지 않았는지 여부"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지 데이터")

                        )));
    }

    @Test
    void 경매_검색_RestDocsAPI() throws Exception {
        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionResponse auctionResponse = new AuctionResponse(1L, 1L, 1L, "product1", ProductCategory.ACCESSORY, 1000L, 10000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");
        Page<AuctionResponse> result = new PageImpl<>(List.of(auctionResponse, auctionResponse));

        given(auctionService.SearchAuctions(anyString(), anyString(), anyInt(), anyInt()))
                .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/auctions/search")
                        .param("keyword", "adidas")
                        .param("category", "ACCESSORY")
                        .param("page", "1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-auctions",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("content[].id").description("경매 ID"),
                                fieldWithPath("content[].productId").description("경매 상품 ID"),
                                fieldWithPath("content[].userId").description("경매를 생성한 사용자 ID"),
                                fieldWithPath("content[].productName").description("경매 상품 이름"),
                                fieldWithPath("content[].category").description("경매 상품 카테고리"),
                                fieldWithPath("content[].minPrice").description("경매 시작 최소 가격"),
                                fieldWithPath("content[].maxPrice").description("경매 낙찰 최대 가격"),
                                fieldWithPath("content[].startTime").description("경매 시작 시간"),
                                fieldWithPath("content[].endTime").description("경매 종료 시간"),
                                fieldWithPath("content[].status").description("경매 상태"),
                                fieldWithPath("content[].remainingTime").description("경매 종료까지 남은 시간"),
                                fieldWithPath("pageable").description("페이지 정보"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 데이터 수"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("first").description("첫 페이지 여부"),
                                fieldWithPath("size").description("요청한 페이지 크기"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort").description("정렬 정보"),
                                fieldWithPath("sort.empty").description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬되었는지 여부"),
                                fieldWithPath("sort.unsorted").description("정렬되지 않았는지 여부"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지 데이터")
                        )));
    }
//
//    @Test
//    void 경매_참여_RestDocsAPI() throws Exception {
//
//        AuctionIncreasePriceRequest request = new AuctionIncreasePriceRequest(1000L);
//        AuctionIncreasePriceResponse response = new AuctionIncreasePriceResponse(1L, 1L, 1L, "productName", ProductCategory.ACCESSORY, 1000L, 10000L);
//
//        given(auctionService.increasePrice(any(), any(), any()))
//                .willReturn(response);
//
//        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/auctions/{auctionId}/participation", 1L)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("participate-auction",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        responseFields(
//                                fieldWithPath("id").description("경매 ID"),
//                                fieldWithPath("productId").description("상품 ID"),
//                                fieldWithPath("consumerId").description("구매자 ID"),
//                                fieldWithPath("productName").description("상품명"),
//                                fieldWithPath("category").description("상품 카테고리"),
//                                fieldWithPath("minPrice").description("시작 가격"),
//                                fieldWithPath("maxPrice").description("즉시 구매 가격")
//                        )
//                ));
//    }

    @Test
    void 경매_시간_수정_RestDocsAPI() throws Exception {
        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionUpdateTimeRequest updateTimeRequest = new AuctionUpdateTimeRequest(auctionTime);
        AuctionResponse auctionResponse = new AuctionResponse(1L, 1L, 1L, "product1", ProductCategory.ACCESSORY, 1000L, 10000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");

        given(auctionService.updateAuctionStartTime(any(), anyLong(), any()))
                .willReturn(auctionResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/auctions/{auctionId}/update-starttime", 1L)
                        .content(objectMapper.writeValueAsString(updateTimeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("updatetime-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("경매 ID"),
                                fieldWithPath("productId").description("상품 ID"),
                                fieldWithPath("userId").description("구매자 ID"),
                                fieldWithPath("productName").description("상품명"),
                                fieldWithPath("category").description("상품 카테고리"),
                                fieldWithPath("minPrice").description("시작 가격"),
                                fieldWithPath("maxPrice").description("즉시 구매 가격"),
                                fieldWithPath("startTime").description("경매 시작 시간"),
                                fieldWithPath("endTime").description("경매 종료 시간"),
                                fieldWithPath("status").description("경매 상태"),
                                fieldWithPath("remainingTime").description("남은 시간")
                        )
                ));
    }

    @Test
    void 경매_가격_수정_RestDocsAPI() throws Exception {
        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionUpdateMinPriceRequest minPriceRequest = new AuctionUpdateMinPriceRequest(1000L);
        AuctionResponse auctionResponse = new AuctionResponse(1L, 1L, 1L, "product1", ProductCategory.ACCESSORY, 1000L, 10000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");

        given(auctionService.updateMinPrice(any(), anyLong(), any()))
                .willReturn(auctionResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/auctions/{auctionId}/update-minprice", 1L)
                        .content(objectMapper.writeValueAsString(minPriceRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("updateprice-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("경매 ID"),
                                fieldWithPath("productId").description("상품 ID"),
                                fieldWithPath("userId").description("구매자 ID"),
                                fieldWithPath("productName").description("상품명"),
                                fieldWithPath("category").description("상품 카테고리"),
                                fieldWithPath("minPrice").description("시작 가격"),
                                fieldWithPath("maxPrice").description("즉시 구매 가격"),
                                fieldWithPath("startTime").description("경매 시작 시간"),
                                fieldWithPath("endTime").description("경매 종료 시간"),
                                fieldWithPath("status").description("경매 상태"),
                                fieldWithPath("remainingTime").description("남은 시간")
                        )
                ));

    }

    @Test
    void 경매_삭제_RestDocsAPI() throws Exception {
        willDoNothing().given(auctionService).deleteAuction(any(), anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/auctions/{auctionId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-auction"));

    }
}
