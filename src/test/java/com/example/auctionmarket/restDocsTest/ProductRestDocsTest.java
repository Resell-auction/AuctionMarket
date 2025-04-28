package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.product.controller.ProductController;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.product.service.ProductService;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.example.auctionmarket.global.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductRestDocsTest extends BaseRestDocsTest {

    @MockBean
    private ProductService productService;

    @Test
    void 제품_등록_RestDocsAPI() throws Exception {

        ProductSaveRequest productSaveRequest = new ProductSaveRequest("신발1", "설명1", "CLOTHES");
        given(productService.createProduct(any(), any()))
                .willReturn(new ProductResponse(1L, "신발1", "설명1", "CLOTHES", "UNSOLD"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/products")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productSaveRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("상품의 고유 ID"),
                                fieldWithPath("data.productName").description("상품명"),
                                fieldWithPath("data.productContent").description("상품 상세 설명"),
                                fieldWithPath("data.category").description("상품 카테고리"),
                                fieldWithPath("data.soldStatus").description("상품 판매 상태 (예: 판매중, 판매완료)")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 발급 시간 연장")
                        )));
    }

    @Test
    void 제품_단건_조회_RestDocsAPI() throws Exception {

        given(productService.getProduct(any(),any()))
                .willReturn(new ProductResponse(1L, "신발1", "설명1", "CLOTHES", "UNSOLD"));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/{productId}",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("상품의 고유 ID"),
                                fieldWithPath("data.productName").description("상품명"),
                                fieldWithPath("data.productContent").description("상품 상세 설명"),
                                fieldWithPath("data.category").description("상품 카테고리"),
                                fieldWithPath("data.soldStatus").description("상품 판매 상태 (예: 판매중, 판매완료)")
                        )));

    }

    @Test
    void 제품_전체_조회_RestDocsAPI() throws Exception {

    }

    @Test
    void 제품_정보_수정_RestDocsAPI() throws Exception {

    }

    @Test
    void 제품_삭제_RestDocsAPI() throws Exception {

    }
}
