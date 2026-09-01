package com.example.aispringboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.service.UserService;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    //用户登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO){

        return Result.success(userService.login(commandDTO));
    }

    //用户注册接口
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> add(@Valid @RequestBody UserRegisterCommandDTO commandDTO){

        return Result.success(userService.register(commandDTO));
    }

    //获取用户信息
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> current(){
        String token = jwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = jwtTokenUtil.verifyToken(token);
        UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(jwt.getClaim("userId").asLong());
        return Result.success(user);
    }

    //用户退出登录
    @PostMapping("/logout")
    public Result<Void> logout(){
        userService.logout();
        return Result.success();
    }

}
