package com.example.resource_manager_demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bertramlabs.plugins.hcl4j.HCLParserException;
import com.example.resource_manager_demo.dto.LoadFile;
import com.example.resource_manager_demo.service.FileService;
import com.example.resource_manager_demo.service.FileValidationService;
import com.example.resource_manager_demo.service.HclParseService;
import com.example.resource_manager_demo.service.VariableParsingService;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class DemoController {

	@Autowired
	private FileService fileService;
	 
	@Autowired
	FileValidationService fileValidationService;
	
	@Autowired
	VariableParsingService variableParsingService;
	
	
	@Autowired
	HclParseService hclParseService;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam String terraformVersion)
			throws IOException {
		return new ResponseEntity<>(fileService.addFile(file, terraformVersion), HttpStatus.OK);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
		LoadFile loadFile = fileService.downloadFile(id);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(loadFile.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
				.body(new ByteArrayResource(loadFile.getFile()));
	}

	@GetMapping("/unzip/{id}")
	public ResponseEntity<?> unzipFile(@PathVariable String id) throws IOException, DockerException {
		fileService.unZipFile(id);
		return new ResponseEntity<>("File Unzipped SuccessFully", HttpStatus.OK);
	}
	
	@GetMapping("/run")
	public ResponseEntity<?> runContainer() throws IOException, DockerException, DockerCertificateException {
		fileService.runContainer();
		return new ResponseEntity<>("Docker container created SuccessFully", HttpStatus.OK);
	}
	
	
	@GetMapping("/validation")
	public ResponseEntity<?> unzipFileValidation(@RequestParam("file") MultipartFile file,
			@RequestParam String terraformVersion) throws IOException, DockerException, DockerCertificateException {
		fileService.unzipValidation(file);
		return new ResponseEntity<>("File Unzipped SucessFully for Validation", HttpStatus.OK);
	}

	@GetMapping("/folder/validation")
	public ResponseEntity<?> unzipFolderValidation(@RequestParam("file") MultipartFile file,
			@RequestParam String terraformVersion) throws IOException, DockerException, DockerCertificateException {
		fileValidationService.folderVaildation(file);
		return new ResponseEntity<>("File is Vaild", HttpStatus.OK);
	}
	
	@GetMapping("/file/validation")
	public ResponseEntity<?> unzippedFileValidation() throws IOException, DockerException, DockerCertificateException {
		fileValidationService.fileValidation();
		return new ResponseEntity<>("File is Vaild", HttpStatus.OK);
	}
	
	@GetMapping("/tfstate/validation")
	public ResponseEntity<?> unzippedtfStateFileValidation() throws IOException, DockerException, DockerCertificateException {
		fileValidationService.tfStateFileValidation();
		return new ResponseEntity<>("File is Vaild", HttpStatus.OK);
	}
	
	@GetMapping("/test/parseFile")
	public ResponseEntity<?> testVariableChange() throws HCLParserException, IOException {
		hclParseService.HclParse();
		//variableParsingService.changeVariables();
		return new ResponseEntity<>("File is Vaild", HttpStatus.OK);
	}
	
	
}
