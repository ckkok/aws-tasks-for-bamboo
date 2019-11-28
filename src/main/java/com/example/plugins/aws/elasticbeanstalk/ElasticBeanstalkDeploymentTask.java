package com.example.plugins.aws.elasticbeanstalk;

import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.ACCESS_KEY;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.APPLICATION_NAME;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.DEPLOYMENT_ACTION;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.ENVIRONMENT_NAME;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.REGION;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.S3_BUCKET;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.SECRET_KEY;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.S3_KEY;
import static com.example.plugins.aws.elasticbeanstalk.ElasticBeanstalkDeploymentTaskConfigurator.VERSION_LABEL;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.elasticbeanstalk.model.InsufficientPrivilegesException;
import software.amazon.awssdk.services.elasticbeanstalk.model.S3LocationNotInServiceRegionException;
import software.amazon.awssdk.services.elasticbeanstalk.model.TooManyApplicationVersionsException;
import software.amazon.awssdk.services.elasticbeanstalk.model.TooManyApplicationsException;
import software.amazon.awssdk.services.elasticbeanstalk.model.TooManyBucketsException;

@Scanned
public class ElasticBeanstalkDeploymentTask implements TaskType {
  
  @Autowired
  @ComponentImport
  private EnvironmentVariableAccessor environmentVariableAccessor;
  
  @Autowired
  @ComponentImport
  private CapabilityContext capabilityContext;
  
  @NotNull
  @Override
  public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
    TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
    BuildLogger buildLogger = taskContext.getBuildLogger();
    ConfigurationMap configuration = taskContext.getConfigurationMap();
    String accessKey = configuration.get(ACCESS_KEY);
    String secretKey = configuration.get(SECRET_KEY);
    String region = configuration.get(REGION);
    String applicationName = configuration.get(APPLICATION_NAME);
    String environmentName = configuration.get(ENVIRONMENT_NAME);
    String s3Bucket = configuration.get(S3_BUCKET);
    String s3Key = configuration.get(S3_KEY);
    String versionLabel = configuration.get(VERSION_LABEL);
    String deploymentAction = configuration.get(DEPLOYMENT_ACTION);
    ElasticBeanstalkProxy ebProxy = null;
    
    buildLogger.addBuildLogEntry("Deployment action: " + deploymentAction);
    
    try {
      ebProxy = new ElasticBeanstalkProxy(accessKey, secretKey, region);
    } catch (IllegalArgumentException e) {
      buildLogger.addErrorLogEntry("Invalid AWS region: " + region, e);
      return builder.failed().build();
    }
    
    try {
      // ebProxy.createApplicationVersion(applicationName, versionLabel, s3Bucket, s3Key);
    } catch (TooManyApplicationsException e) {
      buildLogger.addErrorLogEntry("Exceeded number of allowed applications for account", e);
      return builder.failed().build();
    } catch (TooManyApplicationVersionsException e) {
      buildLogger.addErrorLogEntry("Exceeded number of allowed application versions for account", e);
      return builder.failed().build();
    } catch (InsufficientPrivilegesException e) {
      buildLogger.addErrorLogEntry("Insufficient privileges to create application version", e);
      return builder.failed().build();
    } catch (S3LocationNotInServiceRegionException e) {
      buildLogger.addErrorLogEntry("Invalid S3 location - S3 bucket is outside of service region", e);
      return builder.failed().build();
    } catch (AwsServiceException | SdkClientException e) {
      buildLogger.addErrorLogEntry("Unable to create application " + applicationName + " with version " + versionLabel + " from S3 location " + s3Bucket + "::" + s3Key, e);
      return builder.failed().build();
    }
    
    try {
      // ebProxy.updateEnvironment(applicationName, environmentName, versionLabel);
      return builder.success().build();
    } catch (InsufficientPrivilegesException e) {
      buildLogger.addErrorLogEntry("Insufficient privileges to update environment", e);
      return builder.failed().build();
    } catch (TooManyBucketsException e) {
      buildLogger.addErrorLogEntry("Exceeded number of S3 buckets allowed per environment", e);
      return builder.failed().build();
    } catch (AwsServiceException | SdkClientException e) {
      buildLogger.addErrorLogEntry("Unable to update application " + applicationName + " environment " + environmentName + " to version " + versionLabel, e);
      return builder.failed().build();
    }
  }
  
}
