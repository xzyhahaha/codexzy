package com.codexzy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckInFormDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过 200 个字符")
    private String title;

    @Size(max = 5000, message = "内容不能超过 5000 个字符")
    private String content;
}
