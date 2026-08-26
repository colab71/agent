package com.example.aispringboot.util;

import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.enumClass.UserStatus;
import com.example.aispringboot.service.UserService;
import jakarta.annotation.Resource;
import org.apache.coyote.Response;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.example.aispringboot.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Resource
    private UserService userService;

    private static final String GRANT_PROFIX = "ROLE_";

    private static final String TOKEN_AFTER_VALIDATION = "jwtToken";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return SecurityConfig.isPublicPath(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        System.out.println(requestUrl);
        System.out.println(method);

        //获取token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            //验证token并提取用户信息
            //验证token
            JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
            if (validationResult != null && validationResult.isValid()) {
                //token查询用户信息
                UserLoginResponseDTO.UserDetailResponseDTO userInfo = userService.getUserById(validationResult.getUserId());
                //判断用户是否存在
                if (userInfo == null) {
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.USER_NOT_EXIST);
                    return;
                }
                System.out.println(userInfo);
                //判断用户状态是否正常
                if (UserStatus.DISABLED.getCode().equals(userInfo.getStatus())) {
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }

                //创建Spring Security认证对象
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(GRANT_PROFIX + validationResult.getRoleType())
                );

                //创建UsernamePasswordAuthenticationToken对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        validationResult.getUsername(),
                        null,
                        authorities
                );

                //设置Spring Security上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                //将token存储到请求属性中
                request.setAttribute(TOKEN_AFTER_VALIDATION, token);
            } else {
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }

        } else {
            //清理上下文
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    //清理Spring Security上下文
    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
