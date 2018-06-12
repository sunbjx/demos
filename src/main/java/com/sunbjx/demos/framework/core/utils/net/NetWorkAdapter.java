package com.sunbjx.demos.framework.core.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

/**
 * @author sunbjx
 * @since 2018/6/12 10:38
 */
public class NetWorkAdapter implements BeanFactoryPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(NetWorkAdapter.class);

    private String ipRegex;

    public static void init(String availableIpRegex) {
        if (availableIpRegex == null || availableIpRegex.trim().equals("")) {
            return;
        }
        Pattern availableIpPattern = Pattern.compile(availableIpRegex);
        Class<?> clz = null;
        try {
            clz = Class.forName("com.github.ltsopensource.core.commons.utils.NetUtils");
            Field ipPatternField = clz.getDeclaredField("IP_PATTERN");
            NetWorkAdapter.setFinalStaticPattern(clz, ipPatternField, availableIpPattern);
        } catch (Exception e) {
            logger.error("set lts  NetUtils ip pattern  error. the message is :" + e.getMessage());
        }
        try {
            clz = Class.forName("com.alibaba.dubbo.common.utils.NetUtils");
            Field ipPatternField = clz.getDeclaredField("DEFAULT_IP_PATTERN");
            NetWorkAdapter.setFinalStaticPattern(clz, ipPatternField, availableIpPattern);
        } catch (Exception e) {
            logger.error("set dubbo  NetUtils ip pattern  error. the message is :" + e.getMessage());
        }

        try {
            clz = Class.forName("com.eqying.basic.utils.network.NetUtils");
            Field ipPatternField = clz.getDeclaredField("DEFAULT_IP_PATTERN");
            NetWorkAdapter.setFinalStaticPattern(clz, ipPatternField, availableIpPattern);
        } catch (Exception e) {
            logger.error("set eqying netutils  ip pattern  error. the message is :" + e.getMessage());
        }
    }

    /*
    这是静态的pattern
     */
    private static void setFinalStaticPattern(Class clz, Field field, Pattern pattern) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(clz, pattern);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if (ipRegex != null) {
            NetWorkAdapter.init(ipRegex);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public String getIpRegex() {
        return ipRegex;
    }

    public void setIpRegex(String ipRegex) {
        this.ipRegex = ipRegex;
    }
}
