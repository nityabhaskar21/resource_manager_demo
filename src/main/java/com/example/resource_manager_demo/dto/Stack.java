package com.example.resource_manager_demo.dto;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "stack.files")
public class Stack {
	
	private String stackName;
	private String description;
	private String provider;
	private String accountId;
	private String state;
	private Date createdDate;
}
