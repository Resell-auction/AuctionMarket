package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.websocket.WebSocketClient;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateMinPriceRequest;
import com.example.auctionmarket.domain.auction.dto.request.AuctionUpdateTimeRequest;
import com.example.auctionmarket.domain.auction.dto.response.AuctionJoinResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionPageResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionResponse;
import com.example.auctionmarket.domain.auction.dto.response.AuctionSaveResponse;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.payment.service.PaymentService;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private AuctionOpenSearchService auctionOpenSearchService;

    @Mock
    private WebSocketClient webSocketClient;

    @InjectMocks
    private AuctionService auctionService;

    private AuthUser authUser;
    private User user;
    private Product product;
    private Auction auction;
    private Page<Auction> auctionPage;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "test@email.com", Role.USER, "testnickname");
        user = new User("test@email.com", "testpassword12@@", "testnickname", "010-0000-0000", Role.USER);
        setIdUsingReflection(user, 1L);

        product = new Product(user, "testproduct", "testcontent", ProductCategory.CLOTHES);
        setIdUsingReflection(product, 1L);

        auction = new Auction(
                product,
                10000L,
                LocalDateTime.now().plusHours(1),
                2L
        );
        setIdUsingReflection(auction, 1L);

        List<Auction> auctions = Collections.singletonList(auction);
        auctionPage = new PageImpl<>(auctions);
    }

    private void setIdUsingReflection(Object entity, Long id) {
        try{
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        }catch (Exception e){
            throw new RuntimeException("ID 설정 실패: "+e.getMessage(), e);
        }
    }

    @Test
    public void 경매_생성_성공(){
        // given
        AuctionSaveRequest request = new AuctionSaveRequest(
                1L, 10000L, LocalDateTime.now().plusHours(1), 2L
        );

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        Product mockProduct = new Product(user, "testproduct", "testcontent", ProductCategory.CLOTHES);
        setIdUsingReflection(mockProduct, 1L);
        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(mockProduct));

        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> {
            Auction saved = invocation.getArgument(0);
            setIdUsingReflection(saved, 1L);
            return saved;
        });

        // when
        AuctionSaveResponse response = auctionService.createAuction(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getProductId());
    }

    @Test
    public void 경매_조회_성공(){
        // given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page-1, size);

        given(auctionRepository.findAll(pageable)).willReturn(auctionPage);

        // when
        AuctionPageResponse response = auctionService.getAuctionsRedis(page, size);

        // then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).getId());
    }

    @Test
    public void 경매_참여_성공(){
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(auctionRepository.findById(1L)).willReturn(Optional.of(auction));

        // when
        AuctionJoinResponse response = auctionService.join(authUser, 1L);

        // then
        assertNotNull(response);
        assertEquals(auction.getId(), response.getAuctionId());
        assertEquals(product.getProductName(), response.getProductName());
    }

    @Test
    public void 경매_최소_가격_수정_성공(){
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(auctionRepository.findById(1L)).willReturn(Optional.of(auction));
        AuctionUpdateMinPriceRequest request = new AuctionUpdateMinPriceRequest(20000L);

        // when
        auctionService.updateMinPrice(authUser, auction.getId(), request);

        // then
        assertEquals(20000L, auction.getMinPrice());
    }

    @Test
    public void 경매_시작_시간_수정_성공(){
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(auctionRepository.findById(1L)).willReturn(Optional.of(auction));

        AuctionUpdateTimeRequest request = new AuctionUpdateTimeRequest(LocalDateTime.now().plusHours(3));

        // when
        AuctionResponse response = auctionService.updateAuctionStartTime(authUser, auction.getId(), request);

        // then
        assertNotNull(response);
        assertEquals(request.getUpdateTime(), auction.getStartTime());
    }
}