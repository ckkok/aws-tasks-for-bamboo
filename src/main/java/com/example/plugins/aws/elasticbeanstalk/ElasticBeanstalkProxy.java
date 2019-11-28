package com.example.plugins.aws.elasticbeanstalk;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationVersionResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateConfigurationTemplateResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateStorageLocationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationVersionRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationVersionResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteConfigurationTemplateRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteConfigurationTemplateResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteEnvironmentConfigurationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteEnvironmentConfigurationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.S3Location;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateApplicationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateApplicationVersionResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateConfigurationTemplateResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.UpdateEnvironmentResponse;

public class ElasticBeanstalkProxy {
  
  private final ElasticBeanstalkClient elasticBeanstalkClient;
  
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
  
  public CreateApplicationResponse createApplication(String applicationName, String description) {
    CreateApplicationRequest request = CreateApplicationRequest.builder()
        .applicationName(applicationName)
        .description(description)
        .build();
    return elasticBeanstalkClient.createApplication(request);
  }
  
  public CreateApplicationVersionResponse createApplicationVersion(String applicationName, String versionLabel, String s3Bucket, String s3Key) {
    S3Location s3Location = S3Location.builder().s3Bucket(s3Bucket).s3Key(s3Key).build();
    CreateApplicationVersionRequest request = CreateApplicationVersionRequest.builder()
        .applicationName(applicationName)
        .versionLabel(versionLabel)
        .sourceBundle(s3Location)
        .build();
    return elasticBeanstalkClient.createApplicationVersion(request);
  }
  
  public CreateConfigurationTemplateResponse createConfigurationTemplate(String applicationName, String description) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public CreateEnvironmentResponse createEnvironment(String applicationName, String environmentName, String cNamePrefix) {
    CreateEnvironmentRequest request = CreateEnvironmentRequest.builder()
        .applicationName(applicationName)
        .environmentName(environmentName)
        .cnamePrefix(cNamePrefix)
        .build();
    return elasticBeanstalkClient.createEnvironment(request);
  }
  
  public CreateStorageLocationResponse createStorageLocation() {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public UpdateApplicationResponse updateApplication(String applicationName, String environmentName, String versionLabel) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public UpdateApplicationVersionResponse updateApplicationVersion(String applicationName, String environmentName, String versionLabel) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public UpdateConfigurationTemplateResponse updateConfigurationTemplate(String applicationName, String environmentName, String versionLabel) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public UpdateEnvironmentResponse updateEnvironment(String applicationName, String environmentName, String versionLabel) {
    UpdateEnvironmentRequest request = UpdateEnvironmentRequest.builder()
        .applicationName(applicationName)
        .environmentName(environmentName)
        .versionLabel(versionLabel)
        .build();
    return elasticBeanstalkClient.updateEnvironment(request);
  }
  
  public DeleteApplicationResponse deleteApplication(String applicationName) {
    DeleteApplicationRequest request = DeleteApplicationRequest.builder()
        .applicationName(applicationName)
        .terminateEnvByForce(true)
        .build();
    return elasticBeanstalkClient.deleteApplication(request);
  }
  
  public DeleteApplicationVersionResponse deleteApplicationVersion(String applicationName, String versionLabel) {
    DeleteApplicationVersionRequest request = DeleteApplicationVersionRequest.builder()
        .applicationName(applicationName)
        .versionLabel(versionLabel)
        .deleteSourceBundle(true)
        .build();
    return elasticBeanstalkClient.deleteApplicationVersion(request);
  }
  
  public DeleteConfigurationTemplateResponse deleteConfigurationTemplate(String applicationName, String templateName) {
    DeleteConfigurationTemplateRequest request = DeleteConfigurationTemplateRequest.builder()
        .applicationName(applicationName)
        .templateName(templateName)
        .build();
    return elasticBeanstalkClient.deleteConfigurationTemplate(request);
  }
  
  public DeleteEnvironmentConfigurationResponse deleteEnvironmentConfiguration(String applicationName, String environmentName) {
    DeleteEnvironmentConfigurationRequest request = DeleteEnvironmentConfigurationRequest.builder()
        .applicationName(applicationName)
        .environmentName(environmentName)
        .build();
    return elasticBeanstalkClient.deleteEnvironmentConfiguration(request);
  }
}
