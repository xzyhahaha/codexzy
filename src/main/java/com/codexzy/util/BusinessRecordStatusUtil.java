package com.codexzy.util;

public final class BusinessRecordStatusUtil {

    public static final String UNINVENTORIED = "UNINVENTORIED";
    public static final String INVENTORIED_PENDING_SETTLEMENT = "INVENTORIED_PENDING_SETTLEMENT";
    public static final String SETTLED = "SETTLED";

    private BusinessRecordStatusUtil() {
    }

    public static String defaultStatus(String recordType) {
        if ("INBOUND".equalsIgnoreCase(recordType)) {
            return INVENTORIED_PENDING_SETTLEMENT;
        }
        return UNINVENTORIED;
    }

    public static String normalizeStatus(String status, String recordType) {
        if (UNINVENTORIED.equals(status)
                || INVENTORIED_PENDING_SETTLEMENT.equals(status)
                || SETTLED.equals(status)) {
            return status;
        }
        return defaultStatus(recordType);
    }

    public static String label(String status) {
        if (UNINVENTORIED.equals(status)) {
            return "未入库";
        }
        if (INVENTORIED_PENDING_SETTLEMENT.equals(status)) {
            return "已入库待结算";
        }
        if (SETTLED.equals(status)) {
            return "已结算";
        }
        return "未入库";
    }
}
