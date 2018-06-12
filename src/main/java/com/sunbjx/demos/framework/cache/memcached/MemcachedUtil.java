package com.sunbjx.demos.framework.cache.memcached;


import com.sunbjx.demos.framework.cache.CacheResult;

/**
 * 对memcached 的util
 *
 * @author sunbjx
 * @since 2017/6/15 14:11
 */
public class MemcachedUtil {


    /**
     * check redis response result is success
     */
    public static CacheResult memcacheResult2CacheResult(boolean result) {
        return result ? CacheResult.SUCCESS : CacheResult.FAIL;
    }

    /**
     * check redis response result is success
     */
    public static CacheResult memcacheResult2CacheResult(long result) {
        return result == 0 ? CacheResult.FAIL : CacheResult.SUCCESS;
    }
}
