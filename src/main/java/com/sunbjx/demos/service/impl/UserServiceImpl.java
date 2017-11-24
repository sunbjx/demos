package com.sunbjx.demos.service.impl;

import com.sunbjx.demos.mapper.UserMapper;
import com.sunbjx.demos.model.User;
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
    private UserMapper userMapper;

    @Override
    public User getDetailsById(Integer id) {

        User user = userMapper.selectByPrimaryKey(id);

        return user;
    }
}
