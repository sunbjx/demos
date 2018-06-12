package com.sunbjx.demos.framework.cache.util;

/**
 * 内部cache 的断言
 *
 * @author sunbjx
 * @since 2016 -06-15 19:36:07
 */
public abstract class InternalCacheAssert {

    /**
     * Assert that an object is {@code null} .
     * <pre class="code">InternalCacheAssert.isNotNull(value, "The value must be not null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is less than radix number
     * <pre class="code">InternalCacheAssert.isLessThan(value,radix, "The value must less than radix" + radix);</pre>
     *
     * @param value the value
     * @param radix the radix
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isLessThan(long value, long radix) {
        if (value >= radix) {
            throw new IllegalArgumentException(" the value " + value + " must be less than " + radix);
        }
    }

    /**
     * Assert that an object is less than radix number
     * <pre class="code">InternalCacheAssert.isLessThan(value,radix, "The value must greater than radix" + radix);</pre>
     *
     * @param value the value
     * @param radix the radix
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isGreaterThan(long value, long radix) {
        if (value <= radix) {
            throw new IllegalArgumentException(" the value " + value + " must be greater than " + radix);
        }
    }


}
