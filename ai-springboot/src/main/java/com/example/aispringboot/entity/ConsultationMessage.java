package com.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("consultation_message")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultationMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "会话ID不能为空")
    @TableField("session_id")
    private Long sessionId;

    //发送者类型：1 用户，2 ai助手
    @NotNull(message = "发送者类型不能为空")
    @TableField("sender_type")
    private Byte senderType;

    //消息类型：1 文本
    @NotNull(message = "消息类型不能为空")
    @TableField("message_type")
    private Byte messageType;

    @NotBlank(message = "消息内容不能为空")
    @TableField("content")
    private String content;

    @Size(max = 50, message = "情绪标签长度不能超过50个字符")
    @TableField("emotion_tag")
    private String emotionTag;
    @TableField("ai_model")

    @Size(max = 50, message = "AI模型长度不能超过50个字符")
    private String aiModel;


    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 获取发送者类型描述
     */
    public String getSenderTypeDesc() {
        if (senderType == null) {
            return "未知";
        }
        switch (senderType) {
            case 1:
                return "用户";
            case 2:
                return "AI助手";
            default:
                return "未知";
        }
    }

    /**
     * 获取消息类型描述
     */
    public String getMessageTypeDesc() {
        if (messageType == null) {
            return "未知";
        }
        switch (messageType) {
            case 1:
                return "文本";
            default:
                return "未知";
        }
    }
}
