package com.sunbjx.demos.controller;

import com.sunbjx.demos.context.Response;
import com.sunbjx.demos.context.ResponseConstants;
import com.sunbjx.demos.model.User;
import com.sunbjx.demos.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 09:41 2017/11/23
 * @Modified By:
 */
@RestController
@RequestMapping("/v1/user")
@Api(description = "用户接口（sunbjx）")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/details")
    @CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
    @ApiOperation(value = "用户详情", notes = "用户详情查询")
    public Response<User> details(@ApiParam(name = "id", value = "用户ID") @RequestParam Integer id) {

        Response<User> result = null;
        try {
            User user = userService.getDetailsById(id);
            result = new Response<User>(ResponseConstants.CODE_SUCCESS, ResponseConstants.CODE_SUCCESS_VALUE);
            result.setResults(user);
        } catch (Exception e) {
            result = new Response<User>(ResponseConstants.CODE_SUCCESS, ResponseConstants.CODE_SUCCESS_VALUE);
        }
        return result;
    }

}
