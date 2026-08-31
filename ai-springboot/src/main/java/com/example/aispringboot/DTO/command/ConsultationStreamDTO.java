package com.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationStreamDTO {
    @NotNull(message = "会话ID不能为空")
    private String sessionId;
    @NotNull(message = "初始消息不能为空")
    @Size(max = 2000, message = "初始消息长度不能超过2000个字符")
    private String userMessage;
}
