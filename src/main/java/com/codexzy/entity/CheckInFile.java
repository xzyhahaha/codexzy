package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("check_in_file")
public class CheckInFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long checkInId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private LocalDateTime createTime;
}
