package com.example.resource_manager_demo.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.message.internal.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.resource_manager_demo.docker.AwsDockerService;
import com.example.resource_manager_demo.docker.DockerService;
import com.example.resource_manager_demo.dto.LoadFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.spotify.docker.client.exceptions.DockerException;

import ch.qos.logback.classic.pattern.Util;

@Service
public class FileService {

	@Autowired
	private GridFsTemplate template;

	@Autowired
	private GridFsOperations operations;

	@Autowired
	DockerService dockerService;

	@Autowired
	private AwsDockerService awsDockerService;
	
	
	@Value("${aws.docker.image.name}")
	private String awsDockerImageName;

	public String addFile(MultipartFile upload, String terrafromVersion) throws IOException {

		DBObject metadata = new BasicDBObject();
		metadata.put("fileSize", upload.getSize());
		metadata.put("terraformVersion", terrafromVersion);
		Object fileID = template.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(),
				metadata);
		return fileID.toString();
	}

	public LoadFile downloadFile(String id) throws IOException {
		GridFSFile gridFSFile = template.findOne(new Query(Criteria.where("_id").is(id)));
		LoadFile loadFile = new LoadFile();
		if (gridFSFile != null && gridFSFile.getMetadata() != null) {
			loadFile.setFilename(gridFSFile.getFilename());
			loadFile.setFileType(gridFSFile.getMetadata().get("_contentType").toString());
			loadFile.setFileSize(gridFSFile.getMetadata().get("fileSize").toString());
			loadFile.setFile(IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()));
		}
		ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(loadFile.getFile()));
		Path path = Paths.get("D:\\unzip_demo");
		for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null;) {
			Path resolvedPath = path.resolve(entry.getName());
			if (!entry.isDirectory()) {
				Files.createDirectories(resolvedPath.getParent());
				Files.copy(inputStream, resolvedPath);
			} else {
				Files.createDirectories(resolvedPath);
			}
		}
		return loadFile;
	}

	public void unzipValidation(MultipartFile zipFile) throws IOException {
		LoadFile loadFile = new LoadFile();
		if (zipFile != null) {
			loadFile.setFilename(zipFile.getOriginalFilename());
			loadFile.setFileType(zipFile.getContentType().toString());
			loadFile.setFileSize(String.valueOf(zipFile.getSize()));
			loadFile.setFile(IOUtils.toByteArray(zipFile.getInputStream()));
		}
		ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(loadFile.getFile()));
		Path path = Paths.get("D:\\rm_file_validation");
		for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null;) {
			Path resolvedPath = path.resolve(entry.getName());
			if (!entry.isDirectory()) {
				Files.createDirectories(resolvedPath.getParent());
				Files.copy(inputStream, resolvedPath);
			} else {
				Files.createDirectories(resolvedPath);
			}
		}

	}

	public void unZipFile(String id) throws IOException, DockerException {
		GridFSFile gridFSFile = template.findOne(new Query(Criteria.where("_id").is(id)));
		LoadFile loadFile = new LoadFile();
		if (gridFSFile != null && gridFSFile.getMetadata() != null) {
			loadFile.setFilename(gridFSFile.getFilename());
			loadFile.setFileType(gridFSFile.getMetadata().get("_contentType").toString());
			loadFile.setFileSize(gridFSFile.getMetadata().get("fileSize").toString());
			loadFile.setFile(IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()));
		}

		ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(loadFile.getFile()));
		Path path = Paths.get("D:\\unzip_demo");
		for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null;) {
			Path resolvedPath = path.resolve(entry.getName());
			if (!entry.isDirectory()) {
				Files.createDirectories(resolvedPath.getParent());
				Files.copy(inputStream, resolvedPath);
			} else {
				Files.createDirectories(resolvedPath);
			}
		}

	}

	public void runContainer() throws DockerException {
		var credsMap = new HashMap<String, Object>();
		credsMap.put("access_key", "AKIAS7GGEDCKJH4ZR4A6");
		credsMap.put("secret_key", "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");
		awsDockerService.runContainerForResourceProvisioning(credsMap, "us-east-2", "tf-zip-folder",
				awsDockerImageName);
	}

}
