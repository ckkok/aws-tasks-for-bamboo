package com.example.plugins.aws.elasticbeanstalk;

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
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class ElasticBeanstalkDeploymentTask implements TaskType {

  private static final String ENVIRONMENT = "environmentVariables";

  @Autowired
  @ComponentImport
  private EnvironmentVariableAccessor environmentVariableAccessor;

  @Autowired
  @ComponentImport
  private CapabilityContext capabilityContext;

  @Override
  public TaskResult execute(TaskContext taskContext) throws TaskException {
    TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext).failed();
    BuildLogger buildLogger = taskContext.getBuildLogger();
    buildLogger.addBuildLogEntry("Deployment plugin test");
    ConfigurationMap configuration = taskContext.getConfigurationMap();
    buildLogger.addBuildLogEntry("===== Configured Parameters =====");
    configuration.forEach((k, v) -> {
      buildLogger.addBuildLogEntry(k + ", " + v);
    });
    Map<String, String> environment = getEnvironment(taskContext);
    buildLogger.addBuildLogEntry("===== Environment Variables =====");
    for (Map.Entry<String, String> entry : environment.entrySet()) {
      buildLogger.addBuildLogEntry("Env: key - " + entry.getKey() + ", value - " + entry.getValue());
    }
    buildLogger.addBuildLogEntry("===== Runtime Task Data =====");
    if (Objects.nonNull(taskContext.getRuntimeTaskData())) {
      taskContext.getRuntimeTaskData().forEach((k, v) -> {
        buildLogger.addBuildLogEntry(k + ", " + v.toString());
      });
    }
    buildLogger.addBuildLogEntry("===== Effective Variables =====");
    taskContext.getBuildContext().getVariableContext().getEffectiveVariables().entrySet().forEach(entry -> {
      buildLogger.addBuildLogEntry(entry.getKey() + ", " + entry.getValue().getValue());
    });
    buildLogger.addBuildLogEntry("===== Shared Artifacts From Previous Stages =====");
    taskContext.getBuildContext().getArtifactContext().getSharedArtifactsFromPreviousStages().entries().forEach(entry -> {
      buildLogger.addBuildLogEntry(entry.getKey() + ", " + entry.getValue().getLabel() + ", " + entry.getValue().getLinkType());
    });
    taskContext.getBuildContext().getArtifactContext().getPublishingResults().forEach(result -> {
      buildLogger.addBuildLogEntry("Publishing result location - " + result.getArtifactDefinitionContext().getLocation());
    });
    buildLogger.addBuildLogEntry("===== Artifact Context - Subscriptions =====");
    taskContext.getBuildContext().getArtifactContext().getSubscriptionContexts().forEach(context -> {
      buildLogger.addBuildLogEntry("Name - " + context.getArtifactDefinitionContext().getName());
      buildLogger.addBuildLogEntry("Location - " + context.getArtifactDefinitionContext().getLocation());
      buildLogger.addBuildLogEntry("Effective path - " + context.getEffectiveDestinationPath());
    });
    File workingDir = taskContext.getWorkingDirectory();
    buildLogger.addBuildLogEntry("Working dir - " + workingDir.getAbsolutePath());
    File[] files = workingDir.listFiles();
    if (Objects.nonNull(files)) {
      Arrays.asList(files).forEach(file -> {
        buildLogger.addBuildLogEntry("File: " + file.getPath());
      });
    }
    File rootDir = taskContext.getRootDirectory();
    buildLogger.addBuildLogEntry("Root dir - " + rootDir.getAbsolutePath());
    return builder.success().build();
  }

  private Map<String, String> getEnvironment(TaskContext taskContext) {
    String environment = taskContext.getConfigurationMap().get(ENVIRONMENT);
    return environmentVariableAccessor.splitEnvironmentAssignments(environment);
  }

}
