package com.codexzy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemoUploadDTO {

    @NotNull(message = "请选择分类")
    private Long categoryId;

    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;

    private MultipartFile file;
}
