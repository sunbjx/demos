package com.sunbjx.demos.service.impl;

import com.sunbjx.demos.dao.UserDao;
import com.sunbjx.demos.model.entity.UserEntity;
import com.sunbjx.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 09:53 2017/11/23
 * @Modified By:
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity getDetailsById(Integer id) {

        UserEntity user = userDao.selectByPrimaryKey(id);

        return user;
    }
}
