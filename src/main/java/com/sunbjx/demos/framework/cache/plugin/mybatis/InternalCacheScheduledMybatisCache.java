package com.sunbjx.demos.framework.cache.plugin.mybatis;

/**
 * 内部定时刷新的cache
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
public class InternalCacheScheduledMybatisCache extends InternalCacheMybatisCache {

    /**
     * 定时刷新的间隔.默认 1小时
     */
    private int flushInterval = 60 * 60 * 1000;

    protected long lastClear;


    public InternalCacheScheduledMybatisCache(String id) {
        super(id);
        this.lastClear = System.currentTimeMillis();
    }

    @Override
    public void putObject(Object key, Object value) {
        clearWhenStale();
        super.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return clearWhenStale() ? null : super.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        clearWhenStale();
        return super.removeObject(key);
    }

    @Override
    public void clear() {
        lastClear = System.currentTimeMillis();
        super.clear();

    }

    private boolean clearWhenStale() {
        if (System.currentTimeMillis() - lastClear > flushInterval) {
            clear();
            return true;
        }
        return false;
    }
}
