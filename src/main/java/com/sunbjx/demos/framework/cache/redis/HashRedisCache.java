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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * redis 的 set cache 实现
 *
 * @author sunbjx
 * @since 2016 -06-13 19:31:33
 */
public class HashRedisCache extends AbstractRedisCache {

    private static final Logger logger = LoggerFactory.getLogger(HashRedisCache.class);


    /**
     * 最后访问 后，自动失效时间，如果为 0，则认为不失效~
     */
    private int lassAccessExpireSeconds = 0;


    public void setLassAccessExpireSeconds(int lassAccessExpireSeconds) {
        this.lassAccessExpireSeconds = lassAccessExpireSeconds;
    }

    @Override
    protected String getFullKey(Serializable key) throws Exception {
        return keySerializeStrategy.keySerialize(key);
    }

    @Override
    public CacheResult delete(Serializable key) {
        Jedis _jedis = null;
        String fieldKey = null;
        try {
            fieldKey = getFullKey(key);
            if (logger.isDebugEnabled()) {
                logger.debug("delete String key :{}, field:{}", getNamespace(), fieldKey);
            }
            _jedis = delegate.getResource();
            long result = _jedis.hdel(getNamespace(), fieldKey);
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis delete had error，key is:{}, field:{}", getNamespace(), fieldKey == null ? key : fieldKey, e);
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
     * @author fengyapeng
     * @since 2016 -06-13 19:31:33
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
        String fieldKey = null;
        try {
            fieldKey = getFullKey(key);
            _jedis = delegate.getResource();
            T result = null;
            if (isFormatJson()) {
                String jsonStr = _jedis.hget(getNamespace(), fieldKey);
                if (logger.isTraceEnabled()) {
                    logger.trace("get String  key :{} ,field:{} ,value :{},will converter to :{}", getNamespace(), fieldKey, jsonStr,
                            typeReference);
                }
                result = deSerializeValue(jsonStr, typeReference.getType());
            } else {
                byte[] objDatas = _jedis
                        .hget(InternalCacheUtils.stringEncodeBytes(getNamespace()), InternalCacheUtils.stringEncodeBytes(fieldKey));
                T obj = deSerializeValue(objDatas, typeReference == null ? null : typeReference.getType());
                if (logger.isDebugEnabled()) {
                    logger.trace("get String  key :{} ,field:{} ,value :{},will converter to :{}", getNamespace(), fieldKey, obj,
                            typeReference);
                }
                result = obj;
            }
            resetExpire(_jedis);
            return result;
        } catch (Exception e) {
            logger.warn("redis get object had error，key is:{}，field:{} ", key, fieldKey == null ? key : fieldKey, e);
            return null;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }


    @Override
    public CacheResult exists(Serializable key) {
        Jedis _jedis = null;
        String fieldKey = null;
        try {
            fieldKey = getFullKey(key);
            _jedis = delegate.getResource();
            boolean result = _jedis.hexists(getNamespace(), fieldKey);
            resetExpire(_jedis);
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis exist object had error，key is:{}，field:{}", getNamespace(), fieldKey == null ? key : fieldKey, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long evict() {
        Jedis _jedis = null;
        try {

            _jedis = delegate.getResource();
            long hashLength = _jedis.hlen(getNamespace());
            long delResult = _jedis.del(getNamespace());
            long result = 0;
            if (hashLength != 0) {
                result = hashLength;
            } else {
                result = delResult;
            }
            logger.trace("evict operation had complete.key:{},had remove field size:{}", getNamespace(), result);
            return result;
        } catch (Exception e) {
            logger.warn("evict operation had error,use redis del command. the key:{}", getNamespace(), e);
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
        return 0;
    }

    @Override
    public long evict(String pattern) {
        throw new UnsupportedOperationException("twemproxy not support keys command,so it's un supported");
    }

    @Override
    public CacheResult put(Serializable key, Serializable value) {
        Jedis _jedis = null;
        String fieldKey = null;

        try {
            fieldKey = getFullKey(key);
            ValueSerializeResultWrapper serializeValue = serializeValue(value);
            _jedis = delegate.getResource();
            this.jedisHashSetx(_jedis, fieldKey, serializeValue, null);
            resetExpire(_jedis);
            return CacheResult.SUCCESS;
        } catch (Exception e) {
            logger.warn("redis add had error，key is:{},field:{}", key, fieldKey == null ? key : fieldKey, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    /**
     * 一次性放入多个值
     */
    public CacheResult put(Map<? extends Serializable, ? extends Serializable> values) {
        Jedis _jedis = null;
        try {
            if (values == null || values.size() == 0) {
                logger.info("redis hash put multi values is error. the value is null or empty");
            }
            Map<byte[], byte[]> hmSetValues = new HashMap<>();
            for (Iterator<? extends Map.Entry<? extends Serializable, ? extends Serializable>> iterator = values.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Serializable, Serializable> next = (Map.Entry<Serializable, Serializable>) iterator.next();
                Serializable key = next.getKey();
                Serializable value = next.getValue();
                byte[] storeKey = InternalCacheUtils.stringEncodeBytes(getFullKey(key));
                Object storeValue = serializeValue(value).getSerializeValue();
                if (!(storeValue instanceof byte[])) {
                    storeValue = InternalCacheUtils.stringEncodeBytes((String) storeValue);
                }
                hmSetValues.put(storeKey, (byte[]) storeValue);
            }
            _jedis = delegate.getResource();
            _jedis.hmset(InternalCacheUtils.stringEncodeBytes(getNamespace()), hmSetValues);
            resetExpire(_jedis);
            return CacheResult.SUCCESS;
        } catch (Exception e) {
            logger.warn("redis hash put  multi values had error.，the exception is :", e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public CacheResult put(Serializable key, Serializable value, int expires, CacheTimeUnit cacheTimeUnit) {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
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
        Jedis _jedis = null;
        String fieldKey = null;

        try {
            fieldKey = getFullKey(key);
            ValueSerializeResultWrapper serializeValue = serializeValue(value);
            _jedis = delegate.getResource();
            long result = this.jedisHashSetx(_jedis, fieldKey, serializeValue, CacheConst.REDIS_NX);
            resetExpire(_jedis);
            return RedisUtil.redisResult2CacheResult(result);
        } catch (Exception e) {
            logger.warn("redis add had error，key is:{},field:{}", key, fieldKey == null ? key : fieldKey, e);
            return CacheResult.ERROR;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
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
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }


    @Override
    public long increase(Serializable key) throws Exception {
        return increase(key, 1);
    }

    @Override
    public long increase(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }

    @Override
    public long increase(Serializable key, long value) throws Exception {
        Jedis _jedis = null;
        InternalCacheAssert.isGreaterThan(value, 0);
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            long result = _jedis.hincrBy(getNamespace(), serialKey, value);
            resetExpire(_jedis);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long increase(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }

    @Override
    public long decrease(Serializable key) throws Exception {
        return this.decrease(key, 1);
    }

    @Override
    public long decrease(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }

    /**
     * @param key   the key
     * @param value the value
     */
    @Override
    public long decrease(Serializable key, long value) throws Exception {
        Jedis _jedis = null;
        InternalCacheAssert.isGreaterThan(value, 0);
        try {
            String serialKey = getFullKey(key);
            _jedis = delegate.getResource();
            // decrease value must less than 0.
            long result = _jedis.hincrBy(getNamespace(), serialKey, 0 - value);
            resetExpire(_jedis);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(_jedis);
        }
    }

    @Override
    public long decrease(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }

    @Override
    public CacheResult resetExpire(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) {
        throw new UnsupportedOperationException("redis hash command not support set expires  operation");
    }

    /**
     * 通用的高级的 向redis 设置 hash值的命令
     *
     * @param jedis                 the jedis
     * @param field                 the field
     * @param serializeValueWrapper the serialize value wrapper
     * @param putType               如果为null，则set，如果非null 的话，使用hsetNx command
     * @return the cache result
     */
    private long jedisHashSetx(Jedis jedis, String field, ValueSerializeResultWrapper serializeValueWrapper, String putType) {
        // 由于jedis set 命令设计不合理，无
        // set(final byte[] key, final byte[] value, final byte[] expx,final int time) 的函数，所以将nxxx 参数设置为空的byte

        Object serializeValue = serializeValueWrapper.getSerializeValue();
        if (!(serializeValue instanceof byte[])) {
            serializeValue = InternalCacheUtils.stringEncodeBytes((String) serializeValue);
        }
        Long result = null;
        /**
         * HSET 和 hsetNx 的 integer-reply：含义如下 1：如果字段是个新的字段，并成功赋值 0：如果哈希集中已存在该字段，没有操作被执行
         */
        if (putType == null) {

            result = jedis.hset(InternalCacheUtils.stringEncodeBytes(getNamespace()), InternalCacheUtils.stringEncodeBytes(field),
                    (byte[]) serializeValue);
        } else {
            // 由于jedis set String 最后都转成了byte ，所以，这里也转byte
            result = jedis.hsetnx(InternalCacheUtils.stringEncodeBytes(getNamespace()), InternalCacheUtils.stringEncodeBytes(field),
                    (byte[]) serializeValue);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("redis hset command,key:{}, field:{},rawValue:{},putType：{}, result:{}", getNamespace(), field,
                    serializeValueWrapper.getRawValue(), putType, result);
        }
        resetExpire(jedis);
        return result;
    }


    private long resetExpire(Jedis jedis) {
        if (lassAccessExpireSeconds == 0) {
            return lassAccessExpireSeconds;
        }
        try {
            long result = jedis.expire(getNamespace(), lassAccessExpireSeconds);
            logger.debug("redis reset expire result:{},key is:{}", result, getNamespace());
            return result;
        } catch (Exception e) {
            logger.warn("redis reset expire had error，key is:{}", getNamespace(), e);
            return 0;
        }
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

    /**
     * 获取hash 中的所有的字段。请谨慎使用。因为如果数据量大的话，有可能会造成wait 时间长
     *
     * @param <T>           the type parameter
     * @param typeReference the type reference
     * @return the all values
     */
    public <T> HashMap<String, T> getAllValues(TypeReference<T> typeReference) {
        HashMap<String, T> result = new HashMap<>();
        Jedis jedis = null;
        try {
            jedis = this.delegate.getResource();
            ScanResult<Map.Entry<byte[], byte[]>> hscanResult = jedis
                    .hscan(getNamespace().getBytes(CacheConst.UTF_8), "0".getBytes(CacheConst.UTF_8));
            List<Map.Entry<byte[], byte[]>> hcanEntryList = hscanResult.getResult();
            for (Map.Entry<byte[], byte[]> entry : hcanEntryList) {
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();
                T deValue = null;
                try {
                    deValue = deSerializeValue(isFormatJson() ? InternalCacheUtils.stringDecodeBytes(value) : value,
                            typeReference.getType());
                } catch (Exception e) {
                    logger.warn("反序列化 value 出现错误,key:{},value:{}", InternalCacheUtils.stringDecodeBytes(key),
                            InternalCacheUtils.stringDecodeBytes(value), e);
                }
                if (deValue != null) {
                    result.put(InternalCacheUtils.stringDecodeBytes(key), deValue);
                }
            }
            resetExpire(jedis);
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(jedis);
        }
    }

    /**
     * 获取所有的字段。请谨慎使用。因为如果数据量大的话，有可能会造成wait 时间长
     *
     * @return all values
     */
    public HashMap<String, Object> getAllValues() {

        HashMap<String, Object> result = new HashMap<>();
        Jedis jedis = null;
        try {
            jedis = this.delegate.getResource();
            ScanResult<Map.Entry<byte[], byte[]>> hscanResult = jedis
                    .hscan(getNamespace().getBytes(CacheConst.UTF_8), "0".getBytes(CacheConst.UTF_8));
            List<Map.Entry<byte[], byte[]>> hcanEntryList = hscanResult.getResult();
            for (Map.Entry<byte[], byte[]> entry : hcanEntryList) {
                byte[] key = entry.getKey();
                byte[] value = entry.getValue();
                Object deValue = null;
                try {
                    deValue = deSerializeValue(value, null);
                } catch (Exception e) {
                    logger.warn("反序列化 value 出现错误,key:{},value:{}", InternalCacheUtils.stringDecodeBytes(key),
                            InternalCacheUtils.stringDecodeBytes(value), e);
                }
                if (deValue != null) {
                    result.put(InternalCacheUtils.stringDecodeBytes(key), deValue);
                }
            }
            return result;
        } finally {
            InternalCacheUtils.closeStreamQuietly(jedis);
        }
    }


}


