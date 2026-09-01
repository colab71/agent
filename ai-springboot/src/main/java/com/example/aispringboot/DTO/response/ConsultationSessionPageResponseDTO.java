package com.example.aispringboot.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationSessionPageResponseDTO {
    //会话id
    private Long id;

    private Long userId;

    private String userNickname;

    private String sessionTitle;

    private LocalDateTime startedAt;

    //距离上次对话时间，单位分钟数
    private Long durationMinutes;

    //会话内消息数量
    private Integer messageCount;

    private String lastMessageContent;

    private LocalDateTime lastMessageTime;
}
