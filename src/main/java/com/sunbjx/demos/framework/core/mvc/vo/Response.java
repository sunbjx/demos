package com.sunbjx.demos.framework.core.mvc.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author sunbjx
 * @since 2018/6/11 16:37
 */
public class Response<T> {

    private boolean success = true;
    private int code;
    private String message;
    private T data;

    /**
     * 静态构建类
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> getSuccessInstance(T data) {
        return getSuccessInstance(ResponseCodeEnum.C0.getCode(), ResponseCodeEnum.C0.getName(), data);
    }

    /**
     * 静态构建类
     *
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> getSuccessInstance(int code, String message, T data) {
        return getInstance(true, code, message, data);
    }

    /**
     * 静态构建类
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Response<T> getErrorInstance(int code, String message) {
        return getErrorInstance(code, message, null);
    }

    /**
     * 静态构建类
     *
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> getErrorInstance(int code, String message, T data) {
        return getInstance(false, code, message, data);
    }

    /**
     * 获取实例
     *
     * @param success
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Response<T> getInstance(boolean success, int code, String message, T data) {
        Response<T> response = new Response<>();
        response.setSuccess(success);
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    private void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
