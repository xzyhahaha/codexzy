package com.codexzy.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarDayDTO {

    private int dayOfMonth;

    private boolean currentMonth;

    private boolean checkedIn;

    private boolean today;

    private LocalDate date;

    private int recordCount;

    private String primaryTitle;
}