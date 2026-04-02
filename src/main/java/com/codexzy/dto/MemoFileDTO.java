package com.codexzy.dto;

import lombok.Data;

@Data
public class MemoFileDTO {

    private Long id;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String remark;

    private String filePath;
}
