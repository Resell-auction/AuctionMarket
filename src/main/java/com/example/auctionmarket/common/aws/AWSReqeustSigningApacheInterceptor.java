package com.example.auctionmarket.common.aws;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;
import java.net.URI;

public class AWSReqeustSigningApacheInterceptor implements HttpRequestInterceptor {

    private final String serviceName;
    private final Aws4Signer signer;
    private final AwsCredentialsProvider credentialsProvider;
    private final Region region;
    private final String endpointUrl;

    public AWSReqeustSigningApacheInterceptor(
            String serviceName,
            Aws4Signer signer,
            AwsCredentialsProvider credentialsProvider,
            Region region,
            String endpointUrl
    ) {
        this.serviceName = serviceName;
        this.signer = signer;
        this.credentialsProvider = credentialsProvider;
        this.region = region;
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void process(HttpRequest request, HttpContext context){
        try{
            URI uri = new URI(endpointUrl);

            String endpointHost = uri.getHost();

            URI originalUri = new URI(request.getRequestLine().getUri());
            String rawPath = originalUri.getPath();
            String rawQuery = originalUri.getQuery();

            SdkHttpFullRequest.Builder sdkHttpFullRequest = SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.valueOf(request.getRequestLine().getMethod()))
                    .protocol(uri.getScheme())
                    .host(endpointHost)
                    .encodedPath(rawPath)
                    .putHeader("host", uri.getHost());

            if(rawQuery != null && !rawQuery.isEmpty()) {
                String[] queryParams = rawQuery.split("&");
                for(String param : queryParams){
                    String[] keyValue = param.split("=");
                    String key = keyValue[0];
                    String value = keyValue.length > 1 ? keyValue[1] : "";
                    sdkHttpFullRequest.putRawQueryParameter(key, value);
                }
            }
            SdkHttpFullRequest sdkRequest = sdkHttpFullRequest.build();

            Aws4SignerParams signerParams = Aws4SignerParams.builder()
                    .signingName(serviceName)
                    .signingRegion(region)
                    .awsCredentials(credentialsProvider.resolveCredentials())
                    .build();

            SdkHttpFullRequest signedRequest = signer.sign(sdkRequest, signerParams);

            signedRequest.headers().forEach((key, values)->{
                if(values != null && !values.isEmpty()) {
                    request.setHeader(key, values.get(0));
                }
            });
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
