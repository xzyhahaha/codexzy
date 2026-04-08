package com.codexzy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusinessRecordFormDTO {

    @NotBlank(message = "报单码不能为空")
    @Size(max = 32, message = "报单码不能超过 32 个字符")
    private String targetReportCode;

    private Boolean bindTarget;

    @NotBlank(message = "记录类型不能为空")
    @Pattern(regexp = "REPORT|INBOUND", message = "记录类型不合法")
    private String recordType;

    @NotBlank(message = "记录状态不能为空")
    @Pattern(regexp = "UNINVENTORIED|INVENTORIED_PENDING_SETTLEMENT|SETTLED", message = "记录状态不合法")
    private String recordStatus;

    @NotNull(message = "发生时间不能为空")
    @PastOrPresent(message = "发生时间不能晚于当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime occurredAt;

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称不能超过 200 个字符")
    private String productName;

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于 0")
    private Integer quantity;

    @NotNull(message = "成本不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "成本不能小于 0")
    @Digits(integer = 12, fraction = 2, message = "成本格式不正确")
    private BigDecimal costAmount;

    @NotNull(message = "固返不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "固返不能小于 0")
    @Digits(integer = 12, fraction = 2, message = "固返格式不正确")
    private BigDecimal fixedReturnAmount;

    @NotNull(message = "利润不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "利润不能小于 0")
    @Digits(integer = 12, fraction = 2, message = "利润格式不正确")
    private BigDecimal profitAmount;

    @NotNull(message = "卖出金额不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "卖出金额不能小于 0")
    @Digits(integer = 12, fraction = 2, message = "卖出金额格式不正确")
    private BigDecimal soldAmount;

    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;
}
