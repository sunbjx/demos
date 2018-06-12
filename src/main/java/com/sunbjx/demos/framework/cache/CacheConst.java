package com.sunbjx.demos.framework.cache;

import java.nio.charset.Charset;

/**
 * Redis cache 的静态变量
 *
 * @author sunbjx
 * @since 2016/6/13 19:03
 */
public interface CacheConst {

    Charset UTF_8 = Charset.forName("UTF-8");

    String REDIS_XX = "XX";//XX -- Only set the key if it already exist.

    String REDIS_NX = "NX";//NX -- Only set the key if it does not already exist.

    String REDIS_PX = "PX";//expire time units: PX = milliseconds

    String REDIS_EX = "EX";//expire time units: EX = seconds

    byte[] REDIS_XX_BYTES = REDIS_XX.getBytes();

    byte[] REDIS_NX_BYTES = REDIS_NX.getBytes();

    byte[] REDIS_PX_BYTES = REDIS_PX.getBytes();

    byte[] REDIS_EX_BYTES = REDIS_EX.getBytes();


    byte[] BLANK_BYTES = new byte[0];

    String REDIS_OK_RESULT = "OK";// redis success response

    String SEPARATOR = ":";

    String SPRING_CACHE_FLAG = "springCache";

    String MYBATIS_CACHE_FLAG = "mybatisCache";


}
