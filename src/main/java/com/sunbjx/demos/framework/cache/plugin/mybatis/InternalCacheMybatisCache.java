package com.sunbjx.demos.framework.cache.plugin.mybatis;


import com.sunbjx.demos.framework.cache.CacheException;
import com.sunbjx.demos.framework.cache.redis.AbstractRedisCache;
import com.sunbjx.demos.framework.cache.redis.CallBack;
import com.sunbjx.demos.framework.cache.redis.HashRedisCache;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

import static com.sunbjx.demos.framework.cache.CacheConst.MYBATIS_CACHE_FLAG;
import static com.sunbjx.demos.framework.cache.CacheConst.SEPARATOR;


/**
 * 内部使用的mybatis 的二级缓存
 *
 * @author sunbjx
 * @since 2017/7/14 9:46
 */
public class InternalCacheMybatisCache implements org.apache.ibatis.cache.Cache {


    private String id;

    /**
     * 注入redisCache
     */
    private HashRedisCache delegateRedisCache;


    public InternalCacheMybatisCache(String id) {
        this.id = id;
    }


    /**
     * cache 完成之后，这里做一些初始化 delegateRedisCache 的动作.这里是必须要做的
     */
    public void afterConfigureParse(AbstractRedisCache redisCache) {
        this.delegateRedisCache = new HashRedisCache();
        this.delegateRedisCache.setDelegate(redisCache.getDelegate());
        this.delegateRedisCache.setNamespace(getCacheNameSpace(redisCache));
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if (value == null) {
            return;
        }
        if (!(key instanceof Serializable)) {
            throw new CacheException("key must be instanceof Serializable");
        }
        if (!(value instanceof Serializable)) {
            throw new CacheException("value must be instanceof Serializable");
        }
        this.delegateRedisCache.put((Serializable) key, (Serializable) value);
    }

    @Override
    public Object getObject(Object key) {
        if (!(key instanceof Serializable)) {
            throw new CacheException("key must be instanceof Serializable");
        }
        return this.delegateRedisCache.get((Serializable) key);
    }

    @Override
    public Object removeObject(Object key) {
        if (!(key instanceof Serializable)) {
            throw new CacheException("key must be instanceof Serializable");
        }
        this.delegateRedisCache.delete((Serializable) key);
        return null;
    }

    @Override
    public void clear() {
        delegateRedisCache.evict();
    }

    @Override
    public int getSize() {
        Long size = delegateRedisCache.execute(new CallBack<Long>() {
            @Override
            public Long executeWithJedis(Jedis jedis) {
                return jedis.hlen(delegateRedisCache.getNamespace());
            }
        });
        return size == null ? 0 : size.intValue();

    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    /**
     * 获取 hashRedisCache 的namespace
     */
    private String getCacheNameSpace(AbstractRedisCache cache) {
        return new StringBuffer().append(cache.getNamespace()).append(SEPARATOR).append(MYBATIS_CACHE_FLAG).append(SEPARATOR).append(id)
                .append(SEPARATOR).toString();
    }
}
