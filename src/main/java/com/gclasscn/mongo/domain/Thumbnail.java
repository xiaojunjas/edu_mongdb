package com.gclasscn.mongo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "thumbnail")
public class Thumbnail {

	@Id
	private String id;				//主键(mongodb自动生成)
	@Field("image_id")
	private String imageId;           //原图片id
	private String name;			//缩略图名称
	private String content;			//缩略图内容(base64编码字符串)
	
	public Thumbnail() {
		
	}
	
	public Thumbnail(String name, String imageId, String content) {
		this.imageId = imageId;
		this.name = name;
		this.content = content;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Thumbnail [id=" + id + ", imageId=" + imageId + ", name=" + name + ", content=" + content + "]";
	}

}
