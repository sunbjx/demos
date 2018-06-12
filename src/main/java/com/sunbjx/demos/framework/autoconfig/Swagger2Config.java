package com.sunbjx.demos.framework.autoconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger2 配置
 *
 * @author sunbjx
 * @since 2017年06月27日 下午14:29:26
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    private static final String SWAGGER_SCAN_BASE_PACKAGE = "com.sunbjx.demos.view.oa.controller";
    private static final String VERSION = "0.0.1";

    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("demon用户接口").select() // 选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE)).paths(PathSelectors.any()) // 对所有路径进行监控
                .build().apiInfo(userApiInfo());
    }

    private ApiInfo userApiInfo() {
        return new ApiInfoBuilder()
                .title("UserApi")
                .description("用户接口 RESTful APIs")
                .license("")
                .licenseUrl("")
                .termsOfServiceUrl("http://sunbjx.me")
                .version(VERSION)
                .contact(new Contact("sunbjx", "http://sunbjx.me", "1006046300@qq.com"))
                .build();
    }

    /**
     * SpringBoot默认已经将classpath:/META-INF/resources/和classpath:/META-INF/resources/webjars/映射
     * 所以该方法不需要重写，如果在SpringMVC中，需要重写定义 重写该方法需要 extends WebMvcConfigurerAdapter
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/Tleaf/**").addResourceLocations("classpath:/templates/");
    }

}
