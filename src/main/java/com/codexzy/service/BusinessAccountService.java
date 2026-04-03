package com.codexzy.service;

import com.codexzy.entity.BusinessAccount;

public interface BusinessAccountService {

    BusinessAccount getOrCreateByUserId(Long userId);

    BusinessAccount getByUserId(Long userId);

    BusinessAccount getByReportCode(String reportCode);
}
