package com.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.config.JwtConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtil implements ApplicationContextAware {

    private static final String ISSUER = "mental-health-assistant";

    private static final String TOKEN_HEADER_NAME = "token";

    private static final String TOKEN_AFTER_VALIDATION = "jwtToken";

    private static ApplicationContext applicationContext;

    @Resource
    private StringRedisTemplate template;

    //用户静态工具类中获取Spring容器管理的对象
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }

    private JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    //生成token方法
    public String generateToken(Long userId, String username, Integer roleType) {
        try {
            //生成jwt配置
            JwtConfig jwtConfig = getJwtConfig();
            //生成签名的算法，获取盐
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            //生成过期时间
            Date expireTime = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

            //jwt荷载设置
            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expireTime) //设置过期时间
                    .withIssuedAt(new Date()) //设置签发时间
                    .withIssuer(ISSUER)
                    .sign(Algorithm.HMAC256(jwtConfig.getSecret()));

            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成token异常" + e);
        }
    }

    //获取token
    public String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String token = request.getHeader(TOKEN_HEADER_NAME);
        if (StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }

    //获取已经认证过的token
    public String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(TOKEN_AFTER_VALIDATION);
            if (token != null) {
                return token;
            }

            //备用方案：从请求头中直接获取token
            String headerToken = extractTokenFromRequest(request);
            return headerToken;
        }
        return null;
    }

    //验证token并提取用户信息
    public TokenVerificationResult validateToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        //判断token是否在黑名单中
        if (template.hasKey(BlacklistUtil.REDIS_BLACKLIST_KEY + token)) {
            throw new JWTVerificationException(ResultCode.TOKEN_BLOCKED.getMessage());
        }
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();
        //roleType有可能是string类型，有可能是Integer类型(多余)
        Integer roleType = null;
        try {
            roleType = jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if (StringUtils.hasText(roleTypeStr)) {
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        if (userId != null && username != null && roleType != null) {
            return new TokenVerificationResult(userId, username, roleType, true);
        }
        return null;
    }

    //验证token有效性
    public DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("token不能为空");
        }
        //token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    //获取token有效时间
    public Long getVerificationTime(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getExpiresAt().getTime();
    }


    //token验证结果封装类
    @Getter
    @AllArgsConstructor
    public static class TokenVerificationResult {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;
    }


}
