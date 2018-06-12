package com.sunbjx.demos.framework.cache.redis;


import com.sunbjx.demos.framework.cache.AbstractCache;
import com.sunbjx.demos.framework.cache.util.InternalCacheUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 所有redis cache 的抽象基类
 *
 * @author sunbjx
 * @since 2016/7/14 9:20
 */
public abstract class AbstractRedisCache extends AbstractCache {

	protected JedisPool delegate;

	/**
	 * Gets delegate.
	 *
	 * @return the delegate
	 */
	public JedisPool getDelegate() {
		return delegate;
	}

	/**
	 * Sets delegate.
	 *
	 * @param delegate the delegate
	 */
	public void setDelegate(JedisPool delegate) {
		this.delegate = delegate;
	}

	/**
	 * 获取native cache client
	 */
	@Override
	public Jedis getNativeCacheClient() {
		return this.delegate.getResource();
	}

	@Override
	public void releaseNativeCacheClient(Object nativeCacheClient) {
		if (nativeCacheClient == null) {
			return;
		}
		if (nativeCacheClient instanceof Jedis) {
			InternalCacheUtils.closeStreamQuietly((Jedis) nativeCacheClient);
		}
	}

	/**
	 * 通用的执行原生的 redis 方法，这个方法不用担心是否释放资源的问题
	 * @param callback
	 * @param <T>
	 * @return
	 */
	public <T> T execute(CallBack<T> callback) {
		Jedis jedis = getNativeCacheClient();
		try {
			return callback.executeWithJedis(jedis);
		} finally {
			this.releaseNativeCacheClient(jedis);
		}
	}


}
