package com.sunbjx.demos.framework.tools.reflect.bean;

import java.util.HashMap;

/**
 * 字段 与 列 映射MAP
 *
 * @author sunbjx
 * @since 2016/2/14 18:53
 */
public class FieldColumnMap extends HashMap<String, String> {


    private Class cla;


    public FieldColumnMap(Class cla) {
        super();
        this.cla = cla;
    }


}
