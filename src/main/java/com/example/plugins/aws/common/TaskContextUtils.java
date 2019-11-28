package com.example.plugins.aws.common;

import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.task.TaskContext;

import java.util.Map;

public class TaskContextUtils {
	
	private static final String ENVIRONMENT = "environmentVariables";
	
	public static Map<String, String> getEnvironment(EnvironmentVariableAccessor environmentVariableAccessor, TaskContext taskContext) {
		String environment = taskContext.getConfigurationMap().get(ENVIRONMENT);
		return environmentVariableAccessor.splitEnvironmentAssignments(environment);
	}
}
