package com.sunbjx.demos.framework.cache.util;


import com.sunbjx.demos.framework.cache.CacheConst;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;


/**
 * cache 的通用工具
 *
 * @author sunbjx
 * @since 2016/5/9 9:19
 */
public class InternalCacheUtils {


    private static final Logger logger = LoggerFactory.getLogger(InternalCacheUtils.class);


    /**
     * 静默关闭 Stream,可用于关闭stream，jedis，shardedJedis 等
     *
     * @param closeable the closeable
     */
    public static void closeStreamQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 静默关闭 Stream,可用于关闭stream，jedis，shardedJedis 等
     *
     * @param closeables the closeables
     */
    public static void closeStreamQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            closeStreamQuietly(closeable);
        }
    }

    /**
     * 是不是空的字符串
     */
    public static boolean isBlank(String value) {
        if (value == null) {
            return true;
        }
        if (value.trim().equals("")) {
            return true;
        }
        return false;
    }


    public static byte[] stringEncodeBytes(String value) {
        return value.getBytes(CacheConst.UTF_8);
    }

    public static String stringDecodeBytes(byte[] bytes) {
        return new String(bytes, CacheConst.UTF_8);
    }

    /**
     * Encode base 64 string string.
     *
     * @param bts the bts
     * @return string
     */
    public static String encodeBase64String(byte[] bts) {
        return new String(new Base64().encode(bts), CacheConst.UTF_8);
    }

}
