package com.sunbjx.demos.controller.client;

import com.sunbjx.demos.controller.support.Response;
import com.sunbjx.demos.controller.support.Responses;
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
@RequestMapping("/")
public class LoginController {

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Response login() {

        return Responses.SUCCESS();
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public Response logout() {

        return Responses.SUCCESS();
    }

    @RequestMapping(value = "joinus", method = RequestMethod.POST)
    public Response joinus() {

        return Responses.SUCCESS();
    }
}
