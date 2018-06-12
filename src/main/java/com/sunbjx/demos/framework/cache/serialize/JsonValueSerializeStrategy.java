package com.sunbjx.demos.framework.cache.serialize;


import com.sunbjx.demos.framework.cache.util.InternalCacheSerializeUtils;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * json 的序列化策略
 *
 * @author sunbjx
 * @since 2016/6/13 17:20
 */
public class JsonValueSerializeStrategy implements ValueSerializeStrategy<String> {

    @Override
    public ValueSerializeResultWrapper<String> valueSerialize(Serializable serializable) {
        String serializeValue = InternalCacheSerializeUtils.serializeJson(serializable);
        return new ValueSerializeResultWrapper<String>(serializeValue, serializable);
    }

    @Override
    public <T> T valueDeSerialize(String serValue, Type type) {
        return InternalCacheSerializeUtils.deSerializeJson(serValue, type);
    }
}
