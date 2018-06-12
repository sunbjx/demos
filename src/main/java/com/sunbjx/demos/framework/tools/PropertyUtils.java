package com.sunbjx.demos.framework.tools;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 属性资源文件
 *
 * @author sunbjx
 * @since 2018/6/12 17:09
 */
public class PropertyUtils {


    private static Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    public static Properties load(String pathfilename) throws IOException {
        InputStream in = null;

        try {
            in = PropertyUtils.class.getClassLoader().getResourceAsStream(pathfilename);
            if (in == null) {
                logger.error("属性配置文件[" + pathfilename + "]不存在.");
                throw new FileNotFoundException("属性配置文件[" + pathfilename + "]不存在.");
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 静默加载properties ，如果文件不存在，则不抛出异常
     *
     * @param pathfilename
     * @return
     * @throws IOException
     */
    public static Properties loadQuietly(String pathfilename) throws IOException {
        InputStream in = null;

        try {
            in = PropertyUtils.class.getClassLoader().getResourceAsStream(pathfilename);
            if (in == null) {
                logger.debug("属性配置文件[" + pathfilename + "]不存在.");
                return null;
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static Map<String, String> loadForMap(String pathfilename) {
        return null;
    }
}
