package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.mapper.BusinessAccountMapper;
import com.codexzy.service.BusinessAccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class BusinessAccountServiceImpl implements BusinessAccountService {

    private static final String REPORT_CODE_PREFIX = "CZ";

    @Resource
    private BusinessAccountMapper businessAccountMapper;

    @Override
    @Transactional
    public BusinessAccount getOrCreateByUserId(Long userId) {
        BusinessAccount account = getByUserId(userId);
        if (account != null) {
            return account;
        }

        BusinessAccount created = new BusinessAccount();
        created.setUserId(userId);
        created.setReportCode(generateUniqueReportCode());
        businessAccountMapper.insert(created);
        return created;
    }

    @Override
    public BusinessAccount getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return businessAccountMapper.selectOne(new LambdaQueryWrapper<BusinessAccount>()
                .eq(BusinessAccount::getUserId, userId)
                .last("LIMIT 1"));
    }

    @Override
    public BusinessAccount getByReportCode(String reportCode) {
        if (!StringUtils.hasText(reportCode)) {
            return null;
        }
        return businessAccountMapper.selectOne(new LambdaQueryWrapper<BusinessAccount>()
                .eq(BusinessAccount::getReportCode, reportCode.trim())
                .last("LIMIT 1"));
    }

    private String generateUniqueReportCode() {
        for (int i = 0; i < 30; i++) {
            String candidate = REPORT_CODE_PREFIX + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
            if (getByReportCode(candidate) == null) {
                return candidate;
            }
        }
        throw new IllegalStateException("生成报单码失败，请重试");
    }
}
