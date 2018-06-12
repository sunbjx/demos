/*
 * 2013-4-7 下午9:59:00
 */
package com.sunbjx.demos.framework.cache;

/**
 * 缓存时间单位.最小单位为分钟.
 *
 * @author sunbjx
 */
public enum CacheTimeUnit {

    SECOND(1),
    MINUTE(60),
    HOUR(3600),
    DAY(86400);

    private int value;

    private CacheTimeUnit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
