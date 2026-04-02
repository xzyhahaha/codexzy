package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("memo_file")
public class MemoFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long categoryId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private String remark;

    private LocalDateTime createTime;
}
