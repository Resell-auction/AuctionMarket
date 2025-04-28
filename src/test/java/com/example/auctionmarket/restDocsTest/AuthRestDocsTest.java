package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.domain.auth.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@RestDocsTest
@WebMvcTest(AuthController.class)
public class AuthRestDocsTest {

    @Test
    void 회원가입_RestDocsAPI() throws Exception{

    }

    @Test
    void 로그인_RestDocsAPI() throws Exception{

    }
}


