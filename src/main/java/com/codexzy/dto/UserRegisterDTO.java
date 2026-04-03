package com.codexzy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "请输入用户名")
    @Size(min = 4, max = 32, message = "用户名长度需在 4 到 32 位之间")
    private String username;

    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 64, message = "密码长度至少 6 位")
    private String password;

    @NotBlank(message = "请输入邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
}