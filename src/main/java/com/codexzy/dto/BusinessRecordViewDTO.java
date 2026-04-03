package com.codexzy.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusinessRecordViewDTO {

    private Long id;

    private String recordType;

    private String recordTypeLabel;

    private String recordStatus;

    private String recordStatusLabel;

    private Long ownerUserId;

    private Long reporterUserId;

    private String ownerDisplayName;

    private String reporterDisplayName;

    private LocalDateTime occurredAt;

    private String productName;

    private Integer quantity;

    private BigDecimal costAmount;

    private BigDecimal fixedReturnAmount;

    private BigDecimal profitAmount;

    private BigDecimal soldAmount;

    private String remark;

    private boolean selfReporter;
}
