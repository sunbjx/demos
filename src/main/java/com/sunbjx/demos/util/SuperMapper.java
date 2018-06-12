package com.sunbjx.demos.util;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 注意这个类不要让 mp 扫描到！！
 *
 * @author sunbjx
 * @since 2018/6/11 22:25
 */
public interface SuperMapper<T> extends BaseMapper<T> {

    // 这里可以放一些公共的方法
}
