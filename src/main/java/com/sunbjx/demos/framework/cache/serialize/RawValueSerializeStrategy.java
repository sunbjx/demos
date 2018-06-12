package com.sunbjx.demos.framework.cache.serialize;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 不做任何加工处理，返回raw 的serialize object
 *
 * @author sunbjx
 * @since 2016/6/13 14:38
 */
public class RawValueSerializeStrategy implements ValueSerializeStrategy {

    /**
     * 将value 序列化为 byte[] 数组
     */
    public ValueSerializeResultWrapper valueSerialize(Serializable serializable) throws Exception {
        return new ValueSerializeResultWrapper(serializable, serializable);
    }

    /**
     * 将value 序列化为 byte[] 数组
     */
    public Object valueDeSerialize(Object serValue, Type type) throws Exception {
        return serValue;
    }

}
