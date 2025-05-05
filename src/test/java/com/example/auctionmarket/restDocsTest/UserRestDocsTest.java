package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.user.controller.UserController;
import com.example.auctionmarket.domain.user.dto.MyPageResponse;
import com.example.auctionmarket.domain.user.dto.UpdatePasswordRequest;
import com.example.auctionmarket.domain.user.dto.UpdateUserRequest;
import com.example.auctionmarket.domain.user.dto.UserResponse;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserRestDocsTest extends BaseRestDocsTest{

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {

        AuthUser mockUser = new AuthUser(1L,"abc@gmail.com", Role.ADMIN,"testuser");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 프로필_확인_RestDocsAPI() throws Exception{

        AuthUser mockUser = mock(AuthUser.class);
        when(mockUser.getId()).thenReturn(1L);

        given(userService.getMyPage(mockUser.getId()))
                .willReturn(new MyPageResponse(1L,"abc@gmail.com","르탄이","ADMIN","010-0000-0000","2025-05-05 00:00","2025-05-05 00:00"));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/users/my")
                        .header("Authorization", "Bearer token")
                        .header("Refresh-Token", "Refresh token")
                        .principal(new UsernamePasswordAuthenticationToken(mockUser, null, List.of(new SimpleGrantedAuthority("ADMIN"))))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/get-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.id").description("회원 ID"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.nickName").description("회원 닉네임"),
                                fieldWithPath("data.userRole").description("회원 역할"),
                                fieldWithPath("data.phoneNumber").description("회원 번호"),
                                fieldWithPath("data.createdAt").description("회원 가입일시"),
                                fieldWithPath("data.modifiedAt").description("회원 정보 수정일시")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer 인증 토큰"),
                                headerWithName("Refresh-Token").description("토큰 발급 시간 연장")
                        )
                ));
    }

    @Test
    void 유저정보_수정_RestDocsAPI() throws Exception{

        AuthUser mockUser = mock(AuthUser.class);
        when(mockUser.getId()).thenReturn(1L);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("수정된 르탄이","010-0000-0000");

        given(userService.updateUser(mockUser.getId(),updateUserRequest.getNickname(),updateUserRequest.getPhoneNumber()))
                .willReturn(new UserResponse("abc@gmail.com","수정된 르탄이","USER","010-0000-0000","2025-05-05T00:00"));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/users/my")
                        .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.nickName").description("회원 닉네임"),
                                fieldWithPath("data.userRole").description("회원 역할"),
                                fieldWithPath("data.phoneNumber").description("회원 번호"),
                                fieldWithPath("data.modifiedAt").description("회원 정보 수정일시")
                        )
                ));
    }

    @Test
    void 비밀번호_수정_RestDocsAPI() throws Exception{

        AuthUser realUser = new AuthUser(1L, "abc@gmail.com", Role.ADMIN, "testuser");
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("11234!@#$ASDFf","1234ASDFa!!@#@#4");

        willDoNothing().given(userService)
                .updatePassword(realUser.getId(), updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword());
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/users/my/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/update-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 유저_삭제_RestDocsAPI() throws Exception{

        willDoNothing().given(userService).deleteUser(anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/users/my")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/delete-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
