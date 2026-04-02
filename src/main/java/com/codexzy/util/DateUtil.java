package com.codexzy.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    public static String format(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }
}
