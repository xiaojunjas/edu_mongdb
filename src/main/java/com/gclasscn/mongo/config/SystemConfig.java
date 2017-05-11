package com.gclasscn.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class SystemConfig{
	
	private String officePath;
	private int officePort;
	private String[] fileTypes;
	private String[] imgTypes;
	private String[] imgSizes;

	public String getOfficePath() {
		return officePath;
	}

	public void setOfficePath(String officePath) {
		this.officePath = officePath;
	}

	public int getOfficePort() {
		return officePort;
	}

	public void setOfficePort(int officePort) {
		this.officePort = officePort;
	}

	public String[] getFileTypes() {
		return fileTypes;
	}

	public void setFileTypes(String[] fileTypes) {
		this.fileTypes = fileTypes;
	}

	public String[] getImgTypes() {
		return imgTypes;
	}

	public void setImgTypes(String[] imgTypes) {
		this.imgTypes = imgTypes;
	}

	public String[] getImgSizes() {
		return imgSizes;
	}

	public void setImgSizes(String[] imgSizes) {
		this.imgSizes = imgSizes;
	}

}
