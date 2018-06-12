package com.sunbjx.demos.framework.tools;

import com.sunbjx.demos.framework.tools.codec.EncodeUtils;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * 封装各种生成唯一性ID算法的工具类
 *
 * @author sunbjx
 * @since 2018/6/12 15:13
 */
public class IdGenUtils {

    private static SecureRandom random = new SecureRandom();

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    /**
     * 基于Base64编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return EncodeUtils.base64Encode(randomBytes);
    }

    /**
     * Activiti ID 生成
     */
    public String getNextId() {
        return IdGenUtils.uuid();
    }

    public static Serializable generateId() {
        return IdGenUtils.uuid();
    }

    public static void main(String[] args) {
        System.out.println(IdGenUtils.uuid());
        System.out.println(IdGenUtils.uuid().length());
        System.out.println(new IdGenUtils().getNextId());
        for (int i = 0; i < 1000; i++) {
            System.out.println(IdGenUtils.randomLong() + "  " + IdGenUtils.randomBase62(5));
        }
    }
}
