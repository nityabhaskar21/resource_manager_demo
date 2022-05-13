package com.example.resource_manager_demo.dto;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Document(collection = "stack.files")
public class Stacks {
	@Id
	private String stackId;
	private MetaData metadata;
	@JsonProperty("createdDate")
	private Date uploadDate;

}
