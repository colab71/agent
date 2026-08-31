package com.example.aispringboot.AiService;

import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import reactor.core.publisher.Flux;

public interface PsychologicalSupportService {
    public StructOutPut.StreamChat startSession(Long userId, ConsultationSessionCreateDTO createDTO);

    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage);
}
