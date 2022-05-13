package com.example.resource_manager_demo;

import java.net.URI;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;

@SpringBootApplication
public class ResourceManagerDemoApplication {

	@Value("${cloud.certPath}")
	private String certificationPath;

	@Value("${cloud.host}")
	private String uriHost;
	
	public static void main(String[] args) {
		SpringApplication.run(ResourceManagerDemoApplication.class, args);
	}

	@Bean
	DockerClient dockerClient() {
		String certPath = String.format("%s%s", System.getProperty("user.home"), certificationPath);
		DockerClient docker = null;
		try {
			docker = DefaultDockerClient.builder().uri(URI.create(uriHost))
					.dockerCertificates(new DockerCertificates(Paths.get(certPath))).build();
		} catch (DockerCertificateException e) {
			System.out.println(e.getMessage());
			System.out.println("system shutdown because docker certificaton error");
			System.exit(1);
		}
		return docker;
	}

}
