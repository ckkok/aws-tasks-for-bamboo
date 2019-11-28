package com.example.plugins.aws.elasticbeanstalk;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskConfiguratorHelper;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.struts.TextProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class ElasticBeanstalkDeploymentTaskConfigurator extends AbstractTaskConfigurator {

  public static final String TASK_NAME = "ebDeploymentTask";
  public static final String ACCESS_KEY = "accessKey";
  public static final String SECRET_KEY = "secretKey";
  public static final String REGION = "region";
  public static final String APPLICATION_NAME = "applicationName";
  public static final String ENVIRONMENT_NAME = "environmentName";
  public static final String S3_KEY = "s3Key";
  public static final String S3_BUCKET = "s3Bucket";
  public static final String VERSION_LABEL = "versionLabel";
  public static final String DEPLOYMENT_ACTION = "deploymentAction";
  public static final String DEPLOYMENT_ACTIONS = "deploymentActions";
  public static final String DEFAULT_DEPLOYMENT_ACTION = "create-application-version";
  private static final String ERROR_KEY = "error";
  private static final String SEP = ".";
  private static final String CTX_UI_CONFIG_BEAN = "uiConfigBean";

  private static final List<String> ALL_PARAMS = Arrays.asList(ACCESS_KEY, SECRET_KEY, REGION, APPLICATION_NAME, ENVIRONMENT_NAME, S3_KEY, S3_BUCKET, VERSION_LABEL, DEPLOYMENT_ACTION);
  private static final List<String> NON_EMPTY_PARAMS = Arrays.asList(ACCESS_KEY, SECRET_KEY, REGION, APPLICATION_NAME, ENVIRONMENT_NAME, S3_KEY, S3_BUCKET, VERSION_LABEL, DEPLOYMENT_ACTION);

  @Autowired
  @ComponentImport
  private TextProvider textProvider;
  
  @Autowired
  @ComponentImport
  private UIConfigSupport uiConfigSupport;
  
  @Autowired
  @ComponentImport
  private TaskConfiguratorHelper taskConfiguratorHelper;

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
    populateDeploymentActions(context);
    context.put(DEPLOYMENT_ACTION, DEFAULT_DEPLOYMENT_ACTION);
  }

  @Override
  public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
    super.populateContextForEdit(context, taskDefinition);
    Map<String, String> configuration = taskDefinition.getConfiguration();
    ALL_PARAMS.forEach(param -> context.put(param, configuration.get(param)));
    populateDeploymentActions(context);
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
  
  private void populateDeploymentActions(Map<String, Object> context) {
    context.put(DEPLOYMENT_ACTIONS, getDeploymentActionsMap());
    context.put(CTX_UI_CONFIG_BEAN, uiConfigSupport);
  }
  
  private Map<String, String> getDeploymentActionsMap() {
    Map<String, String> actionsMap = new LinkedHashMap<>();
    actionsMap.put("create-application", "Create Application");
    actionsMap.put("create-application-version", "Create Application Version");
    actionsMap.put("create-configuration-template", "Create Configuration Template");
    actionsMap.put("create-environment", "Create Environment for Application");
    actionsMap.put("create-storage-location", "Create Storage Location");
    actionsMap.put("update-application", "Update Application");
    actionsMap.put("update-application-version", "Update Application Version");
    actionsMap.put("update-configuration-template", "Update Configuration Template");
    actionsMap.put("update-environment", "Update Environment for Application");
    actionsMap.put("delete-application", "Delete Application");
    actionsMap.put("delete-application-version", "Delete Application Version");
    actionsMap.put("delete-configuration-template", "Delete Configuration Template");
    actionsMap.put("delete-environment-configuration", "Delete Environment Configuration");
    return actionsMap;
  }

}
