package com.codexzy.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckInDTO {

    private Long id;

    private String title;

    private String content;

    private LocalDate checkDate;

    private Integer attachmentCount;
}
