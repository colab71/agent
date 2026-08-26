package com.example.aispringboot.service.convert;

import com.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.enumClass.UserStatus;

import java.time.LocalDateTime;

public class UserConvert {
    public static UserLoginResponseDTO.UserDetailResponseDTO entityToDetailResponse(User user){
        return UserLoginResponseDTO.UserDetailResponseDTO.builder()
                .id(user.getId().intValue())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(getGenderDisplayName(user.getGender()))
                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserLoginResponseDTO entityToLoginResponse(String token, UserLoginResponseDTO.UserDetailResponseDTO userInfo){
        return UserLoginResponseDTO.builder()
                .token(token)
                .roleType(userInfo.getUserTypeDisplayName())
                .userInfo(userInfo)
                .build();
    }

    public static User registerCommandToEntity(UserRegisterCommandDTO commandDTO,String encodingPassword){
        return User.builder()
                .username(commandDTO.getUsername())
                .email(commandDTO.getEmail())
                .nickname(commandDTO.getNickname())
                .phone(commandDTO.getPhone())
                .password(encodingPassword)
                .gender(commandDTO.getGender())
                .userType(commandDTO.getUserType())
                .birthday(commandDTO.getBirthday())
                .status(UserStatus.NORMAL.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }



    /**
     * 获取性别显示名称
     * @param gender 性别代码
     * @return 性别显示名称
     */
    private static String getGenderDisplayName(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }
}
