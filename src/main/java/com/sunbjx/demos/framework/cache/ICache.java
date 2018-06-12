/*
 * ICache.java
 * 
 */
package com.sunbjx.demos.framework.cache;

import com.alibaba.fastjson.TypeReference;

import java.io.Serializable;


/**
 * icache  接口，含有memcached 和redis 2种实现；
 * 后续可以加入更多的实现。iache 接口受限于memached ，redis 等底层cache 接口。
 * 对于memcache 一定含有超期值，那么实现的接口如果无超期时间，则按照 memcached 的超期的最大时间设置
 *
 * @author sunbjx
 * @version 2.0
 * @since 2015 /12/03 15:11:01
 */
public interface ICache {

    /**
     * 获取代理的类型
     */
    Object getDelegate();


    /**
     * 设置命名空间
     *
     * @return the namespace
     */
    String getNamespace();


    /**
     * 是否格式化为json 数据
     *
     * @return the boolean
     */
    boolean isFormatJson();

    /**
     * 始终放入 key，value
     *
     * @param key   the key
     * @param value the value
     * @return boolean boolean
     */
    CacheResult put(Serializable key, Serializable value);

    /**
     * 始终放入 key，value
     *
     * @param key           the key
     * @param value         the value
     * @param expire        the expire ，超期的值
     * @param cacheTimeUnit the cache time unit 超期值得单位
     * @return boolean boolean
     */
    CacheResult put(Serializable key, Serializable value, int expire, CacheTimeUnit cacheTimeUnit);

    /**
     * 当cache 中不存在key的时候放入成功；否则放入失败；该操作为原子性。
     * 适用于高并发情况判断是否存在key，不存在，放入的情况。
     * if(!cache.contain(key)){
     * cache.put(key,value)
     * }
     * 如果memcache 调用命令出现异常，则返回null【ps：这里尤其需要注意】
     *
     * @param key   the key
     * @param value the value
     * @return boolean boolean
     */
    CacheResult putIfAbsent(Serializable key, Serializable value);

    /**
     * 当cache 中不存在key的时候放入成功；否则放入失败；该操作为原子性。
     * 适用于高并发情况判断是否存在key，不存在，放入的情况。
     * if(!cache.contain(key)){
     * cache.put(key,value)
     * }
     * 如果memcache 调用命令出现异常，则返回null【ps：这里尤其需要注意】
     *
     * @param key           the key
     * @param value         the value
     * @param expire        the expire ，超期的值
     * @param cacheTimeUnit the cache time unit 超期值得单位
     * @return boolean boolean
     */
    CacheResult putIfAbsent(Serializable key, Serializable value, int expire, CacheTimeUnit cacheTimeUnit);


    /**
     * Delete boolean.
     *
     * @param key the key
     * @return the boolean
     */
    CacheResult delete(Serializable key);


    /**
     * 根据key 获取对象
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the t
     */
    <T> T get(Serializable key);


    /**
     * 根据typeReference 获取对象
     *
     * @param <T>  the type parameter
     * @param key  the key
     * @param type the type
     * @return the t
     */
    <T> T get(Serializable key, TypeReference<T> type);


    /**
     * 判断key 是否存在
     *
     * @param key the key
     * @return the boolean
     */
    CacheResult exists(Serializable key);

    /**
     * 清除当前namespace 下的所有key,很抱歉，所有的实现都不支持evict 方法
     *
     * @return the long.移除的个数
     */
    long evict();


    /**
     * 根据正则表达式清除指定的key，很抱歉，所有的实现都不支持evict 方法
     *
     * @param pattern the pattern
     * @return the long
     */
    long evict(String pattern);


    /**
     * 自增命令，如果key 不存在，自动创建0,并自增
     *
     * @param key the key
     * @return the long
     * @throws Exception the exception
     */
    long increase(Serializable key) throws Exception;


    /**
     * 自增命令，并这是超时时间，如果key 不存在，自动创建0,并自增
     *
     * @param key           the key
     * @param expires       the expires ，必须大于0
     * @param cacheTimeUnit the cache time unit
     * @return the long
     * @throws Exception the exception
     */
    long increase(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception;

    /**
     * 自增 指定的值命令，如果key 不存在，自动创建0,并自增
     *
     * @param key   the key
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    long increase(Serializable key, long value) throws Exception;


    /**
     * 自增 指定的值命令，并这是超时时间，如果key 不存在，自动创建0
     *
     * @param key           the key
     * @param value         the value
     * @param expires       the expires 必须大于0
     * @param cacheTimeUnit the cache time unit
     * @return the long
     * @throws Exception the exception
     */
    long increase(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception;


    /**
     * 自减命令，如果key 不存在，自动创建0,并自减
     *
     * @param key the key
     * @return the long
     * @throws Exception the exception
     */
    long decrease(Serializable key) throws Exception;


    /**
     * 自减命令，如果key 不存在，自动创建0,并自减
     *
     * @param key           the key
     * @param expires       the expires 必须大于0
     * @param cacheTimeUnit the cache time unit
     * @return the long
     * @throws Exception the exception
     */
    long decrease(Serializable key, int expires, CacheTimeUnit cacheTimeUnit) throws Exception;

    /**
     * 自减 指定的值 命令，如果key 不存在，自动创建0,并自减
     *
     * @param key   the key
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    long decrease(Serializable key, long value) throws Exception;

    /**
     * 自减 指定的值 命令，如果key 不存在，自动创建0,并自减
     *
     * @param key           the key
     * @param value         the value
     * @param expires       the expires 必须大于0
     * @param cacheTimeUnit the cache time unit
     * @return the long
     * @throws Exception the exception
     */
    long decrease(Serializable key, long value, int expires, CacheTimeUnit cacheTimeUnit) throws Exception;

    /**
     * 获取native 的cache client
     *
     * @return the native cache client
     */
    Object getNativeCacheClient();


    /**
     * 释放 native 的cache client
     *
     * @param nativeCacheClient the native cache client
     * @return the object
     */
    void releaseNativeCacheClient(Object nativeCacheClient);


    /**
     * 重新设置 key  的超时时间。
     *
     * @param key           the key
     * @param expires       the expires
     * @param cacheTimeUnit the cache time unit
     * @return the cache result
     */
    CacheResult resetExpire(Serializable key, int expires, CacheTimeUnit cacheTimeUnit);


}
