package com.joaobisi.productscatalog.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSnsConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretkey;

    @Value("${aws.sns.topic.catalog.arn}")
    private String calatogTopicArn;

    @Bean
    public AmazonSNS snsClientBuilder() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretkey);

        return AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    @Bean(name = "catalogEventsTopic")
    public Topic snsCatalogBuilder() {
        return new Topic()
                .withTopicArn(calatogTopicArn);
    }
}
