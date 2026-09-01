package com.example.aispringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.enumClass.UserType;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.mapper.UserMapper;
import com.example.aispringboot.service.UserService;
import com.example.aispringboot.service.convert.UserConvert;
import com.example.aispringboot.util.BlacklistUtil;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private StringRedisTemplate template;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,commandDTO.getUsername())
                .or()
                .eq(User::getEmail,commandDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);
//        System.out.println(user);
        if(user == null){
            throw new BusinessException("用户不存在");
        }
        //密码验证
        //去除用户写的空格
        String inputPassword = commandDTO.getPassword().trim();
        if(!passwordEncoder.matches(inputPassword,user.getPassword())){
            throw new BusinessException("密码错误");
        }
        //检查用户状态
        if(!user.isActive()){
            throw new BusinessException("用户已被禁用，请联系管理员");
        }

        //生成jwttoken
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
        UserLoginResponseDTO result = UserConvert.entityToLoginResponse(token,userInfo);

        return result;
    }

    @Override
    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO commandDTO) {
        //检查两次输入密码是否一致
        if(!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())){
            throw new BusinessException("两次输入密码不一致");
        }
        //判断用户名是否存在
        LambdaQueryWrapper<User> userNameQuery = new LambdaQueryWrapper<>();
        userNameQuery.eq(User::getUsername,commandDTO.getUsername());

        if(userMapper.selectCount(userNameQuery) > 0){
            throw new BusinessException("用户名已存在");
        }

        //判断邮箱是否存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail,commandDTO.getEmail());

        if(userMapper.selectCount(emailQuery) > 0){
            throw new BusinessException("邮箱已存在");
        }

        //判断用户类型是否有效
        if(!UserType.isValidCode(commandDTO.getUserType())){
            throw new BusinessException("用户类型无效");
        }

        //创建用户
        String encodedPassword = passwordEncoder.encode(commandDTO.getPassword());
        User user = UserConvert.registerCommandToEntity(commandDTO,encodedPassword);
        userMapper.insert(user);
        //返回用户信息
        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
        return userInfo;
    }

    @Override
    public UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId) {
        if(userId.compareTo(0L) != 1){
            return null;
        }
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }

    @Override
    public void logout() {
        //清理上下文
        SecurityContextHolder.clearContext();
        //获取请求头中的token
        String token = jwtTokenUtil.getCurrentToken();
        //数据在redis存储时间
        Long time = jwtTokenUtil.getVerificationTime(token) - System.currentTimeMillis();
        //将token添加到黑名单
        template.opsForValue().set(BlacklistUtil.REDIS_BLACKLIST_KEY + token, System.currentTimeMillis() + "",time, TimeUnit.MILLISECONDS);
    }

}
