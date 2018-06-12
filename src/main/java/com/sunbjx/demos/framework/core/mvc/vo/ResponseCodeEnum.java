package com.sunbjx.demos.framework.core.mvc.vo;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

/**
 * @author sunbjx
 * @since 2018/6/11 16:44
 */
public enum ResponseCodeEnum {

    C0(0, "success"), C1(1, "内部错误"), C2(2, "参数不能为空"), C3(3, "其他错误");

    private static Map<Integer, ResponseCodeEnum> MAPPING = Maps.newLinkedHashMap();

    static {
        Arrays.stream(ResponseCodeEnum.values()).forEach(i -> MAPPING.put(i.getCode(), i));
    }

    ResponseCodeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ResponseCodeEnum getByCode(Integer code) {
        return null == code ? null : MAPPING.get(code);
    }

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
