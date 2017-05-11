package com.gclasscn.mongo.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import net.coobird.thumbnailator.Thumbnailator;

public class PDFUtil {
	
	/**
	 * PDF封面截取
	 * @param pdfFile: pdf文件
	 * @param width: 图片宽度 (默认 80)
	 * @param height: 图片高度 (默认 100)
	 * @param dpi: 每英寸点数(默认 72)
	 */
	public static BufferedImage createThumbnail(File pdfFile, int width, int height, int dpi) throws IOException{
		width = width == 0 ? 80 : width;
		height = height == 0 ? 100 : height;
		dpi = dpi == 0 ? 72 : dpi;
		PDFRenderer red = new PDFRenderer(PDDocument.load(pdfFile));
		BufferedImage image = red.renderImageWithDPI(0, dpi);//读取pdf第一页用来制作缩略图 
		return Thumbnailator.createThumbnail(image, width, height);
	}
}
