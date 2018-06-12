package com.sunbjx.demos.framework.cache.redis;

import redis.clients.jedis.Jedis;

/**
 * 执行原声的 jedis 方法
 *
 * @author sunbjx
 * @since 2016/7/14 12:29
 */
public interface CallBack<T> {


    T executeWithJedis(Jedis jedis);
}
