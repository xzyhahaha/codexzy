package com.codexzy.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusinessReporterSummaryDTO {

    private Long reporterUserId;

    private String reporterDisplayName;

    private String reporterNote;

    private long recordCount;

    private long reportCount;

    private long inboundCount;

    private long totalQuantity;

    private BigDecimal totalCostAmount = new BigDecimal("0.00");

    private BigDecimal totalFixedReturnAmount = new BigDecimal("0.00");

    private BigDecimal totalProfitAmount = new BigDecimal("0.00");

    private BigDecimal totalSoldAmount = new BigDecimal("0.00");

    private LocalDateTime firstOccurredAt;

    private LocalDateTime lastOccurredAt;

    private boolean selfReporter;
}
