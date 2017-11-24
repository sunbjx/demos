package com.sunbjx.demos.context;

/**
 * @author sunbjx
 * @title ResponseConstants.java
 * @update 2017年11月22日 下午9:00:13
 */
public interface ResponseConstants {

    /*---- http ----*/
    int CODE_SUCCESS = 1;    // 请求成功
    int CODE_FAILED = -1;     // 请求失败
    int CODE_BAN = 0;     // 禁止访问

    String CODE_SUCCESS_VALUE = "SUCCESS";
    String CODE_FAILED_VALUE = "FAILED";
    String CODE_BAN_VALUE = "BAN";
}
