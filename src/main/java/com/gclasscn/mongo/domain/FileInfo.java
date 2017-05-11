package com.gclasscn.mongo.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "file_info")
public class FileInfo {

	@Id
	private String id;				//主键(mongodb自动生成)
	private String name;			//文件名称
	private Long size;				//文件长度
	@Field("upload_date")
	private Date uploadDate;		//上传时间
	@Field("content_type")
	private String contentType;		//文件格式
	@Field("file_id")
	private String fileId;			//原始文件ID
	@Field("pdf_id")
	private String pdfId;			//转换后的pdf文件ID
	@Field("image_id")
	private String imageId;			//pdf文件首页图片
	
	public FileInfo() {
		
	}
	
	public FileInfo(String name, Long size, Date uploadDate, String contentType, String fileId, String pdfId, String imageId) {
		this.name = name;
		this.size = size;
		this.uploadDate = uploadDate;
		this.contentType = contentType;
		this.fileId = fileId;
		this.pdfId = pdfId;
		this.imageId = imageId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getPdfId() {
		return pdfId;
	}
	public void setPdfId(String pdfId) {
		this.pdfId = pdfId;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", name=" + name + ", size=" + size + ", uploadDate=" + uploadDate
				+ ", contentType=" + contentType + ", fileId=" + fileId + ", pdfId=" + pdfId + ", imageId=" + imageId
				+ "]";
	}
	
}
