package com.codexzy.service;

import com.codexzy.dto.BusinessReportTargetOptionDTO;

import java.util.List;

public interface BusinessReportTargetService {

    List<BusinessReportTargetOptionDTO> listOptions(Long userId);

    void bindTarget(Long userId, Long targetUserId);

    void touchTarget(Long userId, Long targetUserId);
}
