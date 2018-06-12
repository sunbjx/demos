package com.sunbjx.demos.framework.cache.plugin.spring;


import com.sunbjx.demos.framework.cache.redis.AbstractRedisCache;
import com.sunbjx.demos.framework.cache.util.InternalCacheAssert;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * icache 的spring manager，每次应用启动都会清除cache.请查看afterPropertiesSet 方法
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
public class InternalCacheSpringCacheManager extends AbstractCacheManager {


    private ConcurrentHashMap<String, InternalCacheSpringCache> InternalCacheSpringCaches = new ConcurrentHashMap();


    private AbstractRedisCache redisCache;

    /**
     * 设置特殊的超时时间,默认使用Schedule Cache decorate 的Cache 来判断自动超时时间。默认为一个小时.
     * 单位：second
     */
    private Map<String, Integer> expires = new HashMap();

    /**
     * 第一次使用缓存的时候，清除cache 中的内容
     */
    private boolean evictCache = false;

    /**
     * evictCache  = true 的情况下，指定清除的CacheName.如果exclude和include 同时包含相同的name。则按照include 处理
     */
    private Set<String> includeEvictCacheNames;
    /**
     * evictCache  = true 的情况下，排除清除的CacheName.如果exclude和include 同时包含相同的name。则按照include 处理
     */
    private Set<String> excludeEvictCacheNames;

    public boolean isEvictCache() {
        return evictCache;
    }

    public void setEvictCache(boolean evictCache) {
        this.evictCache = evictCache;
    }

    public Set<String> getIncludeEvictCacheNames() {
        return includeEvictCacheNames;
    }

    public void setIncludeEvictCacheNames(Set<String> includeEvictCacheNames) {
        this.includeEvictCacheNames = includeEvictCacheNames;
    }

    public Set<String> getExcludeEvictCacheNames() {
        return excludeEvictCacheNames;
    }

    public void setExcludeEvictCacheNames(Set<String> excludeEvictCacheNames) {
        this.excludeEvictCacheNames = excludeEvictCacheNames;
    }

    @Override
    public void afterPropertiesSet() {
        InternalCacheAssert.isNotNull(redisCache, "cache  不能为空");
        super.afterPropertiesSet();

    }

    public AbstractRedisCache getRedisCache() {
        return redisCache;
    }

    public void setRedisCache(AbstractRedisCache redisCache) {
        this.redisCache = redisCache;
    }

    public void setExpires(Map<String, Integer> expires) {
        this.expires = expires;
    }

    public Map<String, Integer> getExpires() {
        return expires;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.InternalCacheSpringCaches.values();
    }


    @Override
    protected Cache getMissingCache(String name) {
        InternalCacheSpringCache internalCacheSpringCache = InternalCacheSpringCaches.get(name);
        if (internalCacheSpringCache == null) {
            synchronized (InternalCacheSpringCaches) {
                internalCacheSpringCache = InternalCacheSpringCaches.get(name);
                if (internalCacheSpringCache != null) {
                    return internalCacheSpringCache;
                }
                Integer expireValue = expires.get(name);
                if (expireValue != null) {
                    internalCacheSpringCache = new InternalCacheSpringCache(redisCache, name, expireValue);
                } else {
                    internalCacheSpringCache = new InternalCacheSpringCache(redisCache, name);
                }
                this.afterInitICache(internalCacheSpringCache);
                InternalCacheSpringCaches.put(name, internalCacheSpringCache);
            }
        }
        return internalCacheSpringCache;
    }

    /**
     * 完成初始化之后，
     */
    protected void afterInitICache(InternalCacheSpringCache springCache) {
        if (evictCache == true) {
            //执行evict spring cache 的逻辑
            boolean clear = true;
            if (excludeEvictCacheNames != null && excludeEvictCacheNames.contains(springCache.getName())) {
                clear = false;// 当含有排除的name，则将不清除
            }
            if (includeEvictCacheNames != null) {
                // 当includeCacheNames 不为空时，不包含在springcache.Name 的话，默认是不清空的；
                // 如果包含的话，清空。
                if (includeEvictCacheNames.contains(springCache.getName())) {
                    clear = true;
                } else {
                    clear = false;
                }
            }
            if (clear == true) {
                springCache.clear();
            }

        }
    }

    @Override
    protected Cache decorateCache(Cache cache) {
        Integer expireValue = expires.get(cache.getName());
        if (expireValue == null) {
            return super.decorateCache(new ScheduledCache(cache));
        } else {
            return super.decorateCache(new ScheduledCache(cache, expireValue * 1000));
        }
    }


}
