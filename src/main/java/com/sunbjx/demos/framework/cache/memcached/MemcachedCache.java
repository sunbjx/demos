/*
 * Memcached.java
 * 
 */
package com.sunbjx.demos.framework.cache.memcached;

import com.alibaba.fastjson.TypeReference;
import com.sunbjx.demos.framework.cache.AbstractCache;
import com.sunbjx.demos.framework.cache.CacheResult;
import com.sunbjx.demos.framework.cache.CacheTimeUnit;
import com.sunbjx.demos.framework.cache.serialize.RawValueSerializeStrategy;
import com.sunbjx.demos.framework.cache.serialize.ValueSerializeResultWrapper;
import com.sunbjx.demos.framework.cache.util.InternalCacheAssert;
import net.rubyeye.xmemcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * memcached 对icache 的实现
 *
 * @author sunbjx
 * @version 1.0
 * @since 2017-7-16 下午1:24:41
 */
public class MemcachedCache extends AbstractCache {

    /**
     * @see <a href="https://github.com/memcached/memcached/blob/master/memcached.c">源码</a>
     * #define REALTIME_MAXDELTA 60*60*24*30，static rel_time_t realtime(const time_t exptime)  函数
     */
    private final static int DEFAULT_DELTA_SECONDS = 30 * 24 * 60 * 60;

    private static final Logger logger = LoggerFactory.getLogger(MemcachedCache.class);

    private MemcachedClient memcachedClient;

