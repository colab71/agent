package com.example.demo.controller;

import com.example.demo.entity.Result;
import com.example.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test1")
    public Result test1() {
        return Result.success(testService.getHello());
    }

    @PostMapping("/test2")
    public Result test2() {
        return Result.success(testService.postHello());
    }
}
