package com.sunbjx.demos.modules.oa.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sunbjx.demos.modules.oa.dao.UserDao;
import com.sunbjx.demos.modules.oa.model.User;
import org.springframework.stereotype.Service;

/**
 * @author: sunbjx
 * @since Created in 09:51 2017/11/23
 */
@Service
public class UserService extends ServiceImpl<UserDao, User> {

}
