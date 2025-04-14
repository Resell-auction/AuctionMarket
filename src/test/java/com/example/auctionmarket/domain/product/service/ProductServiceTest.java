package com.example.auctionmarket.domain.product.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void createProduct_product를_정삭적으로_등록한다() {
        // given
        Long productId = 1L;
        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        ProductSaveRequest request = new ProductSaveRequest("아디다스 신발", "아디다스 신발 한정판", "SHOES");

        Product product = new Product(user, request.getProductName(), request.getProductContent(), ProductCategory.of(request.getCategory()));
        ReflectionTestUtils.setField(product, "id", productId);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(productRepository.save(any(Product.class))).willReturn(product);

        // when
        ProductResponse response = productService.createProduct(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(request.getProductName(), response.getProductName());
        assertEquals(request.getCategory(), response.getCategory());
        verify(userRepository).findByEmail(authUser.getEmail());
        verify(productRepository).save(any(Product.class));

    }

    @Test
    public void getProduct_product를_단건_조회_할_수_있다() {
        // given
        Long productId = 1L;
        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        Product product = new Product(user, "아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES);
        ReflectionTestUtils.setField(product, "id", productId);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.getProduct(authUser, product.getId());

        // then
        assertEquals(product.getProductName(), response.getProductName());
        assertThat(authUser.getId()).isEqualTo(product.getUser().getId());
    }

    @Test
    public void getAllProducts_product를_전체_조회_할_수_있다() {
        // given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        List<Product> productList = List.of(
                new Product(user,"아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES),
                new Product(user,"아디다스 옷", "아디다스 옷 한정판", ProductCategory.CLOTHES)
        );

        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(productRepository.findAllMyProduct(pageable, user.getId())).willReturn(productPage);

        // when
        Page<ProductResponse> responsePage = productService.getAllProducts(authUser, page, size);

        // then
        assertThat(responsePage.getContent()).hasSize(2);
        assertEquals(productList.get(0).getProductName(), responsePage.getContent().get(0).getProductName());
        assertEquals(productList.get(1).getCategory().name(), responsePage.getContent().get(1).getCategory());
    }

    @Test
    public void updateProduct_product_정보_수정이_가능하다() {
        // given
        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        Product product = new Product(user, "아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES);
        ReflectionTestUtils.setField(product, "id", 1L);

        ProductUpdateRequest request = new ProductUpdateRequest("아디다스 신발 수정", "아디다스 신발 한정판 수정", "SHOES");

        given(userRepository.findByEmail(authUser.getEmail())).willReturn(Optional.of(user));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.updateProduct(authUser, product.getId(), request);

        // then
        assertEquals(authUser.getEmail(), user.getEmail());
        assertEquals(request.getProductContent(), response.getProductContent());
    }

    @Test
    public void deleteProduct_product를_삭제할_수_있다() {
        // given
        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        Product product = new Product(user, "아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES);
        ReflectionTestUtils.setField(product, "id", 1L);

        given(userRepository.findByEmail(authUser.getEmail())).willReturn(Optional.of(user));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(product.getId());

        // when
        productService.deleteProduct(authUser, product.getId());

        // then
        verify(productRepository, times(1)).deleteById(product.getId());
    }

}