package com.example.resource_manager_demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.var;

@Service
public class VariableParsingService {
	
	
	
	
	
	public void changeVariables()
	{
		String variableFile= "variable \"region\" {\r\n"
				+ "description = \"My subscription id\"\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "variable \"plan_id\" {\r\n"
				+ "description = \"My tenant id\",\r\n"
				+ "default  = \"\"\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "variable \"user_id\" {\r\n"
				+ "description = \"My user id\"\r\n"
				+ "}";
		
		HashMap<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("region", "us-east-2");
		variableMap.put("plan_id", "bjs123456");
		System.out.println("VAR FILE IS ----"+parseTfVariables(variableFile, variableMap));
		
	}
	
	

	public String parseTfVariables(String variableFile, Map<String, Object> variables) {
		Matcher variableMatcher = Pattern.compile("variable \"(\\S+?)\"\\s*\\{\\s*((?:.|\\n)+?)\\s*}")
				.matcher(variableFile);
		Pattern bodyElementPattern = Pattern.compile("(\\S+)\\s*=\\s*\"(.+)\"");
		String  str = "";
			while (variableMatcher.find()) {
				for (var entry : variables.entrySet()) {
				String key = variableMatcher.group(1).trim();
				if (key.equals(entry.getKey())) {	
					if(variableMatcher.group(2).contains("default"))
					{
						
					}
							{
						
							}
					str = str.concat("variable "+'"'+entry.getKey()+'"'+" {\n");
					
					str= str.concat(variableMatcher.group(2).trim().concat("\n"+"default = "+entry.getValue().toString()));
					str = str.concat("\n"+"}\n");	
				}
				}
			}

			System.out.println("SSS is si"+str);
		return variableFile;
	}
}
