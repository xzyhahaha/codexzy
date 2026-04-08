package com.codexzy.dto;

import lombok.Data;

@Data
public class BusinessReportTargetOptionDTO {

    private Long targetUserId;

    private String targetDisplayName;

    private String targetReportCode;
}
