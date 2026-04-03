package com.codexzy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoCategoryFormDTO {

    @NotBlank(message = "请输入分类名称")
    @Size(max = 40, message = "分类名称不能超过 40 个字符")
    private String categoryName;
}