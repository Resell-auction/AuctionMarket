package com.example.auctionmarket.coupons;


import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.controller.CouponController;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.example.auctionmarket.global.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

@WebMvcTest(com.example.auctionmarket.domain.coupon.controller.CouponController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 필터 비활성화
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
public class CouponRestDocsTest {

    @Autowired
    private MockMvc mockMvc;
    //
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    private CouponController couponController;
//
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponUserService couponUserService;

    @MockBean
    private LogService logService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .build();
    }

    @Test
    void 쿠폰_찾는_RestDocsAPI_생성() throws Exception {
        // GIVEN
        Long couponId =1L;
        AuthUser authUser = new AuthUser(1L, "abc@naver.com", Role.ADMIN, "nickname");
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        given(couponService.findById(couponId))
                .willReturn(new CouponResponse(
                        couponId, "couponName", "description", 15.0, expiredAt, 100
                ));

        // WHEN + THEN
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/coupons/{couponId}", couponId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-coupon",
                        pathParameters(
                                parameterWithName("couponId").description("쿠폰 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("쿠폰의 고유 ID"),
                                fieldWithPath("couponName").description("쿠폰 이름"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("discountRate").description("할인율"),
                                fieldWithPath("expiredAt").description("만료 날짜"),
                                fieldWithPath("amount").description("쿠폰 수량")
                        )
                ));
    }
}