package com.sunbjx.demos.framework.cache.serialize;

import java.io.Serializable;

/**
 * key 序列化的策略
 *
 * @author sunbjx
 * @since 2016/6/13 14:33
 */
public interface KeySerializeStrategy {

    /**
     * 对 key 的序列化为String
     *
     * @return
     */
    String keySerialize(Serializable serializable) throws Exception;


    /**
     * 对 key 的序列化为String
     *
     * @return
     */
    String keyDeSerialize(byte[] bytes) throws Exception;

}
