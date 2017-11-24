package com.sunbjx.demos.config;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;

/**
 * Druid拦截器，用于查看Druid监控
 * 
 * @author sunbjx Date: 2017年6月27日下午3:58:44
 */
@WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*", initParams = {
		@WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*") // 忽略资源
})
public class DruidStatFilter extends WebStatFilter {

}