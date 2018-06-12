package com.sunbjx.demos.framework.cache.serialize;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * S:为序列化后的类型；
 * T：为反序列化的类型
 *
 * @author sunbjx
 * @since 2016/6/13 14:38
 */
public interface ValueSerializeStrategy<S> {

    /**
     * 将value 序列化为 byte[] 数组
     */
    ValueSerializeResultWrapper<S> valueSerialize(Serializable serializable) throws Exception;

    /**
     * 将value 序列化为 byte[] 数组
     */
    <T> T valueDeSerialize(S serValue, Type type) throws Exception;

}
