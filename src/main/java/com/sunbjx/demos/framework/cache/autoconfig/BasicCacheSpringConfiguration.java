package com.sunbjx.demos.framework.cache.autoconfig;


import com.sunbjx.demos.framework.cache.plugin.mybatis.InternalCacheMybatisCache;
import com.sunbjx.demos.framework.cache.redis.RedisCache;
import org.apache.ibatis.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用Configuration 的方式加载 BasicCache
 *
 * @author sunbjx
 * @since 2016/10/9 15:25
 */
@Configuration
public class BasicCacheSpringConfiguration {


    @Bean(name = "iCacheMybatisCache")
    public Cache getMybatisCache(RedisCache redisCache) {
        InternalCacheMybatisCache internalCacheMybatisCache = new InternalCacheMybatisCache("iCacheMybatisCache");
        if (redisCache == null) {
            return null;
        }
        internalCacheMybatisCache.afterConfigureParse(redisCache);
        return internalCacheMybatisCache;
    }

}
