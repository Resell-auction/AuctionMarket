package com.example.auctionmarket.restDocsTest;


import com.example.auctionmarket.domain.payment.controller.PaymentController;
import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.dto.request.RefundRequest;
import com.example.auctionmarket.domain.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentRestDocsTest extends BaseRestDocsTest {

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void 결제_RestDocsAPI() throws Exception {

        PaymentRequest paymentRequest = new PaymentRequest("신용카드", 100000L, 1L);
        willDoNothing().given(paymentService).confirmPayment(anyLong(), any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/payments/{paymentId}", 1L)
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/confirm-payment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 발급 시간 연장")
                        )));
    }

    @Test
    void 결제_실패_RestDocsAPI() throws Exception {

        RefundRequest refundRequest = new RefundRequest("신용카드", "설명" );
        willDoNothing().given(paymentService).confirmPayment(anyLong(), any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/payments/{paymentId}/refund", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment/refund-payment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                       ));
    }
}

