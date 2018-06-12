package com.sunbjx.demos.view.oa.controller;

import com.sunbjx.demos.framework.core.mvc.vo.Response;
import com.sunbjx.demos.framework.core.mvc.vo.ResponseCodeEnum;
import com.sunbjx.demos.modules.oa.model.User;
import com.sunbjx.demos.modules.oa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sunbjx
 * @since Created in 09:41 2017/11/23
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
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "query", dataType = "int")
    public Response details(@RequestParam Integer id) {
        if (null == id) {
            return Response.getErrorInstance(ResponseCodeEnum.C2.getCode(), ResponseCodeEnum.C2.getName());
        }
        User user = userService.selectById(id);
        return Response.getSuccessInstance(user);
    }

}
