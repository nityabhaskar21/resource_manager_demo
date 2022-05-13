package com.example.resource_manager_demo.docker;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;

@Service
public class DockerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DockerService.class);

	@Autowired
	private DockerClient dockerClient;

	@SuppressWarnings("unchecked")
	public LogStream runDockerContainer(Map<String, Object> dockerConfigMap) throws DockerException {
		LOGGER.info("running docker service.......");
		try {
			HostConfig hostConfig = HostConfig.builder().binds((String) dockerConfigMap.get("hostConfigBind")).build();
			String dockerImage = dockerConfigMap.get("imageName").toString();
			List<String> commands = (List<String>) dockerConfigMap.get("commands");
			var exportCreds = (List<String>) dockerConfigMap.get("exportCreds");

			ContainerConfig containerConfig = ContainerConfig.builder().image(dockerImage).hostConfig(hostConfig).cmd(commands)
					.env(exportCreds).build();

			final ContainerCreation creation = dockerClient.createContainer(containerConfig);
			final String id = creation.id();

			final ContainerInfo info = dockerClient.inspectContainer(id);
			dockerClient.startContainer(id);
			LogStream logs = dockerClient.logs(id, DockerClient.LogsParam.follow(), DockerClient.LogsParam.stdout(),
					DockerClient.LogsParam.stderr());
			return logs;
		} catch (Exception exception) {
			LOGGER.info("#### Exception inside runDockerContainer method !!!!!");
			String execMsg = exception.getMessage() == null ? "null" : exception.getMessage();
			LOGGER.info("error msg: {}", execMsg);
			throw new DockerException(execMsg);
		} finally {
			LOGGER.info("completed docker service");
		}
	}

}
