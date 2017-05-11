package com.gclasscn.mongo.domain;

public class UploadResponse {
	
	private String fileId;
	private String filename;
	
	public UploadResponse() {
		
	}

	public UploadResponse(String fileId, String filename){
		this.fileId = fileId;
		this.filename = filename;
	}
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
