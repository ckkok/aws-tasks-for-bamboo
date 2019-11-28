package com.example.plugins.aws.s3upload;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atlassian.struts.TextProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class S3UploadTaskConfigurator extends AbstractTaskConfigurator {

  public static final String TASK_NAME = "s3UploadTask";
  public static final String ACCESS_KEY = "accessKey";
  public static final String SECRET_KEY = "secretKey";
  public static final String REGION = "region";
  public static final String S3_BUCKET = "s3Bucket";
  public static final String FILE = "file";
  public static final String DESTINATION_KEY = "destinationKey";
  private static final String ERROR_KEY = "error";
  private static final String SEP = ".";

  private static final List<String> ALL_PARAMS = Arrays.asList(ACCESS_KEY, SECRET_KEY, REGION, S3_BUCKET, FILE, DESTINATION_KEY);
  private static final List<String> NON_EMPTY_PARAMS = Arrays.asList(ACCESS_KEY, SECRET_KEY, REGION, S3_BUCKET, FILE);

  @Autowired
  @ComponentImport
  private TextProvider textProvider;

  @NotNull
  @Override
  public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, TaskDefinition previousTaskDefinition) {
    Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
    ALL_PARAMS.forEach(param -> config.put(param, params.getString(param)));
    return config;
  }

  @Override
  public void populateContextForCreate(@NotNull Map<String, Object> context) {
    super.populateContextForCreate(context);
    ALL_PARAMS.forEach(param -> context.put(param, ""));
  }

  @Override
  public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
    super.populateContextForEdit(context, taskDefinition);
    Map<String, String> configuration = taskDefinition.getConfiguration();
    ALL_PARAMS.forEach(param -> context.put(param, configuration.get(param)));
  }

  @Override
  public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
    super.validate(params, errorCollection);
    NON_EMPTY_PARAMS.forEach(param -> {
      if (StringUtils.isEmpty(params.getString(param))) {
        errorCollection.addError(param, textProvider.getText(TASK_NAME + SEP + param + SEP + ERROR_KEY));
      }
    });
  }

}
