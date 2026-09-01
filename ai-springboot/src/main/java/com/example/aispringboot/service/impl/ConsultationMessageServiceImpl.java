package com.example.aispringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispringboot.DTO.response.ConsultationSessionPageResponseDTO;
import com.example.aispringboot.entity.ConsultationMessage;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.mapper.ConsultationMessageMapper;
import com.example.aispringboot.service.ConsultationMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultationMessageServiceImpl implements ConsultationMessageService {
    @Resource
    private ConsultationMessageMapper consultationMessageMapper;

    //发送者类型
    private final static byte SENDER_TYPE_USER = 1;
    private final static byte SENDER_TYPE_AI_ASSISTANT = 2;

    //消息类型
    private final static byte MESSAGE_TYPE_TEXT = 1;

    @Override
    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotionTag) {
        if(sessionId == null){
            throw new IllegalArgumentException("会话不存在");
        }
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(SENDER_TYPE_USER)
                .messageType(MESSAGE_TYPE_TEXT)
                .content(content)
                .emotionTag(emotionTag)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(message);

        return message;
    }

    @Override
    public ConsultationMessage saveAiMessage(Long sessionId, String content, String aiModel) {
        if(sessionId == null){
            throw new IllegalArgumentException("会话不存在");
        }
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(SENDER_TYPE_AI_ASSISTANT)
                .messageType(MESSAGE_TYPE_TEXT)
                .content(content)
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();

        //插入数据库
        consultationMessageMapper.insert(message);

        return message;
    }

    //获取会话消息数量
    @Override
    public Integer getMessageCount(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId);
        Integer count = consultationMessageMapper.selectCount(queryWrapper).intValue();
        return count;
    }

    @Override
    public ConsultationMessageResponseDTO getLastMessage(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .orderByDesc(ConsultationMessage::getCreatedAt)
                .last("limit 1");
        ConsultationMessage lastMessage = consultationMessageMapper.selectOne(queryWrapper);
        if(lastMessage == null){
            return null;
        }
        ConsultationMessageResponseDTO lastMessageResponseDTO = ConsultationMessageResponseDTO.builder()
                .id(lastMessage.getId())
                .sessionId(lastMessage.getSessionId())
                .senderType(lastMessage.getSenderType())
                .messageType(lastMessage.getMessageType())
                .content(lastMessage.getContent())
                .emotionTag(lastMessage.getEmotionTag())
                .aiModel(lastMessage.getAiModel())
                .createdAt(lastMessage.getCreatedAt())
                .build();
        return lastMessageResponseDTO;
    }
}
