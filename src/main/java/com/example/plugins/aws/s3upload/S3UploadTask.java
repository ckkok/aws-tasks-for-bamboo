package com.example.plugins.aws.s3upload;

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
import java.io.File;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

@Scanned
public class S3UploadTask implements TaskType {

  private static final String ENVIRONMENT = "environmentVariables";

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

    String accessKey = configuration.get(S3UploadTaskConfigurator.ACCESS_KEY);
    String secretKey = configuration.get(S3UploadTaskConfigurator.SECRET_KEY);
    String region = configuration.get(S3UploadTaskConfigurator.REGION);
    String s3Bucket = configuration.get(S3UploadTaskConfigurator.S3_BUCKET);
    String file = configuration.get(S3UploadTaskConfigurator.FILE);
    String destinationKey = configuration.get(S3UploadTaskConfigurator.DESTINATION_KEY);
    if (StringUtils.isEmpty(destinationKey)) {
      destinationKey = file;
    }
    File workingDirectory = taskContext.getWorkingDirectory();
    File[] files = workingDirectory.listFiles();
    if (Objects.nonNull(files)) {
      for (File f : files) {
        buildLogger.addBuildLogEntry("Available file: " + f.getPath());
      }
    }
    String absoluteFilePath = workingDirectory.getAbsolutePath() + File.separator + file;
    if (!new File(absoluteFilePath).exists()) {
      buildLogger.addErrorLogEntry(file + " does not exist");
      return builder.failed().build();
    }
    try {
      S3Proxy s3Proxy = new S3Proxy(accessKey, secretKey, region);
      s3Proxy.uploadToS3Bucket(file, destinationKey, s3Bucket);
      buildLogger.addBuildLogEntry("Sent " + file + " to S3 bucket " + s3Bucket + " as " + destinationKey);
      return builder.success().build();
    } catch (AwsServiceException | SdkClientException e) {
      buildLogger.addErrorLogEntry("Unable to send " + file + " to S3 bucket " + s3Bucket + " as " + destinationKey, e);
      return builder.failed().build();
    }
  }


}
