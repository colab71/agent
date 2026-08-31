package com.example.aispringboot.service;

import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispringboot.entity.ConsultationMessage;
import com.example.aispringboot.entity.ConsultationSession;

public interface ConsultationMessageService {

    ConsultationMessage saveUserMessage(Long sessionId, String content, String emotionTag);

    ConsultationMessage saveAiMessage(Long sessionId, String content, String aiModel);

    public Integer getMessageCount(Long sessionId);

    public ConsultationMessageResponseDTO getLastMessage(Long sessionId);
}
