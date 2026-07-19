package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String getHello() {
        return "get:helloworld";
    }

    public String postHello() {
        return "post:helloworld";
    }
}
