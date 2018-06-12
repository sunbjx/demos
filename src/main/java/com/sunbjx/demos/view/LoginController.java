package com.sunbjx.demos.view;

import com.sunbjx.demos.framework.core.mvc.vo.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: sunbjx
 * @Description:
 * @Date Created in 09:02 2018/2/17
 * @Modified By:
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Response login() {

        return Response.getSuccessInstance(null);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public Response logout() {

        return Response.getSuccessInstance(null);
    }

    @RequestMapping(value = "joinus", method = RequestMethod.POST)
    public Response joinus() {

        return Response.getSuccessInstance(null);
    }
}
