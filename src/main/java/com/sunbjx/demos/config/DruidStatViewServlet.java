package com.sunbjx.demos.config;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

/**
 * Druid的Servlet 在Application上面添加 @ServletComponentScan 创建好之后，我们访问
 * http://localhost/druid/index.html,会自动跳到http://localhost/druid/login.html登录页面
 * 账户和密码为下面设置的
 * 
 * @author sunbjx Date: 2017年6月27日下午3:59:47
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*", initParams = {
		// @WebInitParam(name="allow",value="192.168.16.110,127.0.0.1"),// IP白名单
		// (没有配置或者为空，则允许所有访问)
		// @WebInitParam(name="deny",value="192.168.16.111"),// IP黑名单
		// (存在共同时，deny优先于allow)
		@WebInitParam(name = "loginUsername", value = "root"), // 用户名
		@WebInitParam(name = "loginPassword", value = "123456"), // 密码
		@WebInitParam(name = "resetEnable", value = "false")// 禁用HTML页面上的“Reset
															// All”功能
})
public class DruidStatViewServlet extends StatViewServlet {

	/** serialVersionUID TODO */

	private static final long serialVersionUID = 1L;

}