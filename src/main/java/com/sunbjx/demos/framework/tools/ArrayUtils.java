package com.sunbjx.demos.framework.tools;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunbjx
 * @since 2018/6/12 15:03
 */
public class ArrayUtils {
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T[]> clazz, int length) {
        return (T[]) Array.newInstance(clazz.getComponentType(), length);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArrayClass(Class<T> clazz, int length) {
        return (T[]) Array.newInstance(clazz, length);
    }

    /**
     * 将数组转化为list，并非arrayList
     */
    public static <T> List<T> toList(T[] ts) {
        return Arrays.asList(ts);
    }
}
