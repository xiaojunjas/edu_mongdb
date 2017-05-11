package com.gclasscn.mongo.config;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BeansConfiguration {

	private static final String BUCKET = "files";
	
	@Autowired
	private SystemConfig config;
	
	@Autowired
	private MongoDbFactory dbFactory;
	@Autowired
	private MongoConverter converter;
	
	@Bean
	public GridFsTemplate gridFsTemplate(){
		return new GridFsTemplate(dbFactory, converter, BUCKET);
	}
	
//	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); //核心线程数
		executor.setMaxPoolSize(30); //最大线程数
		executor.setQueueCapacity(1000); //队列最大长度
		executor.setKeepAliveSeconds(300); //线程池维护线程所允许的空闲时间
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //线程池对拒绝任务(无线程可用)的处理策略
		return executor;
	}
	
	/**
	 * 配置libreoffice
	 */
	@Bean
	public OfficeManager officeManager(){
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		// libreOffice的安装目录
		configuration.setOfficeHome(new File(config.getOfficePath()));
		// 端口号
		configuration.setPortNumber(config.getOfficePort());
		// 设置任务执行超时为10分钟
		configuration.setTaskExecutionTimeout(1000 * 60 * 10);
		OfficeManager officeManager = configuration.buildOfficeManager();
		return officeManager;
	}
}
