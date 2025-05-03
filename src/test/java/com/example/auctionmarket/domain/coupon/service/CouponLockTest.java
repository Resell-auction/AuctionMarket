package com.example.auctionmarket.domain.coupon.service;

//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;

@SpringBootTest
//@Testcontainers
public class CouponLockTest {
//
//    @Container
//    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
//            .withDatabaseName("testdb")
//            .withUsername("root")
//            .withPassword("paassword");
//
//    @Container
//    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
//            .withExposedPorts(6379);
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysql::getJdbcUrl);
//        registry.add("spring.datasource.username", mysql::getUsername);
//        registry.add("spring.datasource.password", mysql::getPassword);
//
//        registry.add("spring.data.redis.host", redis::getHost);
//        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
//
//    }

    @Test
    public void contextLoads() {
        System.out.println("test");
        // 아무 것도 안 하고 컨텍스트 로딩만
    }
}