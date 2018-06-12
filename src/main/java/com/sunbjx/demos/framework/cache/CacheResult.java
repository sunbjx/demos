package com.sunbjx.demos.framework.cache;

/**
 * cache result
 *
 * @author sunbjx
 * @since 2016/6/14 16:43
 */
public enum CacheResult {

    SUCCESS,
    FAIL,
    ERROR;

    public static boolean isSuccess(CacheResult cacheResult) {
        return SUCCESS.equals(cacheResult);
    }

    /**
     * 是否成功。提供更简单的获取是否成功
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return CacheResult.isSuccess(this);
    }

}
