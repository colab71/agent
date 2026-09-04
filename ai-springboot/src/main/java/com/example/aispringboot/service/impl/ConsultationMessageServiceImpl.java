package com.example.aispringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.middle.AuthorityUserDTO;
import com.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispringboot.DTO.response.ConsultationSessionPageResponseDTO;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.entity.ConsultationMessage;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.enumClass.UserType;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.mapper.ConsultationMessageMapper;
import com.example.aispringboot.mapper.ConsultationSessionMapper;
import com.example.aispringboot.mapper.UserMapper;
import com.example.aispringboot.service.ConsultationMessageService;
import com.example.aispringboot.util.AuthorUtil;
import com.example.aispringboot.util.JwtTokenUtil;
import com.example.aispringboot.util.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationMessageServiceImpl implements ConsultationMessageService {
    @Resource
    private ConsultationMessageMapper consultationMessageMapper;

    @Resource
    private AuthorUtil authorUtil;

    //发送者类型
    private final static byte SENDER_TYPE_USER = 1;
    private final static byte SENDER_TYPE_AI_ASSISTANT = 2;

    //消息类型
    private final static byte MESSAGE_TYPE_TEXT = 1;

    private final static String SESSION_ID_PREFIX = "session_";

    @Override
    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotionTag) {
        if (sessionId == null) {
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
        if (sessionId == null) {
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
        if (lastMessage == null) {
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

    @Override
    public List<ConsultationMessageResponseDTO> getMessagesBySessionId(String sessionId) {
        Long sessionIdLong = extractSessionId(sessionId);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            //判断能否成功获取sessionId
            if (sessionIdLong == null) {
                throw new BusinessException("sessionId格式错误");
            }
            //判断是否有查询权限
            if (!authorUtil.haveSelectMessageAuthority(sessionIdLong)) {
                throw new BusinessException("权限不足");
            }
        }
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionIdLong)
                .orderByAsc(ConsultationMessage::getCreatedAt);
        List<ConsultationMessage> messages = consultationMessageMapper.selectList(queryWrapper);
        if (messages.isEmpty()) {
            return Collections.emptyList();
        }
        List<ConsultationMessageResponseDTO> messageResponseDTOList = messages.stream()
                .map(message -> ConsultationMessageResponseDTO.builder()
                        .id(message.getId())
                        .sessionId(message.getSessionId())
                        .senderType(message.getSenderType())
                        .messageType(message.getMessageType())
                        .content(message.getContent())
                        .emotionTag(message.getEmotionTag())
                        .aiModel(message.getAiModel())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());


        return messageResponseDTOList;
    }

    //获取参数中的sessionId
    private Long extractSessionId(String sessionId) {
        if (sessionId.startsWith(SESSION_ID_PREFIX)) {
            return Long.parseLong(sessionId.substring(SESSION_ID_PREFIX.length()));
        }

        return null;
    }
}
