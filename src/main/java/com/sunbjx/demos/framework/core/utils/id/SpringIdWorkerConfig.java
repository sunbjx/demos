package com.sunbjx.demos.framework.core.utils.id;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * spring 对 idWorkerConfig 的支持
 * @author sunbjx
 * @since 2018/6/12 10:49
 */
@Lazy(value = false)
@Component
public class SpringIdWorkerConfig implements InitializingBean {

    @Value("${idworker.applicationName:#{null}}")
    private String idWorkerApplicationName;

    public String getIdWorkerApplicationName() {
        return idWorkerApplicationName;
    }

    public void setIdWorkerApplicationName(String idWorkerApplicationName) {
        this.idWorkerApplicationName = idWorkerApplicationName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (idWorkerApplicationName != null) {
            IdWorkerConfig.setApplicationName(idWorkerApplicationName);
        }
    }
}
