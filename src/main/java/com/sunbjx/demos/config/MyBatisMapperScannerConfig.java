package com.sunbjx.demos.config;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * 
 * @title MyBatisMapperScannerConfig.java
 * @package com.sunbjx.demons.config
 * @description MyBatis扫描接口，使用的com.sunbjx.conf.MapperScannerConfigurer，如果你不使用通用Mapper，可以改为org.xxx...
 * @author ZhangHuaRong
 * @update 2016年12月20日 下午8:28:22
 * @version V1.0
 */
@Configuration
@AutoConfigureAfter(MyBatisAutoConfig.class)
public class MyBatisMapperScannerConfig {

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		mapperScannerConfigurer.setBasePackage("com.sunbjx.demos.mapper");
		Properties properties = new Properties();
		properties.setProperty("mappers", "com.sunbjx.demos.util.MyMapper");
		properties.setProperty("notEmpty", "false");
		properties.setProperty("IDENTITY", "MYSQL");
		mapperScannerConfigurer.setProperties(properties);
		return mapperScannerConfigurer;
	}

}
