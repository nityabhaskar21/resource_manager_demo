package com.example.resource_manager_demo.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AzureDockerService {

	@Value("${azure.mount_path.for.azure_work}")
	private String azureMountPathForAzureWork;

	@Autowired
	DockerService dockerService;

	private final String MOUNT_DATA = "mount_data";
	private final String SUBSCRIPTION_ID = "subscriptionId";
	private final String CLI_EXECUTION = "cli_execution";
	private final String DOCKER_IMAGE = "docker_image";

	public void runContainer(String terraformFolderName, Map<String, String> azureConfigurationMap,
			Map<String, Object> azureCreds) {
		var dockerConfigMap = buildDockerConfigMapForSubscription(terraformFolderName, azureConfigurationMap,
				azureCreds);

	}

	private Map<String, Object> buildDockerConfigMapForSubscription(String terraformFolderName,
			Map<String, String> azureConfigurationMap, Map<String, Object> azureCreds) {
		var exportCreds = List.of();
		var commands = getCommandsListForAzureDockerSubscriptionType(azureCreds,
				azureConfigurationMap.get(CLI_EXECUTION));
		var hostConfigBind = getHostConfigBindForAzureDocker(terraformFolderName);

		Map<String, Object> configMap = new HashMap<>();
		configMap.put("exportCreds", exportCreds);
		configMap.put("imageName", azureConfigurationMap.get(DOCKER_IMAGE));
		configMap.put("commands", commands);
		configMap.put("hostConfigBind", hostConfigBind);
		return configMap;
	}

	private List<String> getCommandsListForAzureDockerSubscriptionType(Map<String, Object> azureCreds,
			String cliCommand) {

		String azloginString = String.format("az login --service-principal --username %s --tenant %s --password %s",
				azureCreds.get("client_id").toString(), azureCreds.get("tenant_id").toString(),
				azureCreds.get("client_secret").toString());
		List<String> commands = new ArrayList<>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add("cd ../mount_data/ ;" + azloginString + ";" + cliCommand);

		log.info("commandsList is: {}", commands);
		return commands;

	}

	private List<String> getCommandsListForAzureDocker(String resourceType, Map<String, Object> cliExecution) {
		List<String> commands = new ArrayList<>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add("python3 discover.py");

		log.info("commandsList is: {}", commands);
		return commands;

	}
	private String getHostConfigBindForAzureDocker(String terraformFolderName) {
		return String.format("%s/%s:/%s", azureMountPathForAzureWork, terraformFolderName, MOUNT_DATA);
	}
}
