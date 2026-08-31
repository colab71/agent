package com.example.aispringboot.AiService.impl;

import com.example.aispringboot.AiService.PromptManage;
import com.example.aispringboot.AiService.PsychologicalSupportService;
import com.example.aispringboot.AiService.StructOutPut;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.service.ConsultationMessageService;
import com.example.aispringboot.service.ConsultationSessionService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologicalSupportServiceImpl implements PsychologicalSupportService {

    //创建聊天室
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Resource
    private ConsultationSessionService consultationSessionService;

    @Resource
    private ConsultationMessageService consultationMessageService;

    //发送者类型
    private final static byte SENDER_TYPE_USER = 1;
    private final static byte SENDER_TYPE_AI_ASSISTANT = 2;

    private final static String SESSION_ID_PREFIX = "session_";

    private final static String CONVERSATION_ID_PREFIX = "conversation_";

    private final static String AI_MODEL = "openai";

    private final static long EXPRIY_TIME = 1000 * 60 * 60 * 24;//24小时过期

    private final static String STATUS_ACTIVE = "ACTIVE";

    @Override
    public StructOutPut.StreamChat startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //数据库创建会话记录
        ConsultationSession session = consultationSessionService.createSession(userId, createDTO);
        if (session == null) {
            throw new BusinessException("创建会话失败，该用户不存在");
        }

        //数据库创建会话记录
        consultationMessageService.saveUserMessage(session.getId(), createDTO.getInitialMessage(), null);

        //创建会话消息
        return new StructOutPut.StreamChat(
                SESSION_ID_PREFIX + session.getId(),
                userId,
                createDTO.getSessionTitle(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + EXPRIY_TIME,
                1,
                STATUS_ACTIVE
        );
    }

    @Override
    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {
        //创建响应流
        return Flux.create(sink -> {
            // sink.next("数据1") // 发布数据
            // sink.complete() // 完成流
            // sink.error(exception) // 发布错误
            Long dbSessionId = extractSessionId(sessionId);
            if (dbSessionId == null) {
                throw new RuntimeException("会话ID格式错误");
            }

            //检查是否为初始消息，避免重复保存
            boolean isInitMessage = false;
            Integer messageCount = consultationMessageService.getMessageCount(dbSessionId);
            if (messageCount == 1) {
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessage(dbSessionId);
                if (lastMessage != null && lastMessage.getSenderType() == SENDER_TYPE_USER && userMessage.equals(lastMessage.getContent())) {
                    isInitMessage = true;
                }
            }
            if (!isInitMessage) {
                //保存数据到数据库
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }

            //进行流式对话
            //生成对话记忆管理
            String conversationId = CONVERSATION_ID_PREFIX + sessionId;
            //构建用户提示词
            List<Message> userMessages = new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            chatMemory.add(conversationId, userMessages);
            //构建系统提示词
            Prompt prompt = new Prompt(
                    List.of(
                            new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
                    )
            );
            //用户储存ai完整响应
            StringBuilder fullResponse = new StringBuilder();
            //使用chatClient进行对话
            chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(fragment -> {
                                fullResponse.append(fragment);
                                sink.next(fragment);
                            }
                    )
                    .doOnComplete(() -> {
                                String completeRes = fullResponse.toString();
                                //将ai返回的内容保存到数据库
                                consultationMessageService.saveAiMessage(dbSessionId, completeRes, AI_MODEL);
                                //添加ai回复到chatMemory
                                List<Message> aiMessages = new ArrayList<>();
                                aiMessages.add(new AssistantMessage(completeRes));
                                chatMemory.add(conversationId,aiMessages);

                                sink.complete();
                            }
                    )
                    .doOnError(error -> {
                                sink.error(error);
                            }
                    )
                    .subscribe();//订阅并启动流


        });
    }


    //获取参数中的sessionId
    private Long extractSessionId(String sessionId) {
        if (sessionId.startsWith(SESSION_ID_PREFIX)) {
            return Long.parseLong(sessionId.substring(SESSION_ID_PREFIX.length()));
        }

        return null;
    }
}
