package com.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginCommandDTO {
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max = 100,message = "用户名或邮箱长度不能超过100个字符")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(max = 50,min = 6,message = "密码长度必须在6到50个字符之间")
    private String password;
}
