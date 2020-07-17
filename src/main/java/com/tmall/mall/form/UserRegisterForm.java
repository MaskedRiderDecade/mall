package com.tmall.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
    @NotBlank //用于String，判断空格
//    @NotEmpty 用于集合是否为空
//    @NotNull 是否为null
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;
}
