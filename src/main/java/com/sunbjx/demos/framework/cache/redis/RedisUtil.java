package com.sunbjx.demos.framework.cache.redis;


import com.sunbjx.demos.framework.cache.CacheConst;
import com.sunbjx.demos.framework.cache.CacheResult;

/**
 * redis util
 *
 * @author sunbjx
 * @since 2016/6/14 16:35
 */
public class RedisUtil {

    /**
     * check redis response result is success
     */
    public static CacheResult redisResult2CacheResult(String result) {
        if (CacheConst.REDIS_OK_RESULT.equals(result)) {
            return CacheResult.SUCCESS;
        } else {
            return CacheResult.FAIL;
        }
    }

    /**
     * check redis response result is success
     */
    public static CacheResult redisResult2CacheResult(boolean result) {
        return result ? CacheResult.SUCCESS : CacheResult.FAIL;
    }

    /**
     * check redis response result is success
     */
    public static CacheResult redisResult2CacheResult(long result) {
        return result == 0 ? CacheResult.FAIL : CacheResult.SUCCESS;
    }
}

