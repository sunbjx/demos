package com.sunbjx.demos;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 02:38 2017/11/24
 * @Modified By:
 */
public class TestActiviti {

    @Test
    public void createTable() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        processEngineConfiguration.setJdbcDriver("");
        processEngineConfiguration.setJdbcUrl("");
        processEngineConfiguration.setJdbcUsername("");
        processEngineConfiguration.setJdbcPassword("");

        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("ProcessEngine: " + processEngine);
    }

    @Test
    public void createTable2() {

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        System.out.println("ProcessEngine: " + processEngine);
    }
}
