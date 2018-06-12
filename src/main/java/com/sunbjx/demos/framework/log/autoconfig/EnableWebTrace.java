package com.sunbjx.demos.framework.log.autoconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author sunbjx
 * @since 2016/9/21 14:21
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import(TraceAutoConfiguration.class)
public @interface EnableWebTrace {

}
