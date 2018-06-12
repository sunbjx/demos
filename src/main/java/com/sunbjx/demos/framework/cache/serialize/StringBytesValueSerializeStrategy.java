package com.sunbjx.demos.framework.cache.serialize;


import com.sunbjx.demos.framework.cache.util.InternalCacheSerializeUtils;
import com.sunbjx.demos.framework.cache.util.InternalCacheUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * 如果序列化的是string 的话，则直接 存入string.byte .不做java 的序列化。
 *
 * @author sunbjx
 * @since 2017/7/12 9:35
 */
public class StringBytesValueSerializeStrategy extends BytesValueSerializeStrategy {


    @Override
    public ValueSerializeResultWrapper<byte[]> valueSerialize(Serializable serializable) throws IOException {
        byte[] serializeValue = null;
        if (serializable instanceof String) {
            serializeValue = InternalCacheUtils.stringEncodeBytes((String) serializable);
        } else {
            serializeValue = InternalCacheSerializeUtils.objectToBytes(serializable);
        }
        return new ValueSerializeResultWrapper<byte[]>(serializeValue, serializable);
    }
}
