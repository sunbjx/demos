package com.sunbjx.demos.constants;

/**
 * @author sunbjx
 * @title ResponseConstants.java
 * @update 2017年11月22日 下午9:00:13
 */
public interface ResponseConstants {

    /*---- http ----*/
    int CODE_AUTH = -1;      // 请求无权限
    int CODE_FAILED = 0;     // 请求失败
    int CODE_SUCCESS = 1;    // 请求成功


    String CODE_FAILED_LOSE_SESSION = "LOSE SESSION";
    String CODE_FAILED_LOSE_TOKEN = "LOSE TOKEN";
}