    public MemcachedCache() {
        super();
        this.valueSerializeStrategy = new RawValueSerializeStrategy();
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public MemcachedCache(MemcachedClient memcachedClient) {
        this.valueSerializeStrategy = new RawValueSerializeStrategy();
        this.memcachedClient = memcachedClient;
    }

    @Override
    public MemcachedClient getDelegate() {
        return memcachedClient;
    }

    @Override
    public CacheResult put(Serializable key, Serializable value) {
        return this.put(key, value, DEFAULT_DELTA_SECONDS, CacheTimeUnit.SECOND);
    }

    @Override
    public CacheResult put(Serializable key, Serializable value, int expires, CacheTimeUnit cacheTimeUnit) {
        try {
            String serialKey = getFullKey(key);
            ValueSerializeResultWrapper serializeResultWrapper = serializeValue(value);
            int secondExpires = expires * cacheTimeUnit.getValue();
            boolean result = memcachedClient.set(serialKey, secondExpires, serializeResultWrapper.getSerializeValue());
            if (logger.isTraceEnabled()) {
                logger.trace("memcache put value,key:{},value:{},secondeExpires:{},result:{}", serialKey,
                        serializeResultWrapper.getRawValue(), secondExpires, result);
            }
            return MemcachedUtil.memcacheResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("memcache put had error，key is:{}", key, e);
            return CacheResult.ERROR;
        }
    }

    @Override
    public CacheResult putIfAbsent(Serializable key, Serializable value) {
        return this.putIfAbsent(key, value, DEFAULT_DELTA_SECONDS, CacheTimeUnit.SECOND);
    }

    @Override
    public CacheResult putIfAbsent(Serializable key, Serializable value, int expires, CacheTimeUnit cacheTimeUnit) {
        try {
            String serialKey = getFullKey(key);
            ValueSerializeResultWrapper serializeResultWrapper = serializeValue(value);
            int secondExpires = expires * cacheTimeUnit.getValue();
            boolean result = memcachedClient.add(serialKey, secondExpires, serializeResultWrapper.getSerializeValue());
            if (logger.isTraceEnabled()) {
                logger.trace("memcache putIfAbsent value,key:{},value:{},secondeExpires:{},result:{}", serialKey,
                        serializeResultWrapper.getRawValue(), secondExpires, result);
            }
            return MemcachedUtil.memcacheResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("memcache putIfAbsent had error，key is:{}", key, e);
            return CacheResult.ERROR;
        }
    }

    @Override
    public CacheResult delete(Serializable key) {
        try {
            String serialKey = getFullKey(key);
            boolean result = memcachedClient.delete(serialKey);
            return MemcachedUtil.memcacheResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("memcache delete object had error，key is:{}", key, e);
            return CacheResult.ERROR;
        }
    }

    @Override
    public <T> T get(Serializable key) {
        if (isFormatJson()) {
            throw new UnsupportedOperationException("该方法不支持 formatJson=true");
        }
        return get(key, null);
    }

    @Override
    public <T> T get(Serializable key, TypeReference<T> classType) {
        try {
            String serialKey = getFullKey(key);
            Serializable cachedValue = memcachedClient.get(serialKey);
            if (logger.isDebugEnabled()) {
                logger.debug("get String key :{} ,value :{},will converter to :{}", key, cachedValue, classType);
            }
            return deSerializeValue(cachedValue, classType == null ? null : classType.getType());
        } catch (Exception e) {
            logger.warn("memcache get object had error，key is:{}", key, e);
            return null;
        }
    }

    @Override
    public CacheResult exists(Serializable key) {
        try {
            String serialKey = getFullKey(key);
            Serializable cachedValue = memcachedClient.get(serialKey);
            if (cachedValue != null) {
                return CacheResult.SUCCESS;
            } else {
                return CacheResult.FAIL;
            }
        } catch (Exception e) {
            logger.warn("memcache delete object had error，key is:{}", key, e);
            return CacheResult.ERROR;
        }
    }

    @Override
    public long evict() {
        return this.evict(null);
    }

    @Override
    public long evict(String pattern) {
        throw new UnsupportedOperationException("memcache 不支持正则查询 keys 的方法，无法支持改方法");
    }

    @Override
    public long increase(Serializable key) throws Exception {
        return this.increase(key, 1);
    }

    @Override
    public long increase(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        return this.increase(key, 1, expires, cacheTimeUnit);
    }

    @Override
    public long increase(Serializable key, long value) throws Exception {
        return this.increase(key, value, DEFAULT_DELTA_SECONDS, CacheTimeUnit.SECOND);
    }

    @Override
    public long increase(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        InternalCacheAssert.isGreaterThan(value, 0);
        String serialKey = getFullKey(key);
        long secondExpires = expires * cacheTimeUnit.getValue();
        //incr 如果不存在，则设置一个add 一个值。为了保证定义increase 接口规则，不存在，赋值为0，并自增指定的值，
        // 所以init 的值为value
        long result = memcachedClient.incr(serialKey, value, value, memcachedClient.getOpTimeout(), expires * cacheTimeUnit.getValue());
        if (logger.isTraceEnabled()) {
            logger.trace("memcache increase key:{}, delta :{},second expires:{},result:{}", key, value, secondExpires, result);
        }
        return result;
    }

    @Override
    public long decrease(Serializable key) throws Exception {
        return this.decrease(key, 1);
    }

    @Override
    public long decrease(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        return this.decrease(key, 1, expires, cacheTimeUnit);
    }

    @Override
    public long decrease(Serializable key, long value) throws Exception {
        return this.decrease(key, value, DEFAULT_DELTA_SECONDS, CacheTimeUnit.SECOND);
    }

    @Override
    public long decrease(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        InternalCacheAssert.isGreaterThan(value, 0);
        String serialKey = getFullKey(key);
        long secondExpires = expires * cacheTimeUnit.getValue();
        // memcache 不支持 负值，所以这里初始化为0
        long result = memcachedClient.decr(serialKey, value, 0, memcachedClient.getOpTimeout(), expires * cacheTimeUnit.getValue());
        if (logger.isTraceEnabled()) {
            logger.trace("memcache increase key:{}, delta :{},second expires:{},result:{}", key, value, secondExpires, result);
        }
        return result;
    }

    @Override
    public MemcachedClient getNativeCacheClient() {
        return memcachedClient;
    }

    @Override
    public void releaseNativeCacheClient(Object nativeCacheClient) {
        //do nothing
    }

    @Override
    public CacheResult resetExpire(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) {
        try {
            String serialKey = getFullKey(key);
            int secondExpires = expires * cacheTimeUnit.getValue();
            boolean result = memcachedClient.touch(serialKey, secondExpires);
            if (logger.isTraceEnabled()) {
                logger.trace("reset expire key:{},second expires value:{}", key, secondExpires);
            }
            return MemcachedUtil.memcacheResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("memcache delete object had error，key is:{}", key, e);
            return CacheResult.ERROR;
        }
    }


    @Override
    protected String getEvictPattern(String pattern) {
        throw new UnsupportedOperationException("受限于memcache 命令，无法批量移除");
    }
}
