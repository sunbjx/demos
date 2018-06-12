//package com.sunbjx.demos.config;
//
//import java.util.Properties;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.session.ExecutorType;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import com.github.pagehelper.PageHelper;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// *
// * @author sunbjx Date: 2017年6月26日下午8:16:32
// */
//@Configuration
//@ComponentScan("com.sunbjx.demos.dao")
//public class MyBatisAutoConfig {
//
//	@Configuration
//	@EnableConfigurationProperties(MybatisProperties.class)
//	protected static class MyBatisConfig {
//		@Autowired
//		private DataSource dataSource;
//		@Autowired(required = false)
//		private Interceptor[] interceptors;
//		@Autowired
//		private ResourceLoader resourceLoader = new DefaultResourceLoader();
//		@Autowired
//		private MybatisProperties properties;
//
//		@PostConstruct
//		public void checkConfigFileExists() {
//			if (this.properties.isCheckConfigLocation()) {
//				Resource resource = this.resourceLoader.getResource(this.properties.getConfig());
//				Assert.state(resource.exists(), "Cannot find config location: " + resource
//						+ " (please add config file or check your Mybatis " + "configuration)");
//			}
//		}
//
//		/**
//		 * 创建 sqlSessionFactoryBean 实例
//		 *
//		 * @return
//		 * @throws Exception
//		 */
//		@Bean(name = "sqlSessionFactory")
//		@ConditionalOnMissingBean
//		public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
//			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//			factory.setDataSource(dataSource);
//
//			if (StringUtils.hasText(this.properties.getConfig())) {
//				factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfig()));
//			} else {
//				if (this.interceptors != null && this.interceptors.length > 0) {
//					factory.setPlugins(this.interceptors);
//				}
//
//				factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
//				// factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
//				factory.setMapperLocations(this.properties.getMapperLocations());
//			}
//			return factory.getObject();
//		}
//
//		@Bean
//		@ConditionalOnMissingBean
//		public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//			return new SqlSessionTemplate(sqlSessionFactory, this.properties.getExecutorType());
//		}
//
//		@Bean
//		public PageHelper pageHelper() {
//			PageHelper pageHelper = new PageHelper();
//			Properties properties = new Properties();
//            // 如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页（false返回空）
//			properties.setProperty("reasonable", "true");
//			// 支持通过Mapper接口参数来传递分页参数
//			properties.setProperty("supportMethodsArguments", "true");
//			// always 总是返回PageInfo类型, check 检查返回类型是否为PageInfo,none返回Page
//			properties.setProperty("returnPageInfo", "check");
//			properties.setProperty("params", "count=countSql");
//			pageHelper.setProperties(properties);
//			return pageHelper;
//		}
//
//	}
//
//	@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
//	public static class MybatisProperties {
//
//		static final String MYBATIS_PREFIX = "mybatis";
//
//		/**
//		 * Config file path.
//		 */
//		private String config;
//
//		/**
//		 * Location of mybatis mapper files.
//		 */
//		private Resource[] mapperLocations;
//
//		/**
//		 * Package to scan domain objects.
//		 */
//		private String typeAliasesPackage;
//
//		/**
//		 * Package to scan handlers.
//		 */
//		private String typeHandlersPackage;
//
//		/**
//		 * Check the config file exists.
//		 */
//		private boolean checkConfigLocation = false;
//
//		/**
//		 * Execution mode.
//		 */
//		private ExecutorType executorType = ExecutorType.SIMPLE;
//
//		public String getConfig() {
//			return this.config;
//		}
//
//		public void setConfig(String config) {
//			this.config = config;
//		}
//
//		public Resource[] getMapperLocations() {
//			return this.mapperLocations;
//		}
//
//		public void setMapperLocations(Resource[] mapperLocations) {
//			this.mapperLocations = mapperLocations;
//		}
//
//		public String getTypeHandlersPackage() {
//			return this.typeHandlersPackage;
//		}
//
//		public void setTypeHandlersPackage(String typeHandlersPackage) {
//			this.typeHandlersPackage = typeHandlersPackage;
//		}
//
//		public String getTypeAliasesPackage() {
//			return this.typeAliasesPackage;
//		}
//
//		public void setTypeAliasesPackage(String typeAliasesPackage) {
//			this.typeAliasesPackage = typeAliasesPackage;
//		}
//
//		public boolean isCheckConfigLocation() {
//			return this.checkConfigLocation;
//		}
//
//		public void setCheckConfigLocation(boolean checkConfigLocation) {
//			this.checkConfigLocation = checkConfigLocation;
//		}
//
//		public ExecutorType getExecutorType() {
//			return this.executorType;
//		}
//
//		public void setExecutorType(ExecutorType executorType) {
//			this.executorType = executorType;
//		}
//	}
//
//}
