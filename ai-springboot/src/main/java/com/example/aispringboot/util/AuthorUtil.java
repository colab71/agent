package com.example.aispringboot.util;

import com.example.aispringboot.DTO.middle.AuthorityUserDTO;
import com.example.aispringboot.enumClass.UserType;
import com.example.aispringboot.mapper.ConsultationSessionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class AuthorUtil {
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private ConsultationSessionMapper consultationSessionMapper;

    public boolean haveSelectMessageAuthority(Long sessionId){
        JwtTokenUtil.TokenVerificationResult tokenVerificationResult = jwtTokenUtil.validateToken(jwtTokenUtil.getCurrentToken());
        AuthorityUserDTO userDTO = consultationSessionMapper.getUserTypeBySessionId(sessionId);
        if(UserType.USER.getCode().intValue() == tokenVerificationResult.getRoleType().intValue()){
            //普通用户，判断是否是会话创建者
            if(tokenVerificationResult.getUserId().equals(userDTO.getUserId())){
                return true;
            }
            return false;
        }
        //管理角色，返回true
        return true;
    }

    public boolean isAdmin(){
        JwtTokenUtil.TokenVerificationResult tokenVerificationResult = jwtTokenUtil.validateToken(jwtTokenUtil.getCurrentToken());
        return tokenVerificationResult.getRoleType().intValue() == UserType.ADMIN.getCode().intValue();
    }

    public Long getUserId() {
        JwtTokenUtil.TokenVerificationResult tokenVerificationResult = jwtTokenUtil.validateToken(jwtTokenUtil.getCurrentToken());
        return tokenVerificationResult.getUserId();
    }

    public boolean isSessionOwner(Long sessionId){
        JwtTokenUtil.TokenVerificationResult tokenVerificationResult = jwtTokenUtil.validateToken(jwtTokenUtil.getCurrentToken());
        AuthorityUserDTO userDTO = consultationSessionMapper.getUserTypeBySessionId(sessionId);
        return tokenVerificationResult.getUserId().equals(userDTO.getUserId());
    }
}
