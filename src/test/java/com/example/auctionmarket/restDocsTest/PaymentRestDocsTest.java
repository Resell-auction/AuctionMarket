package com.example.auctionmarket.restDocsTest;


import com.example.auctionmarket.domain.payment.controller.PaymentController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@RestDocsTest
@WebMvcTest(PaymentController.class)
public class PaymentRestDocsTest {

    @Test
    void 결제_요청_RestDocsAPI() throws Exception{

    }

    @Test
    void 환불_RestDocsAPI() throws Exception{

    }
}

