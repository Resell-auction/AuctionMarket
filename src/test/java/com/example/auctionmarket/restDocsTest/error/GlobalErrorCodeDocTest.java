package com.example.auctionmarket.restDocsTest.error;

import com.example.auctionmarket.domain.analytics.exception.AnalyticsErrorCode;
import com.example.auctionmarket.domain.auth.exception.AuthErrorCode;
import com.example.auctionmarket.domain.user.exception.UserErrorCode;
import com.example.auctionmarket.domain.user.exception.UserException;
import com.example.auctionmarket.common.exception.ErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auth.exception.AuthException;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.payment.exception.PaymentErrorCode;
import com.example.auctionmarket.domain.product.exception.ProductErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class GlobalErrorCodeDocTest {

    @Test
    void 인증_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (AuthErrorCode errorCode : AuthErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/auth-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }

    @Test
    void 유저_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (UserErrorCode errorCode : UserErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/user-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }

    @Test
    void 경매_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (AuctionErrorCode errorCode : AuctionErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/auction-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }
//
    @Test
    void 상품_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (ProductErrorCode errorCode : ProductErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/product-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }

    @Test
    void 쿠폰_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (CouponErrorCode errorCode : CouponErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/coupon-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }

//
    @Test
    void 결제_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (PaymentErrorCode errorCode : PaymentErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/payment-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }

    @Test
    void 빅쿼리_도메인_errorCode() throws IOException {

        StringBuilder adocBuilder = new StringBuilder();
        adocBuilder.append("|===\n");
        adocBuilder.append("| HTTP Status | Error Code | Message\n");

        for (AnalyticsErrorCode errorCode : AnalyticsErrorCode.values()) {
            HttpStatus status = errorCode.getHttpStatus();
            adocBuilder.append(String.format("| %s | %s | %s\n",
                    status.value() + " " + status.getReasonPhrase(),
                    errorCode.getCode(),
                    errorCode.getDefaultMessage()));
        }

        adocBuilder.append("|===\n");

        // 출력 경로
        String outputDir = "src/main/asciidoc";
        Files.createDirectories(Paths.get(outputDir));

        try (FileWriter writer = new FileWriter(outputDir + "/bigquery-errorcode.adoc")) {
            writer.write(adocBuilder.toString());
        }
    }
}
//
//
//
//
//}