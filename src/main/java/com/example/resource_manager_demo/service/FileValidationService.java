package com.example.resource_manager_demo.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.resource_manager_demo.dto.LoadFile;
import com.example.resource_manager_demo.exception.FileVaildationException;

@Service
public class FileValidationService {

	
	@Value("${temp.file.validation.path}")
	private String tempFileValidationPath;
	
	
	
	public void folderVaildation(MultipartFile zipFile) {
		File file = new File("D:\\rm_file_validation\\tf-zipDemo");
		String[] names = file.list();
		for (String name : names) {
			if (new File("D:\\rm_file_validation\\tf-zipDemo\\" + name).isDirectory()) {
				throw new FileVaildationException("Subfolder is not expected in this File.");
			}
		}

	}
	public void fileValidation() {
		File file = new File("D:\\rm_file_validation\\tf-zipDemo");
		File[] file_content = file.listFiles();
		if ((file_content.length >= 1)) {

			if (file_content.length == 1) {
				for (File files : file_content) {
					if (!files.getName().endsWith(".tf")) {
						throw new FileVaildationException("Only terraform files ends with .tf is vaild");
					}
				}
			}
		} else {
			throw new FileVaildationException("Atleast one terraform file required to process");
		}
	}
	
	public void tfStateFileValidation()
	{
		File file = new File("D:\\rm_file_validation\\tf-zipDemo");
		File[] file_content = file.listFiles();
		for (File files : file_content)
		{
			if(files.getName().endsWith(".tfstate"))
			{
				throw new FileVaildationException("State files with .tfstate is not accepected.");
			}
		}
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
}
