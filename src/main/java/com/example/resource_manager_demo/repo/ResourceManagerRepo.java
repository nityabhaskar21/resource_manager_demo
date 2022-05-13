package com.example.resource_manager_demo.repo;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.example.resource_manager_demo.dto.Stack;
import com.example.resource_manager_demo.dto.Stacks;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

@Repository
public class ResourceManagerRepo {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private GridFsOperations operations;

	@Autowired
	MongoTemplate mongoTemplate;


	public String addFiletoDb(String stackName, String provider, String description, MultipartFile file,
			String terrafromVersion, String accountId, String orgId) throws IOException {
		

		DBObject metadata = new BasicDBObject();
		metadata.put("stackName", stackName);
		metadata.put("provider", provider);
		metadata.put("description", description);
		metadata.put("accountId", accountId);
		metadata.put("fileSize", file.getSize());
		metadata.put("state", "ACTIVE");
		metadata.put("orgId", orgId);
		metadata.put("terraformVersion", terrafromVersion);
		var fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(),
				metadata);
		return fileId.toString();
	}

	public Stack getStackDetails(String stackId) {
		GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(stackId)));
		Stack stack = new Stack();
		stack.setStackName(gridFSFile.getMetadata().get("stackName").toString());
		stack.setDescription(gridFSFile.getMetadata().get("description").toString());
		stack.setProvider(gridFSFile.getMetadata().get("provider").toString());
		stack.setAccountId(gridFSFile.getMetadata().get("accountId").toString());
		stack.setState(gridFSFile.getMetadata().get("state").toString());
		stack.setCreatedDate(gridFSFile.getUploadDate());
		return stack;
	}

	public List<Stacks> getAllStacks(String orgId) {
		var query = new Query();
		query.addCriteria(Criteria.where("metadata.orgId").is(orgId));
		return mongoTemplate.find(query, Stacks.class, "fs.files");

	}
	
	
	

}
