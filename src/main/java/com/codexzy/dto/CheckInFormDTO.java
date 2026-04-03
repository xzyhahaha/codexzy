package com.codexzy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CheckInFormDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过 200 个字符")
    private String title;

    @Size(max = 5000, message = "内容不能超过 5000 个字符")
    private String content;

    @NotNull(message = "打卡日期不能为空")
    @PastOrPresent(message = "打卡日期不能晚于今天")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
}