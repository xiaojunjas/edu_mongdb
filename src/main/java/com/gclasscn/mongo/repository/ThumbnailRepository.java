package com.gclasscn.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gclasscn.mongo.domain.Thumbnail;

public interface ThumbnailRepository extends MongoRepository<Thumbnail, String>{
	
}
