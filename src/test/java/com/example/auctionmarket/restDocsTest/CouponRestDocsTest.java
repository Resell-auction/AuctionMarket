package com.example.auctionmarket.restDocsTest;


import com.example.auctionmarket.domain.coupon.controller.CouponController;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
public class CouponRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private CouponUserService couponUserService;

    @MockitoBean
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_생성_RestDocsAPI() throws Exception {

        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponRequest couponRequest = new CouponRequest("couponName", "description", (long) 15.0, expiredAt, 100, CouponType.PERCENT);

        given(couponService.createCoupon(any(), any()))
                .willReturn(new CouponResponse(
                        1L, "couponName", "description", 15.0,
                        LocalDateTime.parse("2025-05-05T00:00:00"), 100
                ));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/coupons")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/post-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("쿠폰의 고유 ID"),
                                fieldWithPath("data.couponName").description("쿠폰 이름"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.discountRate").description("할인율"),
                                fieldWithPath("data.expiredAt").description("만료 날짜"),
                                fieldWithPath("data.amount").description("쿠폰 수량")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 발급 시간 연장")
                        )
                ));
    }

    @Test
    void 쿠폰_단건_조회_RestDocsAPI() throws Exception {

        Long couponId = 1L;
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        given(couponService.findById(couponId))
                .willReturn(new CouponResponse(
                        couponId, "couponName", "description", 15.0, expiredAt, 100
              ));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/get-couponId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponId").description("쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("쿠폰의 고유 ID"),
                                fieldWithPath("data.couponName").description("쿠폰 이름"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.discountRate").description("할인율"),
                                fieldWithPath("data.expiredAt").description("만료 날짜"),
                                fieldWithPath("data.amount").description("쿠폰 수량")
                        )

                ));
    }

    @Test
    void 쿠폰_목록_조회_RestDocsAPI() throws Exception {

        Long couponId = 1L;
        CouponResponse couponResponse = new CouponResponse(couponId, "couponName1", "회원가입 기념 쿠폰입니다.", 10.0, LocalDateTime.parse("2025-05-05T00:00:00"), 100);
        CouponResponse couponResponse2 = new CouponResponse(2L, "couponName2", "VIP회원 전용 쿠폰입니다.", 25.0, LocalDateTime.parse("2025-05-05T00:00:00"), 100);

        List<CouponResponse> couponList = List.of(couponResponse, couponResponse2);
        given(couponService.findAll())
                .willReturn(couponList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/coupons")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/get-coupons",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                // fieldWithPath("code").description("응답 코드"),
                                // fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("쿠폰의 고유 ID"),
                                fieldWithPath("data[].couponName").description("쿠폰 이름"),
                                fieldWithPath("data[].description").description("쿠폰 설명"),
                                fieldWithPath("data[].discountRate").description("할인율"),
                                fieldWithPath("data[].expiredAt").description("만료 날짜"),
                                fieldWithPath("data[].amount").description("쿠폰 수량")
                        )
                ));
    }

    @Test
    void 쿠폰_수정_RestDocsAPI() throws Exception {

        Long couponId = 1L;
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponUpdateRequest couponUpdateRequest = new CouponUpdateRequest("couponName", "description", (long) 15.0, expiredAt);

        given(couponService.updateById(any(), eq(couponId), any()))
                .willReturn(new CouponResponse(
                        1L, "couponName", "description", 15.0,
                        LocalDateTime.parse("2025-05-05T00:00:00"), 100
                ));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/update-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("쿠폰 ID"),
                                fieldWithPath("data.couponName").description("쿠폰 이름"),
                                fieldWithPath("data.description").description("쿠폰 설명"),
                                fieldWithPath("data.discountRate").description("할인율"),
                                fieldWithPath("data.expiredAt").description("만료 날짜"),
                                fieldWithPath("data.amount").description("쿠폰 수량")
                        )
                ));
    }

    @Test
    void 쿠폰_삭제_RestDocsAPI() throws Exception {

        Long couponId = 1L;

        willDoNothing().given(couponService).deleteById(any(), anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/delete-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 특정_유저에게_쿠폰_증정_RestDocsAPI() throws Exception {

        Long couponId = 1L;

        CouponGiveRequest couponGiveRequest = new CouponGiveRequest(1L, 100);

        willDoNothing().given(couponUserService).giveCouponByUserId(any(), anyLong(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/coupons/{couponId}/give", couponId)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponGiveRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/give-coupons",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 쿠폰_만료_RestDocsAPI() throws Exception {

        Long couponId = 1L;

        willDoNothing().given(couponService).expireCoupons();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/coupons/expire")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon/expire-coupons",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));

    }
}