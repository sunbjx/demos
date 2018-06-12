package com.sunbjx.demos.framework.cache.serialize;


import com.sunbjx.demos.framework.cache.util.InternalCacheAssert;
import com.sunbjx.demos.framework.cache.util.InternalCacheSerializeUtils;
import com.sunbjx.demos.framework.cache.util.InternalCacheUtils;

import java.io.Serializable;

/**
 * 默认的key 的序列化的策略
 *
 * @author sunbjx
 * @since 2016/6/13 16:03
 */
public class DefaultKeySerializeStrategy implements KeySerializeStrategy {

    @Override
    public String keySerialize(Serializable serializable) throws Exception {
        InternalCacheAssert.isNotNull(serializable, "key 不能为空");
        if (serializable == null) {
            return null;
        }
        if (serializable instanceof String) {
            return (String) serializable;
        }

        if (serializable instanceof Integer) {
            int value = (int) serializable;
            return String.valueOf(value);
        }
        if (serializable instanceof Long) {
            long value = (long) serializable;
            return String.valueOf(value);
        }
        if (serializable instanceof Double) {
            double value = (double) serializable;
            return String.valueOf(value);
        }
        if (serializable instanceof Float) {
            float value = (float) serializable;
            return String.valueOf(value);
        }
        if (serializable instanceof Boolean) {
            return String.valueOf(serializable);
        }
        if (serializable instanceof byte[]) {
            return InternalCacheUtils.encodeBase64String((byte[]) serializable);

        }
        return serializeObject(serializable);
    }

    @Override
    public String keyDeSerialize(byte[] bytes) throws Exception {
        return InternalCacheUtils.stringDecodeBytes(bytes);
    }

    /**
     * 对于 object 序列化 为String
     */
    private String serializeObject(Serializable serializable) throws Exception {
        byte[] bts = InternalCacheSerializeUtils.objectToBytes(serializable);
        return InternalCacheUtils.encodeBase64String(bts);
    }
}
