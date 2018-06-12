package com.sunbjx.demos.framework.tools.codec;

/**
 * AES 的各种 模式
 *
 * @author sunbjx
 * @since 2018/6/12 15:34
 */
public enum AESModeEnum {
    /**
     * AES/CFB/NoPadding。
     */
    CFB_NO("AES/CFB/NoPadding"),

    /**
     * AES/CFB/PKCS5Padding。
     */
    CFB_PKCS5("AES/CFB/PKCS5Padding"),

    /**
     * AES/CFB/ISO10126Padding。
     */
    CFB_ISO10126("AES/CFB/ISO10126Padding"),

    /**
     * AES/CBC/NoPadding。
     */
    CBC_NO("AES/CBC/NoPadding"),

    /**
     * AES/CBC/PKCS5Padding。
     */
    CBC_PKCS5("AES/CBC/PKCS5Padding"),

    /**
     * AES/CBC/ISO10126Padding。
     */
    CBC_ISO10126("AES/CBC/ISO10126Padding"),

    /**
     * AES/ECB/NoPadding。
     */
    ECB_NO("AES/ECB/NoPadding"),

    /**
     * AES/ECB/PKCS5Padding。
     */
    ECB_PKCS5("AES/ECB/PKCS5Padding"),

    /**
     * AES/ECB/ISO10126Padding。
     */
    ECB_ISO10126("AES/ECB/ISO10126Padding");

    final String name;

    private AESModeEnum(String name) {
        this.name = name;
    }

    /**
     * 返回AES算法模式和填充方式。
     */
    public String getName() {
        return name;
    }
}
