package com.sunbjx.demos.framework.core.utils.id;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 获取idworker的配置.必须保证系统{user.home}/.idworker/idworker-{#workerId} 的方式配置相应的applicationName 和applicationId。
 * 可通过2中方式配置。
 * 第一:系统启动参数:idworker.applicationName 中指定 name
 * 第二:通过 {@link SpringIdWorkerConfig#setIdWorkerApplicationName(String)}  } 配置。
 * 第三：通过 spring properties 的方式 scan 注入。
 * @author sunbjx
 * @since 2018/6/12 10:47
 */
public class IdWorkerConfig {
    private static Long applicationId;
    private static Long workerId;

    private final static String configDir = "/.idworker";

    private final static String configPrefixFileName = "idworker-";

    private final static String IDWORK_PROPERTY_APP_NAME  = "idworker.applicationName";


    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Logger LOGGER      = LoggerFactory.getLogger(IdWorkerConfig.class);

    private static String applicationName;
    private static ConcurrentHashMap<String, Long> applications = new ConcurrentHashMap<>();

    static  {
        //如果是系统属性env的值为test，则加载测试环境的配置
        String userHomeDir = System.getProperty("user.home");
        String applicationName = System.getProperty("idworker.applicationName");
        if (applicationName != null) {
            IdWorkerConfig.applicationName = applicationName;
        }
        final File configDir = new File(userHomeDir + IdWorkerConfig.configDir);
        if (configDir.exists() && configDir.isDirectory()) {
            File[] files = configDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File configFile) {
                    String filename = configFile.getName();
                    if (filename.startsWith(configPrefixFileName)) {
                        String workId = filename.replace(configPrefixFileName, "");
                        if (NumberUtils.isCreatable(workId)) {
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            });
            if (files.length < 1) {
                LOGGER.warn(
                        "id worker doesn't working.if you want using it.please make sure {user.home}/.idworker/idworker-{number} file is exist ");
                ;
            } else if (files.length > 1) {
                LOGGER.warn("{user.home}/.idworker/idworker-{number}  have multi files.please make sure only one file");
            } else {
                load(files[0]);
                reloadIdWorkerProperties();
            }
        } else {
            if (!configDir.exists()) {
                configDir.mkdir();
            }
            LOGGER.warn("id worker doesn't working.if you want using it.please make sure {user.home}/.idworker Folder is exist");
        }
    }

    public static Long getApplicationId() {
        return applicationId;
    }

    public static void setApplicationId(Long applicationId) {
        IdWorkerConfig.applicationId = applicationId;
    }

    public static Long getWorkerId() {
        return workerId;
    }

    public static void setWorkerId(Long workerId) {
        IdWorkerConfig.workerId = workerId;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        IdWorkerConfig.applicationName = applicationName;
        IdWorkerConfig.applicationId = applications.get(applicationName);
    }

    private IdWorkerConfig() {
    }

    public static void load(File file) {
        if (initialized.compareAndSet(false, true)) {
            workerId = getWorkIdByFileName(file.getName());
            TypeSafeProperties props = new TypeSafeProperties();
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                props.load(is);

                Enumeration<Object> keys = props.keys();
                while (keys.hasMoreElements()) {
                    Object o = keys.nextElement();
                    String applicationName = (String) o;
                    String applicationId = (String) props.get(applicationName);
                    if (NumberUtils.isCreatable(applicationId)) {
                        applications.put(applicationName, NumberUtils.createLong(applicationId));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                props.clear();
                IOUtils.closeQuietly(is);

            }
        }
    }

    private static void reloadIdWorkerProperties() {
        InputStream is = IdWorkerConfig.class.getClassLoader().getResourceAsStream("idWorker.properties");
        if (is != null) {
            Properties properties = new Properties();
            try {
                properties.load(is);
                Object o = properties.get("idworker.applicationName");
            } catch (IOException e) {
                LOGGER.info("idWorker.properties is doesn't exist or load error.");
                IOUtils.closeQuietly(is);
            }
        }
    }

    /**
     * 根据fileName 获取 workid
     * @param filename
     * @return
     */
    public static Long getWorkIdByFileName(String filename) {
        if (filename.startsWith(configPrefixFileName)) {
            String workId = filename.replace(configPrefixFileName, "");
            if (NumberUtils.isCreatable(workId)) {
                return Long.decode(workId);
            }
            return null;
        }
        return null;
    }


    static class TypeSafeProperties extends Properties {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = -6367170602933739091L;

        public int getIntProperty(String key, int defaultValue) {
            String val = super.getProperty(key);
            if (val != null && val.trim().length() > 0) {
                return Integer.parseInt(val);
            }
            return defaultValue;
        }

        public boolean getBooleanProperty(String key, boolean defaultValue) {
            String val = super.getProperty(key);
            if (val != null && val.trim().length() > 0) {
                return Boolean.parseBoolean(val);
            }
            return defaultValue;
        }

        public String[] getValues(String key) {
            String inlineValue = super.getProperty(key);
            if (inlineValue != null) {
                return inlineValue.split(",");
            }
            return null;
        }
    }
}
