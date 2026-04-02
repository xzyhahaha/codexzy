package com.codexzy.dto;

import lombok.Data;

@Data
public class CheckInStatDTO {

    private long totalCheckInDays;

    private long currentStreakDays;

    private long currentMonthCount;
}
