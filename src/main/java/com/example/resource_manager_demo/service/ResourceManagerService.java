package com.example.resource_manager_demo.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.resource_manager_demo.dto.LoadFile;
import com.example.resource_manager_demo.dto.Stack;
import com.example.resource_manager_demo.dto.Stacks;
import com.example.resource_manager_demo.exception.FileVaildationException;
import com.example.resource_manager_demo.repo.ResourceManagerRepo;

@Service
public class ResourceManagerService {

	@Value("${temp.file.validation.path}")
	private String tempFileValidationPath;

	@Autowired
	ResourceManagerRepo resourceManagerRepo;

	public String uploadFile(String stackName, String provider, String description, MultipartFile file,
			String terrafromVersion, String accountId, String orgId) throws IOException {

		String fileName = "";
		Object fileId;
		try {
			var loadFile = unzipFile(file, tempFileValidationPath);
			fileName = loadFile.getFilename().replace(".zip", "");
			folderVaildation(fileName);
			fileValidation(fileName);
			tfStateFileValidation(fileName);
			fileId = resourceManagerRepo.addFiletoDb(stackName, provider, description, file, terrafromVersion,
					accountId, orgId);
		} finally {
			FileUtils.deleteDirectory(new File(String.format("%s/%s", tempFileValidationPath, fileName)));
		}
		return fileId.toString();
	}

	private LoadFile unzipFile(MultipartFile zipFile, String unZipingPath) throws IOException {
		LoadFile loadFile = new LoadFile();
		if (zipFile != null) {
			loadFile.setFilename(zipFile.getOriginalFilename());
			loadFile.setFileType(zipFile.getContentType());
			loadFile.setFileSize(String.valueOf(zipFile.getSize()));
			loadFile.setFile(IOUtils.toByteArray(zipFile.getInputStream()));
		}
		ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(loadFile.getFile()));
		Path path = Paths.get(unZipingPath);
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

	private void folderVaildation(String fileName) {
		File filePath = new File(String.format("%s/%s", tempFileValidationPath, fileName));
		String[] files = filePath.list();
		for (String folder : files) {
			if (new File(String.format("%s/%s/", tempFileValidationPath, fileName) + folder).isDirectory()
					&& folder.endsWith(".terraform")) {
				throw new FileVaildationException(
						"Subdirectory with .terraform extension is not expected in this File.");
			}
		}

	}

	private void fileValidation(String fileName) {
		File filePath = new File(String.format("%s/%s", tempFileValidationPath, fileName));
		File[] files = filePath.listFiles();
		int tfFileCount = 0;
		for (File file : files) {
			if (file.getName().endsWith(".tf")) {
				tfFileCount++;
			}
		}
		if (tfFileCount == 0) {
			throw new FileVaildationException("Atleast one terraform file with .tf extension is required to process");
		}
	}

	private void tfStateFileValidation(String fileName) {
		File filePath = new File(String.format("%s/%s", tempFileValidationPath, fileName));
		File[] files = filePath.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".tfstate")) {
				throw new FileVaildationException("State files with .tfstate extension is not accepected.");
			}
		}
	}

	public Stack getStackDetails(String stackId) {
		var stackDetails = resourceManagerRepo.getStackDetails(stackId);
		return stackDetails;
	}
	public List<Stacks> getAllStacksForOrg(String orgId) {
		var stacks = resourceManagerRepo.getAllStacks(orgId);
		return stacks;
	}
	
	
	
	public void performTerraformAction(String stackId, String action)
	{
		
	}
}
