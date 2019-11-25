package com.example.plugins.aws.s3upload;

// import com.atlassian.bamboo.artifact.Artifact;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
// import com.atlassian.bamboo.serialization.WhitelistedSerializable;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
// import com.atlassian.bamboo.variable.VariableDefinitionContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
// import com.google.common.collect.Multimap;
// import java.io.File;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class S3UploadTask implements TaskType {

  private static final String ENVIRONMENT = "environmentVariables";

  @Autowired
  @ComponentImport
  private EnvironmentVariableAccessor environmentVariableAccessor;

  @Autowired
  @ComponentImport
  private CapabilityContext capabilityContext;

  @Override
  public TaskResult execute(TaskContext taskContext) throws TaskException {
    TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
    BuildLogger buildLogger = taskContext.getBuildLogger();
    ConfigurationMap configuration = taskContext.getConfigurationMap();
    // Map<String, String> environment = getEnvironment(taskContext);
    // Map<String, WhitelistedSerializable> runtimeTaskData = taskContext.getRuntimeTaskData();
    // Map<String, VariableDefinitionContext> effectiveVariables = taskContext.getBuildContext().getVariableContext().getEffectiveVariables();
    // Map<String, String> runtimeTaskContext = taskContext.getRuntimeTaskContext();
    // Multimap<String, Artifact> sharedArtifacts = taskContext.getBuildContext().getArtifactContext().getSharedArtifactsFromPreviousStages();
    // File workingDir = taskContext.getWorkingDirectory();
    String accessKey = configuration.get(S3UploadTaskConfigurator.ACCESS_KEY);
    String secretKey = configuration.get(S3UploadTaskConfigurator.SECRET_KEY);
    String region = configuration.get(S3UploadTaskConfigurator.REGION);
    String s3Bucket = configuration.get(S3UploadTaskConfigurator.S3_BUCKET);
    String file = configuration.get(S3UploadTaskConfigurator.FILE);
    String destinationKey = configuration.get(S3UploadTaskConfigurator.DESTINATION_KEY);
    if (StringUtils.isEmpty(destinationKey)) {
      destinationKey = file;
    }
    try {
      S3Proxy s3Proxy = new S3Proxy(accessKey, secretKey, region);
      s3Proxy.uploadToS3Bucket(file, destinationKey, s3Bucket);
      buildLogger.addBuildLogEntry("Sent " + file + " to S3 bucket " + s3Bucket + " as " + destinationKey);
      return builder.success().build();
    } catch (Exception e) {
      buildLogger.addErrorLogEntry("Unable to send " + file + " to S3 bucket " + s3Bucket + " as " + destinationKey, e);
      return builder.failed().build();
    }
  }

  private Map<String, String> getEnvironment(TaskContext taskContext) {
    String environment = taskContext.getConfigurationMap().get(ENVIRONMENT);
    return environmentVariableAccessor.splitEnvironmentAssignments(environment);
  }

}
