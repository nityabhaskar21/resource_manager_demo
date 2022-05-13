package com.example.resource_manager_demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Error {
	
	private String status ;
	private String msg;

}
