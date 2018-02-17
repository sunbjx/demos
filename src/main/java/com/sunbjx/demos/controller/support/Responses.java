package com.sunbjx.demos.controller.support;

import com.sunbjx.demos.constants.ResponseConstants;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 02:58 2018/2/8
 * @Modified By:
 */
public class Responses {

    public static Response SUCCESS() {
        return new Response(ResponseConstants.CODE_SUCCESS);
    }

    public static Response FAILED() {
        return new Response(ResponseConstants.CODE_FAILED);
    }

    public static Response AUTH() {
        return new Response(ResponseConstants.CODE_AUTH);
    }
}
