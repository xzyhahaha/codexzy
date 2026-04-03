package com.codexzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("business_record")
public class BusinessRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerUserId;

    private Long reporterUserId;

    private String recordType;

    private String recordStatus;

    private LocalDateTime occurredAt;

    private String productName;

    private Integer quantity;

    private BigDecimal costAmount;

    private BigDecimal fixedReturnAmount;

    private BigDecimal profitAmount;

    private BigDecimal soldAmount;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
