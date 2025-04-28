package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.domain.user.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.retry.annotation.Retryable;

@RestDocsTest
@WebMvcTest(UserController.class)
public class UserRestDocsTest {

    @Test
    void 프로필_확인_RestDocsAPI() throws Exception{

    }

    @Test
    void 유저정보_수정_RestDocsAPI() throws Exception{

    }

    @Test
    void 비밀번호_수정_RestDocsAPI() throws Exception{

    }

    @Test
    void 유저_삭제_RestDocsAPI() throws Exception{

    }
}
