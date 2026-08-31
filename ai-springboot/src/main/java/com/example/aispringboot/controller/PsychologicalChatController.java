package com.example.aispringboot.controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispringboot.AiService.PsychologicalSupportService;
import com.example.aispringboot.AiService.StructOutPut;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.command.ConsultationStreamDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.service.UserService;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChatController {

    @Resource
    private PsychologicalSupportService psychologicalSupportService;

    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChat> startSession(@Valid @RequestBody ConsultationSessionCreateDTO createDTO){
        //获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        //调用服务层
        StructOutPut.StreamChat streamChat = psychologicalSupportService.startSession(userId, createDTO);
        return Result.success(streamChat);
    }

    @PostMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO){
        //获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        if(userId == null){
            return Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("error")
                            .data(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMessage(),"用户未登录")))
                            .build()
            );
        }

        //开始流式对话

        return psychologicalSupportService.streamPsychologicalChat(streamDTO.getSessionId(),streamDTO.getUserMessage())
                .map(
                        fragment ->{
                            return ServerSentEvent.<String>builder()
                                    .event("message")
                                    .data(JSONUtil.toJsonStr(Result.success(Map.of("content",fragment,"type","normal"))))
                                    .build();
                        }
                )
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("{}")
                                .build()
                ))
                .onErrorResume(error -> Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("error")
                                .data(JSONUtil.toJsonStr(Result.error(ResultCode.SYSTEM_ERROR.getCode(), ResultCode.SYSTEM_ERROR.getMessage(), "流式对话响应失败")))
                                .build()
                ))
                .delayElements(Duration.ofMillis(50));//添加延迟确保流式数据的体验
    }
}
