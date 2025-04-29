package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.domain.auth.controller.AuthController;
import com.example.auctionmarket.domain.auth.dto.LoginResponse;
import com.example.auctionmarket.domain.auth.dto.SigninRequest;
import com.example.auctionmarket.domain.auth.dto.SignupRequest;
import com.example.auctionmarket.domain.auth.dto.SignupResponse;
import com.example.auctionmarket.domain.auth.service.AuthService;
import com.example.auctionmarket.domain.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthRestDocsTest extends BaseRestDocsTest{

    @MockBean
    private AuthService authService;

    @Test
    void 회원가입_RestDocsAPI() throws Exception{

        SignupRequest signupRequest = new SignupRequest("abc@gmail.com","123asdQWE!@#","닉넴","010-0000-0000", "ADMIN");

        given(authService.signup(any(),any(),any(),any(),any()))
                .willReturn(new SignupResponse("accessToken","refreshToken"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signup-auth",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.accessToken").description("엑세스토큰"),
                                fieldWithPath("data.refreshToken").description("연장토큰")
                        )
                ));
    }

    @Test
    void 로그인_RestDocsAPI() throws Exception{
        SigninRequest signinRequest = new SigninRequest("abc@gmail.com","123asdQWE!@#");

        given(authService.signin(any(),any()))
                .willReturn(new LoginResponse("accessToken","refreshToken"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signin-auth",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.accessToken").description("엑세스토큰"),
                                fieldWithPath("data.refreshToken").description("연장토큰")
                        )
                ));
    }
}


