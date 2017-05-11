package com.gclasscn.mongo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.gridfs.GridFSDBFile;

public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	private static Set<String> set = new HashSet<>();
	
	public static File zip(List<GridFSDBFile> dbFiles) {
		File zipFile = new File(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".zip");
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
			set.clear();
			for (GridFSDBFile dbFile : dbFiles) {
				String filename = rename(dbFile.getFilename());
				try (InputStream in = dbFile.getInputStream()) {
					out.putNextEntry(new ZipEntry(filename));
					IOUtils.copy(in, out);
				}
			}
		} catch (IOException e) {
			logger.error("压缩文件失败: {}", e);
		}
		return zipFile;
	}

	private static String rename(String filename){
		boolean specified = set.add(filename);
		if(specified){
			return filename;
		}
		if(filename.indexOf(".") != -1){
			String prefix = filename.substring(0, filename.lastIndexOf("."));
			String suffix = filename.substring(filename.lastIndexOf("."), filename.length());
			return prefix + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + suffix;
		}
		return filename + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	}
}