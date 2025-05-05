package com.example.auctionmarket.restDocsTest;

import com.example.auctionmarket.common.jwt.JwtUtil;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@RestDocsTest
public abstract class BaseRestDocsTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected JwtUtil jwtUtil;
//
//    @MockitoBean
//    private AuctionSearchRepository auctionSearchRepository;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .build();
    }
}
