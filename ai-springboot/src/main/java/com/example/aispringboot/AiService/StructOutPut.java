package com.example.aispringboot.AiService;

public class StructOutPut {
    public record StreamChat(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            Integer messageCount,
            String status
    ){}
}
