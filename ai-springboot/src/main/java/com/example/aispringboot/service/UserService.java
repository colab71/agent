package com.example.aispringboot.service;

import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import jakarta.validation.Valid;

public interface UserService {
    UserLoginResponseDTO login(@Valid UserLoginCommandDTO commandDTO);

    UserLoginResponseDTO.UserDetailResponseDTO register(@Valid UserRegisterCommandDTO commandDTO);

    UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId);

    void logout();
}
