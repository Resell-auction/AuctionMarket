package com.example.auctionmarket.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // @Value: application.properties 에 있는 값을 주입
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region}")
    private String region;

    // S3Client는 AWS SDK for Java v2의 클라이언트 객체
    // 이걸 통해 S3 Bucket에 접근하거나 파일 업로드/다운로드 가능
    @Bean
    public S3Client s3Client() {
        /* 두개의 키로 자격증명 객체 생성 */
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)) // 자격 증명을 제공하는 provider
                .build();
    }
}
