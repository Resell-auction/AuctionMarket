package com.example.auctionmarket.common.config;

import com.example.auctionmarket.common.aws.AWSReqeustSigningApacheInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.url}")
    private String openSearchUrl;

//    @Value("${opensearch.username}")
//    private String username;
//
//    @Value("${opensearch.password}")
//    private String password;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

//    @Bean
//    public RestHighLevelClient restHighLevelClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
//
//        RestClientBuilder builder = RestClient.builder(HttpHost.create(openSearchUrl))
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
//
//        return new RestHighLevelClient(builder);
//    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        Aws4Signer signer = Aws4Signer.create();
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        HttpHost host = HttpHost.create(openSearchUrl);

        RestClientBuilder restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    ApacheHttpClient.Builder apacheClientBuilder = ApacheHttpClient.builder();
                    return httpClientBuilder
                            .addInterceptorLast(new AWSReqeustSigningApacheInterceptor(
                                    "es",
                                    signer,
                                    credentialsProvider,
                                    Region.of(region),
                                    openSearchUrl
                            ));
                });

        return new RestHighLevelClient(restClient);
    }
}
