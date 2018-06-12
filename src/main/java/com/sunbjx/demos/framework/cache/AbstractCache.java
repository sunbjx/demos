package com.sunbjx.demos.framework.cache;

import com.sunbjx.demos.framework.cache.redis.RedisCache;
import com.sunbjx.demos.framework.cache.serialize.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 设置nameSpace 的cache 实现基类
 *
 * @author sunbjx
 * @since 2016/3/3 10:51
 */
public abstract class AbstractCache implements ICache {


    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);


    protected KeySerializeStrategy keySerializeStrategy = new DefaultKeySerializeStrategy();

    protected ValueSerializeStrategy valueSerializeStrategy = new BytesValueSerializeStrategy();


    public void setKeySerializeStrategy(KeySerializeStrategy keySerializeStrategy) {
        this.keySerializeStrategy = keySerializeStrategy;
    }

    public void setValueSerializeStrategy(ValueSerializeStrategy valueSerializeStrategy) {
        this.valueSerializeStrategy = valueSerializeStrategy;
    }

    /**
     * 对于cache 的namespace
     */
    private String namespace;

    /**
     * 默认不自动json 格式化
     */
    private boolean formatJson = false;

    @Override
    public boolean isFormatJson() {
        return formatJson;
    }

    public void setFormatJson(boolean formatJson) {
        this.formatJson = formatJson;
        if (formatJson) {
            valueSerializeStrategy = new JsonValueSerializeStrategy();
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        if (namespace == null || namespace.trim().equals("")) {
            this.namespace = null;
        }
        this.namespace = namespace;
    }

    /**
     * 获取完整的key
     */
    protected String getFullKey(Serializable key) throws Exception {
        String serialKey = keySerializeStrategy.keySerialize(key);
        return namespace == null ? serialKey : namespace + serialKey;
    }


    protected ValueSerializeResultWrapper serializeValue(Serializable serialValue) throws Exception {
        return this.valueSerializeStrategy.valueSerialize(serialValue);
    }

    protected <T> T deSerializeValue(Serializable serialValue, Type type) throws Exception {
        return (T) this.valueSerializeStrategy.valueDeSerialize(serialValue, type);
    }


    protected abstract String getEvictPattern(String pattern);


    public int getSecondExpires(int expires, CacheTimeUnit cacheTimeUnit) {
        return expires * cacheTimeUnit.getValue();
    }

}
