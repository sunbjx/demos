package com.sunbjx.demos.framework.cache.redis;

import com.alibaba.fastjson.TypeReference;
import com.sunbjx.demos.framework.cache.CacheConst;
import com.sunbjx.demos.framework.cache.CacheResult;
import com.sunbjx.demos.framework.cache.CacheTimeUnit;
import com.sunbjx.demos.framework.cache.serialize.ValueSerializeResultWrapper;
import com.sunbjx.demos.framework.cache.util.InternalCacheAssert;
import com.sunbjx.demos.framework.cache.util.InternalCacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

/**
 * redis 的cache 实现
 *
 * @author sunbjx
 * @since 2016 -06-13 19:31:33
 */
@Component
public class RedisCache extends AbstractRedisCache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);


    @Override
    public CacheResult delete(Serializable key) {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            if (logger.isDebugEnabled()) {
                logger.debug("delete String key :{} ", serialKey);
            }
            _jedis = delegate.getResource();
            long result = _jedis.del(serialKey);
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis delete had error，key is:{}", key, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    /**
     * Get t.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the t
     */
    public <T> T get(Serializable key) {
        if (isFormatJson()) {
            throw new UnsupportedOperationException("该方法不支持 formatJson=true");
        }
        return get(key, null);
    }

    /**
     * Get t.
     *
     * @param <T>           the type parameter
     * @param key           the key
     * @param typeReference the type reference
     * @return the t
     */
    @Override
    public <T> T get(Serializable key, TypeReference<T> typeReference) {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            if (isFormatJson()) {
                String jsonStr = _jedis.get(serialKey);
                if (logger.isTraceEnabled()) {
                    logger.trace("get String key :{} ,value :{},will converter to :{}", serialKey, jsonStr, typeReference);
                }
                return deSerializeValue(jsonStr, typeReference.getType());
            } else {
                byte[] objDatas = _jedis.get(InternalCacheUtils.stringEncodeBytes(serialKey));
                T obj = deSerializeValue(objDatas, typeReference == null ? null : typeReference.getType());
                if (logger.isDebugEnabled()) {
                    logger.trace("get String key :{} ,value :{},will converter to :{}", serialKey, obj, typeReference);
                }
                return obj;
            }
        } catch (Exception e) {
            logger.warn("redis get object had error，key is:{}", key, e);
            return null;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    @Override
    public CacheResult exists(Serializable key) {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            boolean result = _jedis.exists(serialKey);
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis exist object had error，key is:{}", key, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    @Override
    public long evict() {
        return this.evict(null);
    }

    @Override
    public long evict(String pattern) {
        throw new UnsupportedOperationException("twemproxy not support keys command,so it's un supported");
//		Jedis _jedis = null;
//		String evictPattern = null;
//		try {
//			if (InternalCacheUtils.isBlank(this.getNamespace())) {
//				throw new UnsupportedOperationException("由于namespace 不存在，不能进行evict 操作");
//			}
//			_jedis = delegate.getResource();
//			evictPattern = getEvictPattern(pattern);
//
//			Set<String> keys = _jedis.keys(evictPattern);
//			String[] keyStrs = (String[]) keys.toArray();
//			return _jedis.del(keyStrs);
//		} catch (Exception e) {
//			logger.warn("redis evict object had error，pattern is:{}", evictPattern, e);
//			return 0;
//		} finally {
//			InternalCacheUtils.closeStreamQuietly(_jedis);
//		}
    }

    @Override
    public CacheResult put(Serializable key, Serializable value) {
        return put(key, value, 0, CacheTimeUnit.SECOND);
    }

    @Override
    public CacheResult put(Serializable key, Serializable value, int expires, CacheTimeUnit cacheTimeUnit) {
        Jedis _jedis = null;
        String serialKey = null;
        try {
            serialKey = getFullKey(key);
            ValueSerializeResultWrapper serializeValue = serializeValue(value);
            _jedis = delegate.getResource();
            if (expires <= 0) {
                return this.jedisSetx(_jedis, serialKey, serializeValue, null);
            } else {
                return this.jedisSetx(_jedis, serialKey, serializeValue, null, expires, cacheTimeUnit);
            }
        } catch (Exception e) {
            logger.warn("redis add had error，key is:{}", key, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    /**
     * Put if absent boolean.
     *
     * @param key   the key
     * @param value the value
     * @return the boolean
     */
    @Override
    public CacheResult putIfAbsent(Serializable key, Serializable value) {
        return this.putIfAbsent(key, value, 0, CacheTimeUnit.SECOND);
    }

    /**
     * Put if absent boolean.
     *
     * @param key           the key
     * @param value         the value
     * @param expires       the expire
     * @param cacheTimeUnit the cache time unit
     * @return the boolean
     */
    @Override
    public CacheResult putIfAbsent(Serializable key, Serializable value, int expires, CacheTimeUnit cacheTimeUnit) {

        Jedis _jedis = null;
        String serialKey = null;

        try {
            serialKey = getFullKey(key);
            ValueSerializeResultWrapper serializeValue = serializeValue(value);
            _jedis = delegate.getResource();
            if (expires <= 0) {
                return this.jedisSetx(_jedis, serialKey, serializeValue, CacheConst.REDIS_NX);
            } else {
                return this.jedisSetx(_jedis, serialKey, serializeValue, CacheConst.REDIS_NX, expires, cacheTimeUnit);
            }
        } catch (Exception e) {
            logger.warn("redis add had error，key is:{}", key, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    @Override
    public long increase(Serializable key) throws Exception {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            return _jedis.incr(serialKey);
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long increase(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.incr(serialKey);
            setExpire(_jedis, serialKey, expires, cacheTimeUnit);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long increase(Serializable key, long value) throws Exception {
        Jedis _jedis = null;
        InternalCacheAssert.isGreaterThan(value, 0);
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            return _jedis.incrBy(serialKey, value);
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long increase(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        InternalCacheAssert.isGreaterThan(value, 0);
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.incrBy(serialKey, value);
            setExpire(_jedis, serialKey, expires, cacheTimeUnit);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long decrease(Serializable key) throws Exception {
        Jedis _jedis = null;

        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            return _jedis.decr(serialKey);
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long decrease(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.decr(serialKey);
            this.setExpire(_jedis, serialKey, expires, cacheTimeUnit);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long decrease(Serializable key, long value) throws Exception {
        Jedis _jedis = null;
        InternalCacheAssert.isGreaterThan(value, 0);
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            return _jedis.decrBy(serialKey, value);
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long decrease(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        Jedis _jedis = null;
        InternalCacheAssert.isGreaterThan(value, 0);
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.decrBy(serialKey, value);
            this.setExpire(_jedis, serialKey, expires, cacheTimeUnit);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    @Override
    public CacheResult resetExpire(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) {
        Jedis _jedis = null;
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.expire(serialKey, expires * cacheTimeUnit.getValue());
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis reset expire had error，key is:{}", key, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    /**
     * 通用的高级的 向redis 设置值的命令
     */
    private CacheResult jedisSetx(Jedis jedis, String key, ValueSerializeResultWrapper serializeValueWrapper, String putType) {
        // 由于jedis set 命令设计不合理，无
        // set(final byte[] key, final byte[] value, final byte[] expx,final int time) 的函数，所以将nxxx 参数设置为空的byte
        byte[] putTypeBts = putType == null ? null : putType.getBytes(CacheConst.UTF_8);

        Object serializeValue = serializeValueWrapper.getSerializeValue();
        if (!(serializeValue instanceof byte[])) {
            serializeValue = InternalCacheUtils.stringEncodeBytes((String) serializeValue);
        }
        String result = null;
        if (putTypeBts == null) {
            result = jedis.set(InternalCacheUtils.stringEncodeBytes(key), (byte[]) serializeValue);
        } else {
            result = jedis.set(InternalCacheUtils.stringEncodeBytes(key), (byte[]) serializeValue, putTypeBts);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("redis setx command,key:{},rawValue:{},putType：{}, result:{}", key, serializeValueWrapper.getRawValue(), putType,
                    result);
        }
        return RedisUtil.redisResult2CacheResult(result);
    }


    /**
     * 通用的高级的 向redis 设置值的命令
     */
    private CacheResult jedisSetx(Jedis jedis, String key, ValueSerializeResultWrapper serializeValueWrapper, String putType, int expires,
                                  CacheTimeUnit cacheTimeUnit) {
        // 由于jedis set 命令设计不合理，无
        // set(final byte[] key, final byte[] value, final byte[] expx,final int time) 的函数，所以将nxxx 参数设置为空的byte
        byte[] putTypeBts = putType == null ? null : putType.getBytes(CacheConst.UTF_8);
        int secondExpires = this.getSecondExpires(expires, cacheTimeUnit);

        Object serializeValue = serializeValueWrapper.getSerializeValue();
        if (!(serializeValue instanceof byte[])) {
            serializeValue = InternalCacheUtils.stringEncodeBytes((String) serializeValue);
        }
        String result = null;
        if (putTypeBts == null) {
            result = jedis.setex(InternalCacheUtils.stringEncodeBytes(key), secondExpires, (byte[]) serializeValue);
        } else {
            // 由于jedis set String 最后都转成了byte ，所以，这里也转byte
            result = jedis.set(InternalCacheUtils.stringEncodeBytes(key), (byte[]) serializeValue, putTypeBts, CacheConst.REDIS_EX_BYTES,
                    secondExpires);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("redis setx command,key:{},rawValue:{},putType：{}, secondExpires:{},result:{}", key,
                    serializeValueWrapper.getRawValue(), putType, secondExpires, result);
        }
        return RedisUtil.redisResult2CacheResult(result);

    }

    /**
     * 对指定的key 设置超期时间
     */
    private CacheResult setExpire(Jedis jedis, String serialKey, int expires, CacheTimeUnit cacheTimeUnit) {
        long result = jedis.expire(serialKey, expires * cacheTimeUnit.getValue());
        return RedisUtil.redisResult2CacheResult(result);
    }


    /**
     * 获取移除key 的含有namespace pattern
     */
    protected String getEvictPattern(String pattern) {
        if (pattern == null) {
            return this.getNamespace() + "*";
        }
        return this.getNamespace() + pattern;
    }


}


