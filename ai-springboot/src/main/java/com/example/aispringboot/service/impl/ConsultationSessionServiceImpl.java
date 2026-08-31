package com.example.aispringboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.mapper.ConsultationSessionMapper;
import com.example.aispringboot.mapper.UserMapper;
import com.example.aispringboot.service.ConsultationSessionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultationSessionServiceImpl implements ConsultationSessionService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ConsultationSessionMapper consultationSessionMapper;

    @Override
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //判断用户是否存在
        User user = userMapper.selectById(userId);
        if(user != null){
            //创建咨询会话
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            if(StrUtil.isBlank(session.getSessionTitle())){
                session.setSessionTitle("宁渡AI助手"+ DateUtil.format(LocalDateTime.now(),"MM-dd HH:mm"));
            }

            //插入记录
            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }
}
