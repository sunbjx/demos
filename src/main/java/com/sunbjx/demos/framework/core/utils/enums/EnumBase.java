package com.sunbjx.demos.framework.core.utils.enums;

/**
 * 用于枚举需要实现的接口
 *
 * @author sunbjx
 * @since 2018/6/12 11:05
 */
public interface EnumBase<K> {
    /**
     * 得到枚举对应的code,一般保存这个code至数据库
     */
    K getCode();

    /**
     * 得到枚举描述
     */
    String getText();

    /**
     * 枚举名称
     */
    String name();
}
