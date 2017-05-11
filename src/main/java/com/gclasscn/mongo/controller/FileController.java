package com.gclasscn.mongo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gclasscn.mongo.config.SystemConfig;
import com.gclasscn.mongo.domain.FileInfo;
import com.gclasscn.mongo.domain.Thumbnail;
import com.gclasscn.mongo.domain.UploadResponse;
import com.gclasscn.mongo.repository.ThumbnailRepository;
import com.gclasscn.mongo.task.Text2PdfTask;
import com.gclasscn.mongo.utils.FileUtil;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@RestController
@RequestMapping("/v1")
public class FileController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SystemConfig config;
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ThumbnailRepository thumbnailRepository;
	@Autowired
	private Text2PdfTask pdfTask;

	/**
	 * 文件上传
	 */
	@RequestMapping(value = "/files", method = RequestMethod.POST)
	public UploadResponse uploadFile(MultipartFile rawFile) {
		try {
			InputStream content = rawFile.getInputStream();
			GridFSFile file = gridFsTemplate.store(content, rawFile.getOriginalFilename(), rawFile.getContentType()); // 保存原文件
			if (convertible(rawFile.getOriginalFilename())) {
				pdfTask.execute(content, file);
			}
			if (imgFile(rawFile.getOriginalFilename())) {
				List<Thumbnail> thumbnails = pdfTask.generateThumbnails(ImageIO.read(rawFile.getInputStream()), file.getId().toString());
				thumbnailRepository.insert(thumbnails);
			}
			return new UploadResponse(file.getId().toString(), file.getFilename());
		} catch (IOException e) {
			logger.error("failed to upload file: {}", rawFile.getOriginalFilename(), e);
			return new UploadResponse();
		}
	}
	
	/**
	 * 文件删除
	 */
	@RequestMapping(value = "/files/fileid/{fileId}", method = RequestMethod.DELETE)
	public Integer deleteFile(@PathVariable String fileId) {
		FileInfo fileInfo = mongoTemplate.findOne(new Query(Criteria.where("file_id").is(fileId)), FileInfo.class);
		//删除文件
		gridFsTemplate.delete(new Query(Criteria.where("_id").in(new Object[]{fileId, fileInfo.getPdfId(), fileInfo.getImageId()})));
		//删除文件描述与缩略图
		mongoTemplate.remove(new Query(Criteria.where("file_id").is(fileId)), FileInfo.class);
		mongoTemplate.remove(new Query(Criteria.where("image_id").is(fileId)), Thumbnail.class);
		return HttpStatus.NO_CONTENT.value();
	}

	/**
	 * 原始图片预览
	 */
	@RequestMapping(value = "/files/images/{imageId}", method = RequestMethod.GET)
	public void preview(@PathVariable("imageId") String imageId, HttpServletRequest request, HttpServletResponse response) {
		GridFSDBFile dbFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(imageId)));
		if(Objects.isNull(dbFile)){
			return ;
		}
		setHeaders(request, response, "inline", dbFile.getLength(), dbFile.getFilename(), dbFile.getContentType());
		try (OutputStream out = response.getOutputStream(); InputStream in = dbFile.getInputStream();) {
			IOUtils.copy(in, out);
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("Error writing file to output stream. Filename is '{}'", dbFile.getFilename(), e);
		}
	}

	/**
	 * 缩略图预览
	 */
	@RequestMapping(value = "/files/images/{imageId}/{width}/{height}", method = RequestMethod.GET)
	public String getImageData(@PathVariable("imageId") String imageId, @PathVariable("width") Integer width,
			@PathVariable("height") Integer height) {
		String imageName = width + "x" + height + ".png";
		Thumbnail thumbnail = mongoTemplate.findOne(
				new Query(Criteria.where("image_id").is(imageId).and("name").is(imageName)), Thumbnail.class);
		return thumbnail != null ? thumbnail.getContent() : null;
	}

	/**
	 * 单文件下载
	 */
	@RequestMapping(value = "/files/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("id") String fileId, HttpServletRequest request) {
		GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
		InputStreamResource resource = new InputStreamResource(file.getInputStream());
		HttpHeaders headers = headers(file.getLength(), file.getFilename(), request);
		return ResponseEntity.ok().headers(headers).body(resource);
	}

	/**
	 * 多文件下载
	 */
	@RequestMapping(value = "/files/ids", method = RequestMethod.GET)
	public void downloadFiles(String[] fileIds, HttpServletRequest request, HttpServletResponse response) {
		List<GridFSDBFile> dbFiles = gridFsTemplate.find(new Query(Criteria.where("_id").in((Object[]) fileIds)));
		File zipFile = FileUtil.zip(dbFiles);
		setHeaders(request, response, "attachment", zipFile.length(), zipFile.getName(), "application/x-zip-compressed");
		try (OutputStream out = response.getOutputStream(); InputStream in = new FileInputStream(zipFile);) {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			logger.error("Error writing file to output stream. Filename is {}", zipFile.getName(), e);
		} finally {
			if(Objects.nonNull(zipFile) && zipFile.exists()){
				zipFile.delete();
			}
		}
	}
	
	private void setHeaders(HttpServletRequest request, HttpServletResponse response, String type, Long length, String filename, String contentType){
		response.setHeader("Content-Length", String.valueOf(length));
		response.setHeader("Content-Type", StringUtils.isEmpty(contentType) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : contentType);
		response.setHeader("Content-Disposition", type + ";filename=" + encodeFilename(request, filename));
	}
	
	private HttpHeaders headers(Long length, String filename, HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentLength(length);
		headers.setContentDispositionFormData("attachment", encodeFilename(request, filename));
		return headers;
	}
	
	private String encodeFilename(HttpServletRequest request, String filename) {
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		try {
			if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {
				return URLEncoder.encode(filename, "UTF-8");
			}
			return new String(filename.getBytes(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return filename;
		}
	}

	private boolean convertible(String filename) {
		for (String fileType : config.getFileTypes()) {
			if (filename.endsWith(fileType) || filename.toLowerCase().endsWith(fileType)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean imgFile(String filename) {
		for (String imgType : config.getImgTypes()) {
			if (filename.endsWith(imgType) || filename.toLowerCase().endsWith(imgType)) {
				return true;
			}
		}
		return false;
	}
}
