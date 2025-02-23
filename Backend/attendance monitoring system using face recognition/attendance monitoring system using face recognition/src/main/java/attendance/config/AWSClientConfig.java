package attendance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
public class AWSClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSClientConfig.class);

    // 🔴 HARDCODED AWS CREDENTIALS (Replace with actual ones)
    private static final String ACCESS_KEY = "AKIAW5WU5GBYZ5YXLZOL";
    private static final String SECRET_KEY = "5wMirnRIqD+CR3pt7SgoYoYvtneBtemGhEP5oSZv";
    private static final Region REGION = Region.US_EAST_2;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        LOGGER.info("✅ AWS Credentials Loaded Successfully.");
        return StaticCredentialsProvider.create(awsCredentials);
    }

    @Bean
//    public S3Client(AwsCredentialsProvider credentialsProvider) {
//        LOGGER.info("🚀 Initializing S3 Client in region: {}", REGION);
//        return S3Client.builder()
//                .region(REGION)
//                .credentialsProvider(credentialsProvider)
//                .overrideConfiguration(ClientOverrideConfiguration.builder()
//                        .retryPolicy(RetryPolicy.defaultRetryPolicy())
//                        .apiCallTimeout(Duration.ofSeconds(30))
//                        .build())
//                .build();
//    }
     public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTH_1) // Ensure this matches your S3 bucket region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Optional: Enable path-style access if needed
                        .build())
                .endpointOverride(URI.create("https://sts.amazonaws.com")) // Explicitly set correct endpoint
                .build();
    }

    @Bean
    public RekognitionClient rekognitionClient(AwsCredentialsProvider credentialsProvider) {
        LOGGER.info("🚀 Initializing Rekognition Client in region: {}", REGION);
        return RekognitionClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryPolicy(RetryPolicy.defaultRetryPolicy())
                        .apiCallTimeout(Duration.ofSeconds(30))
                        .build())
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider) {
        LOGGER.info("🚀 Initializing DynamoDB Client in region: {}", REGION);
        return DynamoDbClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryPolicy(RetryPolicy.defaultRetryPolicy())
                        .apiCallTimeout(Duration.ofSeconds(30))
                        .build())
                .build();
    }
}
