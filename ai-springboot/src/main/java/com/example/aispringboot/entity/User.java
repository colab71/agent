package com.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.aispringboot.enumClass.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.aispringboot.enumClass.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
@Builder
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 50,message = "用户名长度必须在3到50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",message = "用户名只能包含字母、数字和下划线")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码长度必须在6到20之间")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    @Size(max = 100,message = "邮箱长度不能超过100")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}$",message = "手机号格式不正确")
    private String phone;
    @Size(max = 100,message = "昵称长度不能超过100")
    private String nickname;
    @Size(max = 500,message = "头像路径长度不能超过500")
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    @TableField(value = "user_type")
    private Integer userType;
    private Integer status;
    @TableField(value = "created_at")
    private LocalDateTime createdAt;
    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * 是否为普通用户
     */
    public boolean isUser() {
        return UserType.USER.getCode().equals(this.userType);
    }

    /**
     * 是否为正常状态
     */
    public boolean isActive() {
        return UserStatus.NORMAL.getCode().equals(this.status);
    }

    /**
     * 是否被禁用
     */
    public boolean isDisabled() {
        return UserStatus.DISABLED.getCode().equals(this.status);
    }

    /**
     * 获取显示名称（优先显示昵称，否则显示用户名）
     */
    public String getDisplayName() {
        return nickname != null && !nickname.trim().isEmpty() ? nickname : username;
    }

    /**
     * 获取用户类型显示名称
     */
    public String getUserTypeDisplayName() {
        try {
            return UserType.fromCode(userType).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }

    /**
     * 获取用户状态显示名称
     */
    public String getStatusDisplayName() {
        try {
            return UserStatus.fromCode(status).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }
}
