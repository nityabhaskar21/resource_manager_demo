package com.example.resource_manager_demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.resource_manager_demo.dto.Error;
import com.example.resource_manager_demo.exception.FileVaildationException;
import com.example.resource_manager_demo.service.ResourceManagerService;

@RestController
@CrossOrigin
@RequestMapping("/resource-manager")
public class RersourceManagerController {

	@Autowired
	ResourceManagerService resourceManagerService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String terraformVersion)
			throws IOException {
		try {
			String orgId ="";
			var response = resourceManagerService.uploadFile(terraformVersion, terraformVersion, terraformVersion, file,
					terraformVersion, terraformVersion,null);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (FileVaildationException e) {
			var errorResponse = Error.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).msg(e.getMessage())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/stack")
	public ResponseEntity<?> createStack(@RequestParam String stackName, @RequestParam String provider,
			@RequestParam String description, @RequestParam("terraConfigfile") MultipartFile terraConfigfile,
			@RequestParam String terraformVersion, @RequestParam String accountId) throws IOException {
		try {
			String orgId  ="abc12345";
			var response = resourceManagerService.uploadFile(stackName, provider, description, terraConfigfile,
					terraformVersion, accountId,orgId);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (FileVaildationException e) {
			var errorResponse = Error.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).msg(e.getMessage())
					.build();
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/stacks")
	public ResponseEntity<?> getAllStacksForOrg() {
		var orgId = "abc123";
		var response = resourceManagerService.getAllStacksForOrg(orgId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/stack/{stackId}")
	public ResponseEntity<?> getStackDetails(@PathVariable String stackId) {
		var response = resourceManagerService.getStackDetails(stackId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	public ResponseEntity<?> performTerraformAction(@RequestParam String stackId, @RequestParam String action )
	{
		
		return null;
	}

}
