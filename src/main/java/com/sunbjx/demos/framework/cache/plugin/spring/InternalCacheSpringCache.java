package com.sunbjx.demos.framework.cache.plugin.spring;


import com.sunbjx.demos.framework.cache.CacheResult;
import com.sunbjx.demos.framework.cache.redis.AbstractRedisCache;
import com.sunbjx.demos.framework.cache.redis.HashRedisCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.Callable;

import static com.sunbjx.demos.framework.cache.CacheConst.SEPARATOR;
import static com.sunbjx.demos.framework.cache.CacheConst.SPRING_CACHE_FLAG;


/**
 * spring 的icache 功能
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
public class InternalCacheSpringCache extends AbstractValueAdaptingCache {


    /**
     * 前缀
     */
    private String prefix;

    private String springCacheName;


    private HashRedisCache hashRedisCache;


    public InternalCacheSpringCache(AbstractRedisCache redisCache, String springCacheName) {
        super(true);
        this.hashRedisCache = new HashRedisCache();
        this.hashRedisCache.setDelegate(redisCache.getDelegate());
        this.springCacheName = springCacheName;
        this.hashRedisCache.setNamespace(getCacheNameSpace(redisCache, springCacheName));
    }

    public InternalCacheSpringCache(AbstractRedisCache redisCache, String springCacheName, int lassAccessExpireSeconds) {
        super(true);
        this.hashRedisCache = new HashRedisCache();
        this.hashRedisCache.setLassAccessExpireSeconds(lassAccessExpireSeconds);
        this.hashRedisCache.setDelegate(redisCache.getDelegate());
        this.springCacheName = springCacheName;
        this.hashRedisCache.setNamespace(getCacheNameSpace(redisCache, springCacheName));

    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public String getName() {
        return springCacheName;
    }

    @Override
    public HashRedisCache getNativeCache() {
        return hashRedisCache;
    }

//	@Override
//	public ValueWrapper get(Object key) {
//		Assert.isInstanceOf(Serializable.class, key);
//		return toWrapper(hashRedisCache.get((Serializable) key));
//
//	}
//
//	@Override
//	public <T> T get(Object key, Class<T> type) {
//		Assert.isInstanceOf(Serializable.class, key);
//		return hashRedisCache.get((Serializable) key);
//	}

    @Override
    protected Object lookup(Object key) {
        Assert.isInstanceOf(Serializable.class, key);
        return hashRedisCache.get((Serializable) key);
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        return hashRedisCache.get((Serializable) key);
    }


    @Override
    public void put(Object key, Object value) {
        Assert.isInstanceOf(Serializable.class, key);
        value = toStoreValue(value);
        if (value == null) {
            return;
        }
        hashRedisCache.put((Serializable) key, (Serializable) value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Assert.isInstanceOf(Serializable.class, key);
        value = toStoreValue(value);
        if (value == null) {
            return null;
        }
        CacheResult cacheResult = hashRedisCache.putIfAbsent((Serializable) key, (Serializable) value);
        if (cacheResult == CacheResult.SUCCESS) {
            return null;
        } else {
            Object cachedValue = hashRedisCache.get((Serializable) key);
            return toValueWrapper(cachedValue);
        }
    }

    @Override
    public void evict(Object key) {
        Assert.isInstanceOf(Serializable.class, key);
        this.hashRedisCache.delete((Serializable) key);
    }

    @Override
    public void clear() {
        this.hashRedisCache.evict();
    }


    /**
     * 获取 hashRedisCache 的namespace
     */
    private String getCacheNameSpace(AbstractRedisCache cache, String name) {
        return new StringBuffer().append(cache.getNamespace()).append(SEPARATOR).append(SPRING_CACHE_FLAG).append(SEPARATOR).append(name)
                .append(SEPARATOR).toString();
    }
}
