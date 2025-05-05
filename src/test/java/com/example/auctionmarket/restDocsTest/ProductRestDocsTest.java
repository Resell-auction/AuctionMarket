package com.example.auctionmarket.restDocsTest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.auctionmarket.domain.product.controller.ProductController;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.service.ProductService;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;

@WebMvcTest(ProductController.class)
public class ProductRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private ProductService productService;

    @Test
    void 상품_등록_RestDocsAPI() throws Exception {

        List<ProductImageResponse> productImageResponselist= new ArrayList<>();

        ProductSaveRequest productSaveRequest = new ProductSaveRequest("신발1", "설명1", "CLOTHES");
        given(productService.createProduct(any(), any()))
            .willReturn(new ProductResponse(1L, "신발1", "설명1", "CLOTHES", "UNSOLD", productImageResponselist));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/products")
                .header("Authorization", "Bearer token")
                .header("Refresh-Token", "Refresh token")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productSaveRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/post-product",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data.id").description("상품의 고유 ID"),
                    fieldWithPath("data.productName").description("상품명"),
                    fieldWithPath("data.productContent").description("상품 상세 설명"),
                    fieldWithPath("data.category").description("상품 카테고리"),
                    fieldWithPath("data.soldStatus").description("상품 판매 상태 "),
                    fieldWithPath("data.images").description("상품 이미지리스트 ")
                ),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰"),
                    headerWithName("Refresh-Token").description("토큰 발급 시간 연장")
                )));
    }

    @Test
    void 상품_단건_조회_RestDocsAPI() throws Exception {

        List<ProductImageResponse> productImageResponselist= new ArrayList<>();

        given(productService.getProduct(any(),any()))
            .willReturn(new ProductResponse(1L, "신발1", "설명1", "CLOTHES", "UNSOLD",productImageResponselist));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/{productId}",1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/get-product",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data.id").description("상품의 고유 ID"),
                    fieldWithPath("data.productName").description("상품명"),
                    fieldWithPath("data.productContent").description("상품 상세 설명"),
                    fieldWithPath("data.category").description("상품 카테고리"),
                    fieldWithPath("data.soldStatus").description("상품 판매 상태"),
                    fieldWithPath("data.images").description("상품 이미지리스트 ")

                )));
    }

    @Test
    void 상품_전체_조회_RestDocsAPI() throws Exception {

        List<ProductImageResponse> productImageResponselist= new ArrayList<>();

        ProductResponse productResponse = new ProductResponse(1L, "신발1", "설명1", "SHOSE", "UNSOLD",productImageResponselist);
        ProductResponse productResponse2 = new ProductResponse(2L, "상의1", "설명1", "CLOTHES", "UNSOLD",productImageResponselist);

        Page<ProductResponse> result = new PageImpl<>(List.of(productResponse, productResponse2),  PageRequest.of(1, 10), 2);

        given(productService.getAllProducts(any(),anyInt(),anyInt()))
            .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/get-allproducts",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data[].id").description("상품의 고유 ID"),
                    fieldWithPath("data[].productName").description("상품명"),
                    fieldWithPath("data[].productContent").description("상품 상세 설명"),
                    fieldWithPath("data[].category").description("상품 카테고리"),
                    fieldWithPath("data[].soldStatus").description("상품 판매 상태"),
                    fieldWithPath("data[].images").description("상품 이미지리스트 "),
                    fieldWithPath("totalPages").description("전체 페이지 수"),
                    fieldWithPath("totalElements").description("전체 데이터 수"),
                    fieldWithPath("pageSize").description("페이지당 데이터 수"),
                    fieldWithPath("pageNumber").description("현재 페이지 번호")
                )
            ));
    }

    @Test
    void 상품_검색_RestDocsAPI() throws Exception {

        List<ProductImageResponse> productImageResponselist= new ArrayList<>();

        ProductResponse productResponse = new ProductResponse(1L, "신발1", "설명1", "SHOSE", "UNSOLD",productImageResponselist);
        ProductResponse productResponse2 = new ProductResponse(2L, "상의1", "설명1", "CLOTHES", "UNSOLD",productImageResponselist);

        Page<ProductResponse> result = new PageImpl<>(List.of(productResponse, productResponse2),  PageRequest.of(1, 10), 2);

        given(productService.searchProducts(any(),any(), anyInt(), anyInt(), any()))
            .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/search")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/search-products",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data[].id").description("상품의 고유 ID"),
                    fieldWithPath("data[].productName").description("상품명"),
                    fieldWithPath("data[].productContent").description("상품 상세 설명"),
                    fieldWithPath("data[].category").description("상품 카테고리"),
                    fieldWithPath("data[].soldStatus").description("상품 판매 상태"),
                    fieldWithPath("data[].images").description("상품 이미지리스트 "),
                    fieldWithPath("totalPages").description("전체 페이지 수"),
                    fieldWithPath("totalElements").description("전체 데이터 수"),
                    fieldWithPath("pageSize").description("페이지당 데이터 수"),
                    fieldWithPath("pageNumber").description("현재 페이지 번호")
                )
            ));
    }

    @Test
    void 상품_정보_수정_RestDocsAPI() throws Exception {

        List<ProductImageResponse> productImageResponselist= new ArrayList<>();

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest("수정된 상품","수정된 상품설명","BAG");

        given(productService.updateProduct(any(), anyLong(), any()))
            .willReturn(new ProductResponse(1L, "상품1","상품설명","CLOTHES","UNSOLD",productImageResponselist));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/products/{productId}",1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/update-product",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data.id").description("상품의 고유 ID"),
                    fieldWithPath("data.productName").description("상품명"),
                    fieldWithPath("data.productContent").description("상품 상세 설명"),
                    fieldWithPath("data.category").description("상품 카테고리"),
                    fieldWithPath("data.soldStatus").description("상품 판매 상태"),
                    fieldWithPath("data.images").description("상품 이미지리스트 ")
                )));
    }

    @Test
    void 상품_삭제_RestDocsAPI() throws Exception {

        willDoNothing().given(productService).deleteProduct(any(), anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/products/{productId}",1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("product/delete-product"));

    }
}