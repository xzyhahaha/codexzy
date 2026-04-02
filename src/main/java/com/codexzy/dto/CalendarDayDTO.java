package com.codexzy.dto;

import lombok.Data;

@Data
public class CalendarDayDTO {

    private int dayOfMonth;

    private boolean currentMonth;

    private boolean checkedIn;

    private boolean today;
}
