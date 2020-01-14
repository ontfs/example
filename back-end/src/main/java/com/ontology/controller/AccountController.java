package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.UserDto;
import com.ontology.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@Api(tags = "登录注册V2")
@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin
public class AccountController {
    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "注册用户名", notes = "注册用户名", httpMethod = "POST")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserDto req) throws Exception {
        String action = "register";
        Map<String, Object> result = accountService.register(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }


    @ApiOperation(value = "登录app", notes = "登录app", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserDto req) throws Exception {
        String action = "login";
        Map<String, Object> result = accountService.login(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }

}
