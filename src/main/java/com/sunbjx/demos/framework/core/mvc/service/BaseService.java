package com.sunbjx.demos.framework.core.mvc.service;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sunbjx.demos.framework.core.mvc.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通用服务
 *
 * @author sunbjx
 * @since 2018/6/11 17:58
 */
@Transactional(rollbackFor = {ServiceException.class})
public class BaseService<M extends BaseMapper<T>, T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    protected M baseMapper;

    // 通用方法

}
