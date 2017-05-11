package com.gclasscn.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gclasscn.mongo.domain.FileInfo;

public interface FileInfoRepository extends MongoRepository<FileInfo, String>{
	
}
