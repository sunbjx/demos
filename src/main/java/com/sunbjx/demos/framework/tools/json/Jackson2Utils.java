package com.sunbjx.demos.framework.tools.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author sunbjx
 * @since 2018/6/12 16:26
 */
public class Jackson2Utils {

    private static final Logger log = LoggerFactory.getLogger(Jackson2Utils.class);

    private final static ObjectMapper objectMapper;
    private static boolean resetStatus = false;


    /**
     * 是否打印美观格式
     */
    static boolean isPretty = false;

    static {
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
        objectMapper = new ObjectMapper(null, sp, null);
        objectMapper.enableDefaultTyping();
        resetObjectMapper();
    }


    private static void resetObjectMapper() {
        // sp.setNullValueSerializer(new NullSerializer());
        // 数字也加引号
        //objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        //objectMapper.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
        //日期格式化
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //未知属性不进行处理
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        resetStatus = true;
        return objectMapper;
    }

    /**
     * Java对象转Json字符串
     *
     * @param object Java对象，可以是对象，数组，List,Map等
     * @return json 字符串
     */
    public static String toJson(Object object) {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(object);
            if (resetStatus) {
                resetObjectMapper();
            }
        } catch (Exception e) {
            log.error("json error:" + e.getMessage());
        }
        return jsonString;

    }

    /**
     * Java对象转Json字符串
     *
     * @param object Java对象，可以是对象，数组，List,Map等
     * @return json 字符串
     */
    public static String toJson(Object object, boolean isPretty) {
        String jsonString = "";
        try {
            if (isPretty) {
                jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                jsonString = objectMapper.writeValueAsString(object);
            }
            if (resetStatus) {
                resetObjectMapper();
            }
        } catch (Exception e) {
            log.error("json error:" + e.getMessage());
        }
        return jsonString;

    }

    /**
     * JSON串转换为Java泛型对象，可以是各种类型，此方法最为强大。用法看测试用例。
     *
     * @param jsonString JSON字符串
     * @param tr         TypeReference,例如: new TypeReference< List<FamousUser> >(){}
     * @return List对象列表
     */
    public static <T> T json2GenericObject(String jsonString, TypeReference<T> tr) {
        if (jsonString == null || "".equals(jsonString)) {
            return null;
        } else {
            try {
                if (resetStatus) {
                    resetObjectMapper();
                }
                return objectMapper.readValue(jsonString, tr);
            } catch (Exception e) {
                log.error("json error:" + e.getMessage());
            }
        }
        return null;
    }


    /**
     * Json字符串转Java对象
     */
    public static <T> T json2Object(String jsonString, Class<T> c) {

        if (jsonString == null || "".equals(jsonString)) {
            return null;
        } else {
            try {
                if (resetStatus) {
                    resetObjectMapper();
                }
                return objectMapper.readValue(jsonString, c);
            } catch (Exception e) {
                log.error("json error:" + e.getMessage());
            }
        }
        return null;
    }

    //新添加的两个方法

    /**
     * 根据json串和节点名返回节点
     */
    public static JsonNode getNode(String json, String nodeName) {
        JsonNode node = null;
        try {
            node = Jackson2Utils.getObjectMapper().readTree(json);
            if (resetStatus) {
                resetObjectMapper();
            }
            return node.get(nodeName);
        } catch (JsonProcessingException e) {
            log.error("json error:", e);
        } catch (IOException e) {
            log.error("json error:", e);
        }
        return node;
    }

    /**
     * JsonNode转换为Java泛型对象，可以是各种类型，此方法最为强大。用法看测试用例。
     *
     * @param node JsonNode
     * @param tr   TypeReference,例如: new TypeReference< List<FamousUser> >(){}
     * @return List对象列表
     */
    public static <T> T jsonNode2GenericObject(JsonNode node, TypeReference<T> tr) {

        if (node == null || "".equals(node)) {
            return null;
        } else {
            try {
                JsonParser jp = new TreeTraversingParser(node);
                T t = (T) objectMapper.readValue(jp, tr);
                jp.close();
                if (resetStatus) {
                    resetObjectMapper();
                }
                return t;
            } catch (Exception e) {
                log.warn("json error:" + e.getMessage());
            }
        }
        return null;
    }
}
