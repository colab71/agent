package com.example.aispringboot.DTO.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterCommandDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3 ,max = 50 ,message = "用户名长度必须在3到50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",message = "用户名只能包含字母、数字和下划线")
    private String username;
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @Size(max = 50 ,message = "昵称长度必须在50以下")
    private String nickname;

    @Pattern(regexp = "^1[3456789]\\d{9}$",message = "手机号格式错误")
    private String phone;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 50,message = "密码长度必须在6到50之间")
    private String password;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    private Integer gender;
    private Integer userType = 1;
    private LocalDate birthday;
}
