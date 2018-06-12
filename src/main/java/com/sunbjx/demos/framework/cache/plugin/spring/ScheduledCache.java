package com.sunbjx.demos.framework.cache.plugin.spring;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * simple定时清除cache
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
public class ScheduledCache implements Cache {


    private Cache delegate;

    private int cacheIntervalMillis = 60 * 60 * 1000;

    private long lastClearTime;

    public ScheduledCache(Cache delegate) {
        this.delegate = delegate;
        this.lastClearTime = System.currentTimeMillis();
    }

    public ScheduledCache(Cache delegate, int cacheIntervalMillis) {
        this.delegate = delegate;
        this.cacheIntervalMillis = cacheIntervalMillis;
        this.lastClearTime = System.currentTimeMillis();

    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        if (clearWhenStale() == true) {
            return null;
        } else {
            return this.delegate.get(key);
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (clearWhenStale() == true) {
            return null;
        } else {
            return this.delegate.get(key, type);
        }
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (clearWhenStale() == true) {
            return null;
        } else {
            return this.delegate.get(key, valueLoader);
        }
    }

    @Override
    public void put(Object key, Object value) {
        clearWhenStale();
        this.delegate.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        clearWhenStale();
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        clearWhenStale();
        this.delegate.evict(key);
    }

    @Override
    public void clear() {
        this.lastClearTime = System.currentTimeMillis();
        this.delegate.clear();
    }

    private boolean clearWhenStale() {
        if (System.currentTimeMillis() - lastClearTime > cacheIntervalMillis) {
            clear();
            return true;
        }
        return false;
    }
}
