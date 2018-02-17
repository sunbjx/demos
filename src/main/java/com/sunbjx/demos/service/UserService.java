package com.sunbjx.demos.service;

import com.sunbjx.demos.model.entity.UserEntity;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 09:51 2017/11/23
 * @Modified By:
 */
public interface UserService {

    /**
     * 通过ID获取用户详情
     * @param id
     * @return
     */
    UserEntity getDetailsById(Integer id);
}
