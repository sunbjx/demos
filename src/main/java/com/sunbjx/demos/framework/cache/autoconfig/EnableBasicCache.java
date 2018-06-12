package com.sunbjx.demos.framework.cache.autoconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 使用注解引入Cache
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import(BasicCacheSpringConfiguration.class)
public @interface EnableBasicCache {

}
