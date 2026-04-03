package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_reporter_note")
public class BusinessReporterNote {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerUserId;

    private Long reporterUserId;

    private String noteName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
