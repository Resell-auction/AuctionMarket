package com.example.auctionmarket.coupons;


import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.controller.CouponController;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class CouponRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponUserService couponUserService;

    @MockBean
    private LogService logService;

    @Test
    void 쿠폰_찾는_RestDocsAPI_생성() throws Exception {
        // GIVEN
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        given(couponService.findById(1L))
                .willReturn(new CouponResponse(
                        1L, "couponName", "description", 15.0, expiredAt, 100
                ));
//        CouponRespo

        // WHEN + THEN
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/coupons/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("get-coupon",
                        pathParameters(
                                parameterWithName("id").description("쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("쿠폰의 고유 ID"),
                                fieldWithPath("name").description("쿠폰 이름"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("discountRate").description("할인율"),
                                fieldWithPath("expiredAt").description("만료 날짜"),
                                fieldWithPath("amount").description("쿠폰 수량")
                        )
                ));
    }
}