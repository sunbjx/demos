package com.sunbjx.demos.framework.cache.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.*;
import java.lang.reflect.Type;


/**
 * cache 的序列化工具
 *
 * @author sunbjx
 * @since 2016/6/13 15:56
 */
public class InternalCacheSerializeUtils {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InternalCacheSerializeUtils.class);

    /**
     * 将对象转为json
     *
     * @return string
     */
    public static String serializeJson(Object obj) {
        String json = JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        if (json == null) {
            logger.warn("对象转json 出现错误,obj class:{},value:{}", obj.getClass(), obj);
        }
        return json;
    }

    /**
     * 将json str 转为 对象
     *
     * @param <T>  the type parameter
     * @param json the json
     * @param type the type
     * @return the t
     */
    public static <T> T deSerializeJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }


    /**
     * 将对象序列化为
     */
    public static byte[] objectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } finally {
            InternalCacheUtils.closeStreamQuietly(oos, baos);
        }

    }

    /**
     * @param bts
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object bytesToObject(byte[] bts) throws IOException, ClassNotFoundException {

        if (bts == null) {
            return null;
        }
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bts);
            ois = new ObjectInputStream(bais);
            Object data = ois.readObject();
            return data;
        } finally {
            InternalCacheUtils.closeStreamQuietly(bais, ois);
        }

    }

}
