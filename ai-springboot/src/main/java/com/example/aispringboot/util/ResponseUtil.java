package com.example.aispringboot.util;

import cn.hutool.json.JSONUtil;
import com.example.aispringboot.common.Result;
import org.springframework.http.HttpStatus;

import com.example.aispringboot.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

//处理过滤器异常响应
public class ResponseUtil {
    public static void writeError(HttpServletResponse response, ResultCode resultCode){
        //根据不同状态码返回不同响应
        int status = switch (resultCode){
            case UNAUTHORIZED , ACCESS_UNAUTHORIZED,TOKEN_INVALID,TOKEN_EXPIRED,TOKEN_BLOCKED -> HttpStatus.UNAUTHORIZED.value();
            case TOKEN_ACCESS_FORBIDDEN -> HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try(PrintWriter writer = response.getWriter()){
            String jsonResponse = JSONUtil.toJsonStr(Result.error(resultCode.getCode(), resultCode.getMessage(), null));
            writer.print(jsonResponse);
            writer.flush();//确保将响应内容写道输出流
        }catch(IOException e){
            System.out.println("写入响应失败"+e);
        }
    }
}
