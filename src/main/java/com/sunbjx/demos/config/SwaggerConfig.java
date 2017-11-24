package com.sunbjx.demos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @title SwaggerConfig.java
 * @package com.sunbjx.demons.config
 * @description swagger配置
 * @author sunbjx
 * @update 2017年06月27日 下午14:29:26
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket userApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("demon用户接口").select() // 选择那些路径和api会生成document
				.apis(RequestHandlerSelectors.basePackage("com.sunbjx.demos.controller")).paths(PathSelectors.any()) // 对所有路径进行监控
				.build().apiInfo(userApiInfo());
	}

	private ApiInfo userApiInfo() {
		ApiInfo apiInfo = new ApiInfo("demon接口Api", "用户接口", "0.1", "sunbjx", "sunbjx", "sunbjx", "http://sunbjx.me");
		return apiInfo;
	}

	/**
	 * SpringBoot默认已经将classpath:/META-INF/resources/和classpath:/META-INF/resources/webjars/映射
	 * 所以该方法不需要重写，如果在SpringMVC中，可能需要重写定义（我没有尝试） 重写该方法需要 extends
	 * WebMvcConfigurerAdapter
	 *
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

		registry.addResourceHandler("/Tleaf/**").addResourceLocations("classpath:/templates/");
	}

}
