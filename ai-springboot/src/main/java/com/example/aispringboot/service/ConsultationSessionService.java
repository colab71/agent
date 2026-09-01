package com.example.aispringboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationSessionPageResponseDTO;
import com.example.aispringboot.entity.ConsultationSession;

public interface ConsultationSessionService {
    ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO);

    Page<ConsultationSessionPageResponseDTO> getSessionByPage(Long pageNum, Long pageSize);
}
