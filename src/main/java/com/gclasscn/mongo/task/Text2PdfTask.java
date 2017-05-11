package com.gclasscn.mongo.task;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.gclasscn.mongo.config.SystemConfig;
import com.gclasscn.mongo.domain.FileInfo;
import com.gclasscn.mongo.domain.Thumbnail;
import com.gclasscn.mongo.repository.FileInfoRepository;
import com.gclasscn.mongo.repository.ThumbnailRepository;
import com.gclasscn.mongo.utils.PDFUtil;
import com.mongodb.gridfs.GridFSFile;

import net.coobird.thumbnailator.Thumbnails;

@Component
public class Text2PdfTask {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SystemConfig config;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private FileInfoRepository fileRepository;
	@Autowired
	private ThumbnailRepository thumbnailRepository;
	@Autowired
	private OfficeManager officeManager;
	
	@Async
	public void execute(InputStream in, GridFSFile file) {
		File rawFile = null; //原始文件
		File pdfFile = null; //生成的pdf文件
		File imageFile = null; //截取的pdf第一页
		try (InputStream content = in) {
			//向原始文件中写入数据
			rawFile = new File(file.getFilename());
			writeTo(content, rawFile);
			
			//生成pdf文件并入库
			pdfFile = file.getFilename().endsWith(".pdf") ? rawFile : startConvert(rawFile);
			String pdfId = file.getFilename().endsWith(".pdf") ? file.getId().toString() : savePdf(pdfFile);
			
			//截取pdf文件首页并入库
			String imageId = saveImg(pdfFile, imageFile);
	    	
			//保存文件信息
			FileInfo fileInfo = new FileInfo(rawFile.getName(), rawFile.length(), file.getUploadDate(), 
					file.getContentType(), file.getId().toString(), pdfId, imageId);
			fileRepository.save(fileInfo);
		} catch (IOException e) {
			logger.error("failed to convert text file to pdf file\n", e);
		} finally {
			clean(rawFile, pdfFile, imageFile);
		}
	}
	
	/**
	 * 将原始文件的数据流写入临时文件中
	 */
	private void writeTo(InputStream in, File rawFile) throws IOException{
		try(BufferedInputStream bis = new BufferedInputStream(in);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(rawFile))){
			byte[] buffer = new byte[1024 * 8];
			while(bis.read(buffer) != -1){
				bos.write(buffer);
			}
			bos.flush();
		}
	}
	
	/**
	 * pdf文件入库
	 */
	private String savePdf(File pdfFile) throws IOException{
		return gridFsTemplate.store(new FileInputStream(pdfFile), pdfFile.getName()).getId().toString();
	}
	
	/**
	 * 截取pdf文件首页并入库
	 */
	private String saveImg(File pdfFile, File imageFile) throws IOException{
		BufferedImage pdfIamge = PDFUtil.createThumbnail(pdfFile, 640, 800, 0);
		imageFile = new File(pdfFile.getAbsolutePath().replaceAll(".pdf", ".png"));
		ImageIO.write(pdfIamge, "png", imageFile);
		String rawImgId = gridFsTemplate.store(new FileInputStream(imageFile), imageFile.getName()).getId().toString();
		thumbnailRepository.insert(generateThumbnails(pdfIamge, rawImgId));
		return rawImgId;
	}
	
	/**
	 * 启动libreoffice服务,转换pdf,关闭服务
	 */
	private File startConvert(File inputFile){
		officeManager.start();
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		String filePath = inputFile.getAbsolutePath();
		File outputFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + ".pdf");
		converter.convert(inputFile, outputFile);
		officeManager.stop();
		return outputFile;
	}
	
	/**
	 * 裁剪图片并转换成base64编码的字符串
	 */
	public List<Thumbnail> generateThumbnails(BufferedImage rawImg, String rawImgId){
		List<Thumbnail> thumbnails = new ArrayList<>();
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
			String[] imgSizes = config.getImgSizes();
			for(String imgSize : imgSizes){
				int width = Integer.valueOf(imgSize.split("x")[0]);
				int height = Integer.valueOf(imgSize.split("x")[1]);
				Thumbnails.of(rawImg).forceSize(width, height).outputFormat("png").toOutputStream(out);
				thumbnails.add(new Thumbnail(imgSize + ".png", rawImgId, "data:image/png;base64," + Base64Utils.encodeToString(out.toByteArray())));
				out.reset();
			}
		} catch (Exception e) {
			logger.error("error: " + e);
		}
		return thumbnails;
	}
	
	/**
	 * 删除临时文件
	 */
	private void clean(File... files){
		for(File file : files){
			if(file != null && file.exists()) file.delete();
		}
	}

}
