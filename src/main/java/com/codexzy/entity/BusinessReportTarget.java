package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_report_target")
public class BusinessReportTarget {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long targetUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
