package com.example.auctionmarket.domain.auction.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.dto.request.AuctionSaveRequest;
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

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @InjectMocks
    private AuctionService auctionService;

    private AuthUser authUser;
    private User user;
    private Product product;
    private Auction auction;

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
    @DisplayName("경매 생성 성공")
    public void createAuction_Success(){
        //given
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

        //when
        AuctionSaveResponse response = auctionService.createAuction(authUser, request);

        //then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getProductId());
    }

    @Test
    @DisplayName("경매 전체 조회 성공")
    public void getAuctions_Success(){
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Auction> auctionPage = new PageImpl<>(List.of(auction), pageable, 1);

        when(auctionRepository.findAll(pageable)).thenReturn(auctionPage);

        //when
        Page<AuctionResponse> result = auctionService.getAuctions(1, 10);

        //then
        
    }
}