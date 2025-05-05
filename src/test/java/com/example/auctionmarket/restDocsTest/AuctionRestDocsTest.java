package com.example.auctionmarket.restDocsTest;


import com.example.auctionmarket.domain.auction.controller.AuctionController;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionJoinResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionPageResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.service.AuctionService;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
public class AuctionRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private AuctionService auctionService;

    @Test
    void 경매_생성_RestDocsAPI() throws Exception {

        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionSaveRequest auctionSaveRequest = new AuctionSaveRequest(1L, 1000L, auctionTime, 100L);

        given(auctionService.createAuction(any(), any()))
                .willReturn(new AuctionSaveResponse(
                        1L, 1L, 1L, "productName", ProductCategory.ACCESSORY, 1000L, auctionTime, auctionTime, AuctionStatus.PENDING, "url")
                );

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auctions")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auctionSaveRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auction/create-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("경매 ID"),
                                fieldWithPath("data.productId").description("경매에 등록된 상품 ID"),
                                fieldWithPath("data.userId").description("경매를 생성한 사용자 ID"),
                                fieldWithPath("data.productName").description("경매에 등록된 상품 이름"),
                                fieldWithPath("data.category").description("경매에 등록된 상품 카테고리"),
                                fieldWithPath("data.minPrice").description("경매 시작 최소 가격"),
                                fieldWithPath("data.startTime").description("경매 시작 시간"),
                                fieldWithPath("data.endTime").description("경매 종료 시간"),
                                fieldWithPath("data.status").description("경매 상태"),
                                fieldWithPath("data.websocketUrl").description("websocketURL")),
                                requestHeaders(
                                        headerWithName("Authorization").description("Bearer 인증 토큰"),
                                        headerWithName("Refresh-Token").description("토큰 시간 연장")
                                )
                        ));
    }

    @Test
    void 경매_전체_조회_RestDocsAPI() throws Exception {

        LocalDateTime auctionTime = LocalDateTime.parse("2025-05-05T00:00:00");
        AuctionResponse auctionResponse = new AuctionResponse(1L, 1L, 1L, "product1", ProductCategory.ACCESSORY, 1000L, 10000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");
        AuctionResponse auctionResponse2 = new AuctionResponse(2L, 2L, 5L, "product2", ProductCategory.BEAUTY, 100000L, 100000000L, auctionTime, auctionTime, AuctionStatus.PENDING, "remainingTime");

        AuctionPageResponse result = new AuctionPageResponse(List.of(auctionResponse, auctionResponse), 1, 10, 10, 10);

        given(auctionService.getAuctionsRedis(1, 10))
                .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/auctions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auction/get-auctions",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.content[].id").description("경매 ID"),
                                fieldWithPath("data.content[].productId").description("경매 상품 ID"),
                                fieldWithPath("data.content[].userId").description("경매를 생성한 사용자 ID"),
                                fieldWithPath("data.content[].productName").description("경매 상품 이름"),
                                fieldWithPath("data.content[].category").description("경매 상품 카테고리"),
                                fieldWithPath("data.content[].minPrice").description("경매 시작 최소 가격"),
                                fieldWithPath("data.content[].maxPrice").description("경매 낙찰 최대 가격"),
                                fieldWithPath("data.content[].startTime").description("경매 시작 시간"),
                                fieldWithPath("data.content[].endTime").description("경매 종료 시간"),
                                fieldWithPath("data.content[].status").description("경매 상태"),
                                fieldWithPath("data.content[].remainingTime").description("경매 종료까지 남은 시간"),
                                fieldWithPath("data.content[].websocketUrl").description("websocketURL"),
                                fieldWithPath("data.page").description("요청한 페이지"),
                                fieldWithPath("data.size").description("요청한 페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("총 페이지 수")
                        )));
    }

    @Test
    void 경매_참여_RestDocsAPI() throws Exception {

        LocalDateTime startTime = LocalDateTime.parse("2025-05-05T00:00:00");
        LocalDateTime endTime = LocalDateTime.parse("2025-05-06T00:00:00");

        AuctionJoinResponse response = new AuctionJoinResponse(1L,"신발",100000L,startTime,endTime,"url");
        given(auctionService.join(any(), anyLong()))
                .willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auctions/{auctionId}/join", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auction/join-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.auctionId").description("경매 ID"),
                                fieldWithPath("data.productName").description("상품명"),
                                fieldWithPath("data.minPrice").description("시작 가격"),
                                fieldWithPath("data.startTime").description("시작 시각"),
                                fieldWithPath("data.endTime").description("종료 시각"),
                                fieldWithPath("data.websocketUrl").description("websocketURL")
                                )
                ));
    }

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
                .andDo(document("auction/updatetime-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("경매 ID"),
                                fieldWithPath("data.productId").description("상품 ID"),
                                fieldWithPath("data.userId").description("구매자 ID"),
                                fieldWithPath("data.productName").description("상품명"),
                                fieldWithPath("data.category").description("상품 카테고리"),
                                fieldWithPath("data.minPrice").description("시작 가격"),
                                fieldWithPath("data.maxPrice").description("즉시 구매 가격"),
                                fieldWithPath("data.startTime").description("경매 시작 시간"),
                                fieldWithPath("data.endTime").description("경매 종료 시간"),
                                fieldWithPath("data.status").description("경매 상태"),
                                fieldWithPath("data.remainingTime").description("남은 시간"),
                                fieldWithPath("data.websocketUrl").description("websocketURL")
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
                .andDo(document("auction/updateprice-auction",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("경매 ID"),
                                fieldWithPath("data.productId").description("상품 ID"),
                                fieldWithPath("data.userId").description("구매자 ID"),
                                fieldWithPath("data.productName").description("상품명"),
                                fieldWithPath("data.category").description("상품 카테고리"),
                                fieldWithPath("data.minPrice").description("시작 가격"),
                                fieldWithPath("data.maxPrice").description("즉시 구매 가격"),
                                fieldWithPath("data.startTime").description("경매 시작 시간"),
                                fieldWithPath("data.endTime").description("경매 종료 시간"),
                                fieldWithPath("data.status").description("경매 상태"),
                                fieldWithPath("data.remainingTime").description("남은 시간"),
                                fieldWithPath("data.websocketUrl").description("websocketURL")
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
                .andDo(document("auction/delete-auction"));
    }
//
//    @Test
//    void 경매_종료_RestDocsAPI() throws Exception {
//        AuctionEndRequest endRequest= new AuctionEndRequest()
//        willDoNothing().given(auctionService).endAuction(anyLong());
//
//        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/auctions/end", 1L)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("end-auction"));
//    }
}
