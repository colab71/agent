package com.example.aispringboot.service;

import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.entity.ConsultationSession;

public interface ConsultationSessionService {
    ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO);
}
