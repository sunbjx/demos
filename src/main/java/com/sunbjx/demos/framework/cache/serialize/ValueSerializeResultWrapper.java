package com.sunbjx.demos.framework.cache.serialize;

import java.io.Serializable;

/**
 * 最result 的结果的包装
 *
 * @author sunbjx
 * @since 2016/6/14 14:42
 */
public class ValueSerializeResultWrapper<S> {

    private S serializeValue;

    private Serializable rawValue;

    public ValueSerializeResultWrapper(S serializeValue, Serializable rawValue) {
        this.serializeValue = serializeValue;
        this.rawValue = rawValue;
    }

    public S getSerializeValue() {
        return serializeValue;
    }


    public Object getRawValue() {
        return rawValue;
    }


}
