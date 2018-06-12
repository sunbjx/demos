package com.sunbjx.demos.framework.core.utils.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举工具类
 * @author sunbjx
 * @since 2018/6/12 11:09
 */
public class EnumBaseUtils {
    private static ConcurrentHashMap<Class<? extends EnumBase>, LinkedHashMap> cache = new ConcurrentHashMap<>();

    /**
     * 将EnumBase.getCode()作为Key,EnumBase.getText()作为value,存放在Map中并返回
     */
    public static <T extends EnumBase> LinkedHashMap toMap(Class<? extends EnumBase> enumClass) {
        return toMap(enumClass.getEnumConstants());
    }

    /**
     * 将EnumBase.getCode()作为Key,EnumBase.getText()作为value,存放在Map中并返回
     */
    public static <T extends EnumBase> LinkedHashMap toMap(T[] values) {
        LinkedHashMap map = new LinkedHashMap();
        for (EnumBase item : values) {
            map.put(item.getCode(), item.getText());
        }
        return map;
    }

    /**
     * 将EnumBase.getCode()作为Key,EnumBase.getText()作为value,存放在Map中并返回
     */
    public static <T extends EnumBase> LinkedHashMap toInverseMap(T[] values) {
        LinkedHashMap map = new LinkedHashMap();
        for (EnumBase item : values) {
            map.put(item.getText(), item.getCode());
        }
        return map;
    }


    /**
     * 将EnumBase.getCode()作为Key,EnumBase作为value,存放在Map中并返回，并将map 放入cache 中
     */
    public static <T extends EnumBase> LinkedHashMap toCodeEnumsMap(Class<? extends EnumBase> enumBaseCls) {
        LinkedHashMap codeEnumsMaps = cache.get(enumBaseCls);
        if (codeEnumsMaps == null) {
            codeEnumsMaps = toCodeEnumsMap(enumBaseCls.getEnumConstants());
            cache.put(enumBaseCls, codeEnumsMaps);
        }
        return codeEnumsMaps;
    }

    /**
     * 将EnumBase.getCode()作为Key,EnumBase作为value,存放在Map中并返回
     */
    public static <T extends EnumBase> LinkedHashMap toCodeEnumsMap(T[] values) {
        LinkedHashMap map = new LinkedHashMap();
        for (EnumBase item : values) {
            map.put(item.getCode(), item);
        }
        return map;
    }

    public static <T extends EnumBase<K>, K> K getCode(T enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.getCode();
    }

    public static <T extends EnumBase> String getText(T enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.getText();
    }

    public static <T extends Enum> String getName(T enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.name();
    }

    /**
     * 根据code从cache  中查找得到Enum
     */
    public static <T extends EnumBase> T getByCode(Object code, Class<? extends EnumBase> enumClass) {
        if (code == null) {
            return null;
        }
        if (code instanceof String && StringUtils.isBlank((String) code)) {
            return null;
        }
        LinkedHashMap hashMap = toCodeEnumsMap(enumClass);
        return (T) hashMap.get(code);
    }

    /**
     * 根据code查找得到Enum
     */
    public static <T extends EnumBase> T getByCode(Object code, T[] values) {
        if (code == null) {
            return null;
        }
        if (code instanceof String && StringUtils.isBlank((String) code)) {
            return null;
        }

        for (T item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据code得到Enum,找不到则抛异常.如果code为null或者是空字符串,则返回null
     *
     * @throws IllegalArgumentException 根据code得到Enum,找不到则抛异常
     */
    public static <T extends EnumBase> T getRequiredByCode(Object code, Class<? extends EnumBase> enumClass) {
        if (code == null) {
            return null;
        }
        if (code instanceof String && StringUtils.isBlank((String) code)) {
            return null;
        }

        EnumBase v = getByCode(code, enumClass);
        if (v == null) {
            throw new IllegalArgumentException("not found Enum:" + enumClass + " value by code:" + code);
        }
        return (T) v;
    }

    /**
     * 根据code得到Enum,找不到则抛异常.如果code为null或者是空字符串,则返回null
     *
     * @throws IllegalArgumentException 根据code得到Enum,找不到则抛异常
     */
    public static <T extends EnumBase> T getRequiredByCode(Object code, T[] values) throws IllegalArgumentException {
        if (code == null) {
            return null;
        }
        if (code instanceof String && StringUtils.isBlank((String) code)) {
            return null;
        }

        EnumBase v = getByCode(code, values);
        if (v == null) {
            if (values.length > 0) {
                String className = values[0].getClass().getName();
                throw new IllegalArgumentException("not found Enum:" + className + " value by code:" + code);
            } else {
                throw new IllegalArgumentException("not found Enum by code:" + code);
            }
        }
        return (T) v;
    }
}
