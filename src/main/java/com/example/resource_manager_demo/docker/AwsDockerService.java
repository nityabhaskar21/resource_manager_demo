package com.example.resource_manager_demo.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.spotify.docker.client.exceptions.DockerException;

@Service
public class AwsDockerService {

	@Value("${aws.mount.path}")
	private String awsMountPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsDockerService.class);

	@Autowired
	private DockerService dockerService;

	private final String MOUNT_DATA = "mount_data";

	public Object runContainerForResourceProvisioning(Map<String, Object> awsCreds, String region,
			String terraformFolderName, String imageName) throws DockerException {
		var dockerConfigMap = buildDockerConfigMap(terraformFolderName, awsCreds, region, imageName);

		dockerService.runDockerContainer(dockerConfigMap);

		return null;

	}

	private Map<String, Object> buildDockerConfigMap(String terraformFolderName, Map<String, Object> awsCreds,
			String region, String imageName) {
		var exportCreds = getExportCredsForAwsDocker(awsCreds, region);
		var commands = getCommandsListForAwsDocker();
		var hostConfigBind = getHostConfigBindForAwsDocker(terraformFolderName);

		Map<String, Object> configMap = new HashMap<>();
		configMap.put("exportCreds", exportCreds);
		configMap.put("imageName", imageName);
		configMap.put("commands", commands);
		configMap.put("hostConfigBind", hostConfigBind);
		return configMap;
	}

	private List<String> getExportCredsForAwsDocker(Map<String, Object> awsCreds, String region) {
		var exportCreds = List.of("AWS_ACCESS_KEY_ID=" + awsCreds.get("access_key").toString(),
				"AWS_SECRET_ACCESS_KEY=" + awsCreds.get("secret_key").toString(), "AWS_DEFAULT_REGION=" + region);
		LOGGER.info("exportCred: {}", exportCreds);
		return exportCreds;

	}

	private List<String> getCommandsListForAwsDocker() {
		List<String> commands = new ArrayList<>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add("python3 apply.py");
		LOGGER.info("commandsList is: {}", commands);
		return commands;

	}

	private String getHostConfigBindForAwsDocker(String terraformFolderName) {
		return String.format("%s/%s:/%s", awsMountPath, terraformFolderName, MOUNT_DATA);
	}

}
