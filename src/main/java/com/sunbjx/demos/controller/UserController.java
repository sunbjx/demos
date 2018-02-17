package com.sunbjx.demos.controller;

import com.sunbjx.demos.controller.support.Response;
import com.sunbjx.demos.controller.support.Responses;
import com.sunbjx.demos.model.entity.UserEntity;
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
    public Response details(@ApiParam(name = "id", value = "用户ID") @RequestParam Integer id) {

        UserEntity user = userService.getDetailsById(id);
        if (null != user) {
            return Responses.SUCCESS().setResults(user);
        }
        return Responses.FAILED();

    }

}
