package com.example.plugins.aws.elasticbeanstalk;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.*;

public class ElasticBeanstalkProxy {

  private ElasticBeanstalkClient elasticBeanstalkClient;

  public ElasticBeanstalkProxy(String accessKey, String secretKey, String region) {
    AwsCredentials credentials = new AwsCredentials() {
      @Override
      public String accessKeyId() {
        return accessKey;
      }

      @Override
      public String secretAccessKey() {
        return secretKey;
      }
    };
    AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
    elasticBeanstalkClient = ElasticBeanstalkClient.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build();
  }
  
  public boolean createApplicationVersion(String applicationName, String versionLabel, String s3Bucket, String s3Key) {
    S3Location s3Location = S3Location.builder().s3Bucket(s3Bucket).s3Key(s3Key).build();
    CreateApplicationVersionRequest createApplicationVersionRequest = CreateApplicationVersionRequest.builder()
        .applicationName(applicationName)
        .versionLabel(versionLabel)
        .sourceBundle(s3Location)
        .build();
    CreateApplicationVersionResponse createApplicationVersionResponse = elasticBeanstalkClient.createApplicationVersion(createApplicationVersionRequest);
    return createApplicationVersionResponse.sdkHttpResponse().isSuccessful();
  }

  public boolean updateEnvironment(String applicationName, String environmentName, String versionLabel) {
    UpdateEnvironmentRequest updateEnvironmentRequest = UpdateEnvironmentRequest.builder()
        .applicationName(applicationName)
        .environmentName(environmentName)
        .versionLabel(versionLabel)
        .build();
    UpdateEnvironmentResponse updateEnvironmentResponse = elasticBeanstalkClient.updateEnvironment(updateEnvironmentRequest);
    return updateEnvironmentResponse.sdkHttpResponse().isSuccessful();
  }
}
