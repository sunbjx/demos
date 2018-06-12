package com.sunbjx.demos.framework.tools.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author sunbjx
 * @since 2018/6/12 16:24
 */
public class FastJsonUtils {

    private static Logger logger = LoggerFactory.getLogger(FastJsonUtils.class);
//    private static FastJsonUtils instance = new FastJsonUtils();

    private FastJsonUtils() {

    }

//    public static FastJsonUtils getInstance() {
//    	return instance;
//    }

    /**
     * 将对象序列化成json字符串。
     *
     * @param obj 要被序列化的对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return FastJsonUtils.toJson(obj, false);
    }

    /**
     * 将对象序列化成字节数组
     *
     * @param obj 要被序列化的对象
     * @return 字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        return FastJsonUtils.toJsonBytes(obj, false);
    }

    /**
     * 将json反序列化成java对象。Class&lt;T&gt;只能是JavaBean对象及其数组对象，不能是集合，Map。<br>
     * 当然，JavaBean内部是可以包含集合，Map的。
     *
     * @param json  json字符串
     * @param clazz 目标类型
     * @return Class&lt;T&gt;的实例
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return FastJsonUtils.fromJson(json, clazz, false);
    }

    /**
     * 将json反序列化成java对象List。可使用{@link#fromJson(String, TypeReference)}代替，后者更通用。
     *
     * @param json  json字符串
     * @param clazz 目标类型
     * @return Class&lt;T&gt;的实例List
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        return FastJsonUtils.fromJsonArray(json, clazz, false);
    }

    /**
     * 将json反序列化成java对象。适用于任何Java对象。TypeReference用法如：<br>
     * new TypeReference&lt;Map&lt;String, List&lt;User&gt;&gt;&gt;(){}，new TypeReference&lt;List&lt;User&gt;&gt;(){}，<br>
     * new TypeReference&lt;User&gt;(){}，new TypeReference&lt;User[]&gt;(){}<br>
     * 泛型参数Map&lt;String, List&lt;User&gt;&gt;，List&lt;User&gt;，User，User[]即为你要反序列化的目标类型。
     *
     * @param json      json字符串
     * @param reference 泛型类型引用
     * @return &lt;T&gt;的实例
     */
    public static <T> T fromJson(String json, TypeReference<T> reference) {
        return FastJsonUtils.fromJson(json, reference, false);
    }


    /**
     * 将对象序列化成json字符串。
     *
     * @param obj 要被序列化的对象
     * @return json字符串
     */
    public static String toJson(Object obj, boolean logRecord) {
        String json = JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        if (logRecord) {
            logger.debug("object to Json string,  obj class:{},json:{}", obj.getClass(), json);
        }
        return json;
    }

    /**
     * 将对象序列化成字节数组
     *
     * @param obj 要被序列化的对象
     * @return 字节数组
     */
    public static byte[] toJsonBytes(Object obj, boolean logRecord) {
        if (logRecord) {
            logger.debug("object to Json bytes， obj:{}", obj);
        }
        byte[] json = JSON.toJSONBytes(obj, SerializerFeature.WriteDateUseDateFormat);
        return json;
    }

    /**
     * 将json反序列化成java对象。Class&lt;T&gt;只能是JavaBean对象及其数组对象，不能是集合，Map。<br>
     * 当然，JavaBean内部是可以包含集合，Map的。
     *
     * @param json  json字符串
     * @param clazz 目标类型
     * @return Class&lt;T&gt;的实例
     */
    public static <T> T fromJson(String json, Class<T> clazz, boolean logRecord) {
        if (logRecord) {
            logger.debug("from Json to {}, json data:{}", clazz, json);
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将json反序列化成java对象List。可使用{@link#fromJson(String, TypeReference)}代替，后者更通用。
     *
     * @param json  json字符串
     * @param clazz 目标类型
     * @return Class&lt;T&gt;的实例List
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz, boolean logRecord) {
        if (logRecord) {
            logger.debug("from JsonArray to {}, json data:{}", clazz, json);
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将json反序列化成java对象。适用于任何Java对象。TypeReference用法如：<br>
     * new TypeReference&lt;Map&lt;String, List&lt;User&gt;&gt;&gt;(){}，new TypeReference&lt;List&lt;User&gt;&gt;(){}，<br>
     * new TypeReference&lt;User&gt;(){}，new TypeReference&lt;User[]&gt;(){}<br>
     * 泛型参数Map&lt;String, List&lt;User&gt;&gt;，List&lt;User&gt;，User，User[]即为你要反序列化的目标类型。
     *
     * @param <T>       the type parameter
     * @param json      json字符串
     * @param reference 泛型类型引用
     * @param logRecord 日志记录
     * @return &lt;T&gt;的实例
     */
    public static <T> T fromJson(String json, TypeReference<T> reference, boolean logRecord) {
        if (logRecord) {
            logger.debug("from json to {}, json data:{}", reference.getType(), json);
        }
        return JSON.parseObject(json, reference.getType());
    }
}
