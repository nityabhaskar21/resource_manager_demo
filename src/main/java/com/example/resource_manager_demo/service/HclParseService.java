package com.example.resource_manager_demo.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bertramlabs.plugins.hcl4j.HCLParser;
import com.bertramlabs.plugins.hcl4j.HCLParserException;
import com.google.gson.Gson;

@Service
public class HclParseService {

	public Map<String, Object> HclParse() throws HCLParserException, IOException {
		File terraformFile = new File("D:\\tf-zipDemo\\variables.tf");
		var map = new HCLParser().parse(terraformFile, "UTF-8");
		LinkedHashMap<String, Map >  linkMap = (LinkedHashMap<String, Map>) map.get("variable");
		System.out.println("MAP value for description ----"+linkMap.get("region").put("default", "us-esat-2"));
		System.out.println("MAP is ----"+map);
		String mapString = StringUtils.join(map);
		mapString=mapString.replaceAll("\\{", "");
		mapString=mapString.replaceAll("\\}", "");
		mapString=mapString.replaceAll(",","");
		System.out.println("As a String ----"+mapString);
		
		var json =new Gson().toJson(map);
		System.out.println("new Json Is "+ json);
		return map;
	}

}
