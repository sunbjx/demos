package com.sunbjx.demos.framework.log.instrument.lts;

import com.github.ltsopensource.spring.TaskTrackerXmlFactoryBean;
import com.github.ltsopensource.spring.tasktracker.JobDispatcher;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @auther: Feng Yapeng
 * @since: 2016/10/20 10:46
 */
public class TaskTrackerFactoryBean extends TaskTrackerXmlFactoryBean implements ApplicationContextAware {

    private JobDispatcher jobDispatcher;

    private ApplicationContext applicationContext;


    public JobDispatcher getJobDispatcher() {
        return jobDispatcher;
    }

    public void setJobDispatcher(JobDispatcher jobDispatcher) {
        this.jobDispatcher = jobDispatcher;
    }

    @Override
    protected JobRunner createJobRunner() {
        return jobDispatcher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
