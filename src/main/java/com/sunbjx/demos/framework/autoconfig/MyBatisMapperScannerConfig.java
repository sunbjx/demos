//package com.sunbjx.demos.config;
//
//import java.util.Properties;
//
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//
///**
// * @author sunbjx
// * @since  2016年12月20日 下午8:28:22
// * @version V1.0
// */
//@Configuration
//@AutoConfigureAfter(MyBatisAutoConfig.class)
//public class MyBatisMapperScannerConfig {
//
//	@Bean
//	public MapperScannerConfigurer mapperScannerConfigurer() {
//		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//		mapperScannerConfigurer.setBasePackage("com.sunbjx.demos.dao");
//		Properties properties = new Properties();
//		properties.setProperty("mappers", "com.sunbjx.demos.util.MyMapper");
//		properties.setProperty("notEmpty", "false");
//		properties.setProperty("IDENTITY", "MYSQL");
//		mapperScannerConfigurer.setProperties(properties);
//		return mapperScannerConfigurer;
//	}
//
//}
