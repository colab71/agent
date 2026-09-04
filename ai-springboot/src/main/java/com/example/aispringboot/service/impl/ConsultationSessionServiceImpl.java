package com.example.aispringboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationSessionPageResponseDTO;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.entity.ConsultationMessage;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.mapper.ConsultationMessageMapper;
import com.example.aispringboot.mapper.ConsultationSessionMapper;
import com.example.aispringboot.mapper.UserMapper;
import com.example.aispringboot.service.ConsultationSessionService;
import com.example.aispringboot.util.AuthorUtil;
import com.example.aispringboot.util.JwtTokenUtil;
import com.example.aispringboot.util.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class ConsultationSessionServiceImpl implements ConsultationSessionService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ConsultationSessionMapper consultationSessionMapper;

    @Resource
    private ConsultationMessageMapper consultationMessageMapper;

    @Resource
    private AuthorUtil authorUtil;

    private final static String SESSION_ID_PREFIX = "session_";

    @Override
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //判断用户是否存在
        User user = userMapper.selectById(userId);
        if (user != null) {
            //创建咨询会话
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            if (StrUtil.isBlank(session.getSessionTitle())) {
                session.setSessionTitle("宁渡AI助手" + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm"));
            }

            //插入记录
            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }

    @Override
    public Page<ConsultationSessionPageResponseDTO> getSessionByPage(Long pageNum, Long pageSize) {
        Page<ConsultationSessionPageResponseDTO> page = new Page<>(pageNum, pageSize);
        //判断是否为管理端
        if (!authorUtil.isAdmin()) {
            return consultationSessionMapper.selectUserSessionPage(page, authorUtil.getUserId());
        }
        return consultationSessionMapper.selectSessionPage(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        Long sessionIdLong = extractSessionId(sessionId);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            //判断能否成功获取sessionId
            if (sessionIdLong == null) {
                throw new BusinessException("sessionId格式错误");
            }
            //判断会话是否存在
            ConsultationSession session = consultationSessionMapper.selectById(sessionIdLong);
            if (session == null) {
                return;
            }
            //判断这个会话是否属于这个用户
            if (!authorUtil.isAdmin() && !authorUtil.isSessionOwner(sessionIdLong)) {
                throw new BusinessException("权限不足");
            }
        }
        //删除咨询对话
        consultationSessionMapper.deleteById(sessionIdLong);
        //删除咨询对话消息记录
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionIdLong);
        consultationMessageMapper.delete(queryWrapper);
    }

    //获取参数中的sessionId
    private Long extractSessionId(String sessionId) {
        if (sessionId.startsWith(SESSION_ID_PREFIX)) {
            return Long.parseLong(sessionId.substring(SESSION_ID_PREFIX.length()));
        }

        return null;
    }
}
