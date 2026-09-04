package com.example.aispringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aispringboot.DTO.middle.AuthorityUserDTO;
import com.example.aispringboot.DTO.response.ConsultationSessionPageResponseDTO;
import com.example.aispringboot.entity.ConsultationSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ConsultationSessionMapper extends BaseMapper<ConsultationSession> {
    @Select("""
        SELECT
            cs.id,
            cs.user_id AS userId,
            COALESCE(u.nickname, u.username) AS userNickname,
            cs.session_title AS sessionTitle,
            cs.started_at AS startedAt,
            TIMESTAMPDIFF(MINUTE, cs.started_at, NOW()) AS durationMinutes,

            (
                SELECT COUNT(*)
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
            ) AS messageCount,

            (
                SELECT cm.content
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
                ORDER BY cm.id DESC
                LIMIT 1
            ) AS lastMessageContent,

            (
                SELECT cm.created_at
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
                ORDER BY cm.id DESC
                LIMIT 1
            ) AS lastMessageTime

        FROM consultation_session cs
        LEFT JOIN user u ON cs.user_id = u.id
        ORDER BY cs.id DESC
        """)
    Page<ConsultationSessionPageResponseDTO> selectSessionPage(Page<ConsultationSessionPageResponseDTO> page);

    @Select("""
        SELECT
            cs.id,
            cs.user_id AS userId,
            COALESCE(u.nickname, u.username) AS userNickname,
            cs.session_title AS sessionTitle,
            cs.started_at AS startedAt,
            TIMESTAMPDIFF(MINUTE, cs.started_at, NOW()) AS durationMinutes,

            (
                SELECT COUNT(*)
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
            ) AS messageCount,

            (
                SELECT cm.content
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
                ORDER BY cm.id DESC
                LIMIT 1
            ) AS lastMessageContent,

            (
                SELECT cm.created_at
                FROM consultation_message cm
                WHERE cm.session_id = cs.id
                ORDER BY cm.id DESC
                LIMIT 1
            ) AS lastMessageTime

        FROM consultation_session cs
        LEFT JOIN user u ON cs.user_id = u.id
        WHERE cs.user_id = #{userId}
        ORDER BY cs.id DESC
        """)
    Page<ConsultationSessionPageResponseDTO> selectUserSessionPage(Page<ConsultationSessionPageResponseDTO> page, Long userId);

    @Select("""
        SELECT
            u.id AS userId,
            u.user_type AS userType
        FROM consultation_session cs
        LEFT JOIN user u ON cs.user_id = u.id
        WHERE cs.id = #{sessionId}
        """)
    AuthorityUserDTO getUserTypeBySessionId(Long sessionId);
}
