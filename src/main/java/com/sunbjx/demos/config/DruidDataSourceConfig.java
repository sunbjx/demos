package com.sunbjx.demos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 16:56 2017/11/21
 * @Modified By:
 */
@Configuration
@ConditionalOnClass(DruidDataSourceConfig.class)
public class DruidDataSourceConfig implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    @Value("${datasource.type}")
    private Class<? extends DataSource> dataSourcetype;


    @Bean(name = "dataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "druid")
    public DataSource dataSource() {
        System.out.println("-------------------- writeDataSource init ---------------------");

        return DataSourceBuilder.create().type(dataSourcetype).build();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "druid.");
    }

    /**
     * 事务管理器
     * @return
     * @throws Exception
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }
}
