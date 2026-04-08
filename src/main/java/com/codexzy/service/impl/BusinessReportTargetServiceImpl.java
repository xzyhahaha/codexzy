package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.dto.BusinessReportTargetOptionDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.BusinessReportTarget;
import com.codexzy.entity.User;
import com.codexzy.mapper.BusinessReportTargetMapper;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.BusinessReportTargetService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class BusinessReportTargetServiceImpl implements BusinessReportTargetService {

    @Resource
    private BusinessReportTargetMapper businessReportTargetMapper;

    @Resource
    private BusinessAccountService businessAccountService;

    @Resource
    private UserService userService;

    @Override
    public List<BusinessReportTargetOptionDTO> listOptions(Long userId) {
        List<BusinessReportTargetOptionDTO> options = new ArrayList<>();
        if (userId == null) {
            return options;
        }

        List<BusinessReportTarget> targets = businessReportTargetMapper.selectList(new LambdaQueryWrapper<BusinessReportTarget>()
                .eq(BusinessReportTarget::getUserId, userId)
                .orderByDesc(BusinessReportTarget::getUpdateTime)
                .orderByDesc(BusinessReportTarget::getId));
        for (BusinessReportTarget target : targets) {
            if (target.getTargetUserId() == null) {
                continue;
            }

            BusinessAccount account = businessAccountService.getByUserId(target.getTargetUserId());
            if (account == null || !StringUtils.hasText(account.getReportCode())) {
                continue;
            }

            User user = userService.getById(target.getTargetUserId());
            BusinessReportTargetOptionDTO option = new BusinessReportTargetOptionDTO();
            option.setTargetUserId(target.getTargetUserId());
            option.setTargetReportCode(account.getReportCode());
            option.setTargetDisplayName(resolveDisplayName(user, target.getTargetUserId()));
            options.add(option);
        }
        return options;
    }

    @Override
    @Transactional
    public void bindTarget(Long userId, Long targetUserId) {
        saveOrRefresh(userId, targetUserId, true);
    }

    @Override
    @Transactional
    public void touchTarget(Long userId, Long targetUserId) {
        saveOrRefresh(userId, targetUserId, false);
    }

    private void saveOrRefresh(Long userId, Long targetUserId, boolean createIfMissing) {
        if (userId == null || targetUserId == null || userId.equals(targetUserId)) {
            return;
        }

        BusinessReportTarget existing = businessReportTargetMapper.selectOne(new LambdaQueryWrapper<BusinessReportTarget>()
                .eq(BusinessReportTarget::getUserId, userId)
                .eq(BusinessReportTarget::getTargetUserId, targetUserId)
                .last("LIMIT 1"));
        if (existing == null) {
            if (!createIfMissing) {
                return;
            }
            BusinessReportTarget target = new BusinessReportTarget();
            target.setUserId(userId);
            target.setTargetUserId(targetUserId);
            target.setUpdateTime(LocalDateTime.now());
            try {
                businessReportTargetMapper.insert(target);
                return;
            } catch (DuplicateKeyException ignored) {
                existing = businessReportTargetMapper.selectOne(new LambdaQueryWrapper<BusinessReportTarget>()
                        .eq(BusinessReportTarget::getUserId, userId)
                        .eq(BusinessReportTarget::getTargetUserId, targetUserId)
                        .last("LIMIT 1"));
                if (existing == null) {
                    return;
                }
            }
        }

        existing.setUpdateTime(LocalDateTime.now());
        businessReportTargetMapper.updateById(existing);
    }

    private String resolveDisplayName(User user, Long userId) {
        if (user == null) {
            return "用户#" + userId;
        }
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }
}
