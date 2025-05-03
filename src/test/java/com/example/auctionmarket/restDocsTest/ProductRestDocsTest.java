package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.domain.product.controller.ProductController;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private ProductService productService;

    @Test
    void 상품_등록_RestDocsAPI() throws Exception {

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
    void 상품_단건_조회_RestDocsAPI() throws Exception {

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
    void 상품_전체_조회_RestDocsAPI() throws Exception {
        ProductResponse productResponse = new ProductResponse(1L, "신발1", "설명1", "CLOTHES", "UNSOLD");
        Page<ProductResponse> result = new PageImpl<>(List.of(productResponse, productResponse),  PageRequest.of(1, 10), 2);

        given(productService.getAllProducts(any(),anyInt(),anyInt()))
                .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-allproducts",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].id").description("상품의 고유 ID"),
                                fieldWithPath("data[].productName").description("상품명"),
                                fieldWithPath("data[].productContent").description("상품 상세 설명"),
                                fieldWithPath("data[].category").description("상품 카테고리"),
                                fieldWithPath("data[].soldStatus").description("상품 판매 상태"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 데이터 수"),
                                fieldWithPath("pageSize").description("페이지당 데이터 수"),
                                fieldWithPath("pageNumber").description("현재 페이지 번호")
                      )
                ));
    }

    @Test
    void 상품_정보_수정_RestDocsAPI() throws Exception {

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest("수정된 상품","수정된 상품설명","BAG");

        given(productService.updateProduct(any(), anyLong(), any()))
                .willReturn(new ProductResponse(1L, "상품1","상품설명","CLOTHES","UNSOLD"));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/products/{productId}",1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-product",
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
    void 상품_삭제_RestDocsAPI() throws Exception {

        willDoNothing().given(productService).deleteProduct(any(), anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/products/{productId}",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-product"));

    }
}
