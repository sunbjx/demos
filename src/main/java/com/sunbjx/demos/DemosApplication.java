package com.sunbjx.demos;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * 忽略url访问鉴权
 */
@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.activiti.spring.boot.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})
@EnableSwagger2
@EnableTransactionManagement
public class DemosApplication extends WebMvcConfigurerAdapter {

	@Bean
	public Object testBean(PlatformTransactionManager platformTransactionManager) {
		System.out.println(">>>>>>>>>>" + platformTransactionManager.getClass().getName());
		return new Object();
	}

	/**
	 * @ResponBody 中生效
	 * Spring 自定义 Json
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		// 定义 convert 转换消息对象
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		// 添加 fastjosn 的配置信息
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		// 在 convert 中添加配置信息
		fastConverter.setFastJsonConfig(fastJsonConfig);
		// 将 convert 添加到 converters 中
		converters.add(fastConverter);
	}

	/**
	 * 全局配置
	 * @param args
	 */
//	@Bean
//	public HttpMessageConverters fastJsonHttpMessageConverters() {
//		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//
//		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//
//		fastConverter.setFastJsonConfig(fastJsonConfig);
//		// 将 convert 添加到 converters 中
//		HttpMessageConverter<?>  converter = fastConverter;
//		return new HttpMessageConverters(converter);
//
//	}

	public static void main(String[] args) {
		SpringApplication.run(DemosApplication.class, args);
	}
}
