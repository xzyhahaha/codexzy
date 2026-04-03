package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_account")
public class BusinessAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String reportCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
