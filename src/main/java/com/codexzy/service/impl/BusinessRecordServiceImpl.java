package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.dto.BusinessRecordFormDTO;
import com.codexzy.dto.BusinessRecordSummaryDTO;
import com.codexzy.dto.BusinessRecordViewDTO;
import com.codexzy.dto.BusinessReporterSummaryDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.BusinessRecord;
import com.codexzy.entity.User;
import com.codexzy.mapper.BusinessRecordMapper;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.BusinessRecordService;
import com.codexzy.service.BusinessReporterNoteService;
import com.codexzy.service.UserService;
import com.codexzy.util.BusinessRecordStatusUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BusinessRecordServiceImpl implements BusinessRecordService {

    private static final String TYPE_REPORT = "REPORT";
    private static final String TYPE_INBOUND = "INBOUND";
    private static final String LABEL_REPORT = "报单";
    private static final String LABEL_INBOUND = "入库";

    @Resource
    private BusinessRecordMapper businessRecordMapper;

    @Resource
    private BusinessAccountService businessAccountService;

    @Resource
    private UserService userService;

    @Resource
    private BusinessReporterNoteService businessReporterNoteService;

    @Override
    @Transactional
    public BusinessRecord createRecord(Long currentUserId, BusinessRecordFormDTO formDTO) {
        BusinessAccount currentAccount = businessAccountService.getOrCreateByUserId(currentUserId);
        String recordType = normalizeRecordType(formDTO.getRecordType());

        BusinessAccount targetAccount;
        if (TYPE_INBOUND.equals(recordType)) {
            targetAccount = currentAccount;
        } else {
            targetAccount = businessAccountService.getByReportCode(formDTO.getTargetReportCode());
            if (targetAccount == null) {
                throw new IllegalArgumentException("报单码不存在");
            }
        }

        BusinessRecord record = new BusinessRecord();
        record.setOwnerUserId(targetAccount.getUserId());
        record.setReporterUserId(currentUserId);
        record.setRecordType(recordType);
        // 报单单据的状态由上家后台管理，创建时不接受报单人自行指定
        record.setRecordStatus(TYPE_REPORT.equals(recordType)
                ? BusinessRecordStatusUtil.defaultStatus(recordType)
                : BusinessRecordStatusUtil.normalizeStatus(formDTO.getRecordStatus(), recordType));
        record.setOccurredAt(formDTO.getOccurredAt());
        record.setProductName(formDTO.getProductName().trim());
        record.setQuantity(formDTO.getQuantity());
        record.setCostAmount(normalizeMoney(formDTO.getCostAmount()));
        record.setFixedReturnAmount(normalizeMoney(formDTO.getFixedReturnAmount()));
        record.setProfitAmount(normalizeMoney(formDTO.getProfitAmount()));
        record.setSoldAmount(normalizeMoney(formDTO.getSoldAmount()));
        record.setRemark(normalizeRemark(formDTO.getRemark()));
        businessRecordMapper.insert(record);
        return record;
    }

    @Override
    public BusinessRecord getById(Long ownerUserId, Long recordId) {
        BusinessRecord record = businessRecordMapper.selectById(recordId);
        if (record == null || !ownerUserId.equals(record.getOwnerUserId())) {
            throw new IllegalArgumentException("经营记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void updateRecord(Long ownerUserId, Long recordId, BusinessRecordFormDTO formDTO) {
        BusinessRecord record = getById(ownerUserId, recordId);
        record.setRecordStatus(BusinessRecordStatusUtil.normalizeStatus(formDTO.getRecordStatus(), record.getRecordType()));
        record.setOccurredAt(formDTO.getOccurredAt());
        record.setProductName(formDTO.getProductName().trim());
        record.setQuantity(formDTO.getQuantity());
        record.setCostAmount(normalizeMoney(formDTO.getCostAmount()));
        record.setFixedReturnAmount(normalizeMoney(formDTO.getFixedReturnAmount()));
        record.setProfitAmount(normalizeMoney(formDTO.getProfitAmount()));
        record.setSoldAmount(normalizeMoney(formDTO.getSoldAmount()));
        record.setRemark(normalizeRemark(formDTO.getRemark()));
        businessRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long ownerUserId, Long recordId) {
        BusinessRecord record = getById(ownerUserId, recordId);
        businessRecordMapper.deleteById(record.getId());
    }

    @Override
    public BusinessRecordSummaryDTO getSummary(Long ownerUserId, LocalDate startDate, LocalDate endDate, String scope) {
        List<BusinessRecord> records = selectRecords(ownerUserId, startDate, endDate, scope, null);
        return buildSummary(records);
    }

    @Override
    public List<BusinessRecordViewDTO> listRecords(Long ownerUserId, LocalDate startDate, LocalDate endDate, String scope) {
        List<BusinessRecord> records = selectRecords(ownerUserId, startDate, endDate, scope, null);
        return buildRecordViews(records, ownerUserId);
    }

    @Override
    public List<BusinessReporterSummaryDTO> listReporterSummaries(Long ownerUserId,
                                                                  LocalDate startDate,
                                                                  LocalDate endDate,
                                                                  String scope) {
        List<BusinessRecord> records = selectRecords(ownerUserId, startDate, endDate, scope, null);
        Set<Long> reporterIds = new LinkedHashSet<>();
        Map<Long, BusinessReporterSummaryDTO> summaryMap = new LinkedHashMap<>();
        for (BusinessRecord record : records) {
            reporterIds.add(record.getReporterUserId());
            BusinessReporterSummaryDTO summary = summaryMap.computeIfAbsent(record.getReporterUserId(), reporterId -> {
                BusinessReporterSummaryDTO dto = new BusinessReporterSummaryDTO();
                dto.setReporterUserId(reporterId);
                return dto;
            });
            accumulateReporterSummary(summary, record);
        }

        reporterIds.add(ownerUserId);
        Map<Long, String> reporterNoteMap = businessReporterNoteService.getNoteNameMap(ownerUserId, reporterIds);
        Map<Long, User> userMap = loadUsers(reporterIds);
        for (BusinessReporterSummaryDTO summary : summaryMap.values()) {
            User reporter = userMap.get(summary.getReporterUserId());
            String reporterNote = reporterNoteMap.get(summary.getReporterUserId());
            summary.setReporterNote(reporterNote);
            summary.setReporterDisplayName(resolveDisplayName(reporter, reporterNote, summary.getReporterUserId()));
            summary.setSelfReporter(ownerUserId.equals(summary.getReporterUserId()));
        }
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public BusinessRecordSummaryDTO getReporterSummary(Long ownerUserId,
                                                       Long reporterUserId,
                                                       LocalDate startDate,
                                                       LocalDate endDate,
                                                       String scope) {
        List<BusinessRecord> records = selectRecords(ownerUserId, startDate, endDate, scope, reporterUserId);
        return buildSummary(records);
    }

    @Override
    public List<BusinessRecordViewDTO> listRecordsByReporter(Long ownerUserId,
                                                             Long reporterUserId,
                                                             LocalDate startDate,
                                                             LocalDate endDate,
                                                             String scope) {
        List<BusinessRecord> records = selectRecords(ownerUserId, startDate, endDate, scope, reporterUserId);
        return buildRecordViews(records, ownerUserId);
    }

    private List<BusinessRecord> selectRecords(Long ownerUserId,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               String scope,
                                               Long reporterUserId) {
        LambdaQueryWrapper<BusinessRecord> query = new LambdaQueryWrapper<BusinessRecord>()
                .eq(BusinessRecord::getOwnerUserId, ownerUserId)
                .orderByDesc(BusinessRecord::getOccurredAt)
                .orderByDesc(BusinessRecord::getCreateTime);

        if (startDate != null) {
            query.ge(BusinessRecord::getOccurredAt, startDate.atStartOfDay());
        }
        if (endDate != null) {
            query.lt(BusinessRecord::getOccurredAt, endDate.plusDays(1).atStartOfDay());
        }
        if (StringUtils.hasText(scope) && !"ALL".equalsIgnoreCase(scope)) {
            query.eq(BusinessRecord::getRecordType, normalizeRecordType(scope));
        }
        if (reporterUserId != null) {
            query.eq(BusinessRecord::getReporterUserId, reporterUserId);
        }
        return businessRecordMapper.selectList(query);
    }

    private BusinessRecordSummaryDTO buildSummary(List<BusinessRecord> records) {
        BusinessRecordSummaryDTO summary = new BusinessRecordSummaryDTO();
        summary.setRecordCount(records.size());
        for (BusinessRecord record : records) {
            summary.setTotalQuantity(summary.getTotalQuantity() + valueOf(record.getQuantity()));
            summary.setTotalCostAmount(sum(summary.getTotalCostAmount(), record.getCostAmount()));
            summary.setTotalFixedReturnAmount(sum(summary.getTotalFixedReturnAmount(), record.getFixedReturnAmount()));
            summary.setTotalProfitAmount(sum(summary.getTotalProfitAmount(), record.getProfitAmount()));
            summary.setTotalSoldAmount(sum(summary.getTotalSoldAmount(), record.getSoldAmount()));
            if (TYPE_INBOUND.equals(record.getRecordType())) {
                summary.setInboundCount(summary.getInboundCount() + 1);
            } else {
                summary.setReportCount(summary.getReportCount() + 1);
            }
            if (summary.getFirstOccurredAt() == null || record.getOccurredAt().isBefore(summary.getFirstOccurredAt())) {
                summary.setFirstOccurredAt(record.getOccurredAt());
            }
            if (summary.getLastOccurredAt() == null || record.getOccurredAt().isAfter(summary.getLastOccurredAt())) {
                summary.setLastOccurredAt(record.getOccurredAt());
            }
        }
        return summary;
    }

    private void accumulateReporterSummary(BusinessReporterSummaryDTO summary, BusinessRecord record) {
        summary.setRecordCount(summary.getRecordCount() + 1);
        summary.setTotalQuantity(summary.getTotalQuantity() + valueOf(record.getQuantity()));
        summary.setTotalCostAmount(sum(summary.getTotalCostAmount(), record.getCostAmount()));
        summary.setTotalFixedReturnAmount(sum(summary.getTotalFixedReturnAmount(), record.getFixedReturnAmount()));
        summary.setTotalProfitAmount(sum(summary.getTotalProfitAmount(), record.getProfitAmount()));
        summary.setTotalSoldAmount(sum(summary.getTotalSoldAmount(), record.getSoldAmount()));
        if (TYPE_INBOUND.equals(record.getRecordType())) {
            summary.setInboundCount(summary.getInboundCount() + 1);
        } else {
            summary.setReportCount(summary.getReportCount() + 1);
        }
        if (summary.getFirstOccurredAt() == null || record.getOccurredAt().isBefore(summary.getFirstOccurredAt())) {
            summary.setFirstOccurredAt(record.getOccurredAt());
        }
        if (summary.getLastOccurredAt() == null || record.getOccurredAt().isAfter(summary.getLastOccurredAt())) {
            summary.setLastOccurredAt(record.getOccurredAt());
        }
    }

    private List<BusinessRecordViewDTO> buildRecordViews(List<BusinessRecord> records, Long ownerUserId) {
        Set<Long> userIds = new LinkedHashSet<>();
        Set<Long> reporterIds = new LinkedHashSet<>();
        userIds.add(ownerUserId);
        for (BusinessRecord record : records) {
            userIds.add(record.getReporterUserId());
            reporterIds.add(record.getReporterUserId());
        }
        Map<Long, String> reporterNoteMap = businessReporterNoteService.getNoteNameMap(ownerUserId, reporterIds);
        Map<Long, User> userMap = loadUsers(userIds);

        List<BusinessRecordViewDTO> views = new ArrayList<>();
        for (BusinessRecord record : records) {
            BusinessRecordViewDTO view = new BusinessRecordViewDTO();
            view.setId(record.getId());
            view.setRecordType(record.getRecordType());
            view.setRecordTypeLabel(resolveRecordTypeLabel(record.getRecordType()));
            view.setRecordStatus(record.getRecordStatus());
            view.setRecordStatusLabel(BusinessRecordStatusUtil.label(record.getRecordStatus()));
            view.setOwnerUserId(record.getOwnerUserId());
            view.setReporterUserId(record.getReporterUserId());
            view.setOwnerDisplayName(resolveDisplayName(userMap.get(record.getOwnerUserId()), null, record.getOwnerUserId()));
            view.setReporterDisplayName(resolveDisplayName(userMap.get(record.getReporterUserId()), reporterNoteMap.get(record.getReporterUserId()), record.getReporterUserId()));
            view.setOccurredAt(record.getOccurredAt());
            view.setProductName(record.getProductName());
            view.setQuantity(record.getQuantity());
            view.setCostAmount(record.getCostAmount());
            view.setFixedReturnAmount(record.getFixedReturnAmount());
            view.setProfitAmount(record.getProfitAmount());
            view.setSoldAmount(record.getSoldAmount());
            view.setRemark(record.getRemark());
            view.setSelfReporter(record.getOwnerUserId().equals(record.getReporterUserId()));
            views.add(view);
        }
        return views;
    }

    private Map<Long, User> loadUsers(Set<Long> userIds) {
        Map<Long, User> userMap = new LinkedHashMap<>();
        for (Long userId : userIds) {
            if (userId != null && !userMap.containsKey(userId)) {
                userMap.put(userId, userService.getById(userId));
            }
        }
        return userMap;
    }

    private String resolveDisplayName(User user, String noteName, Long userId) {
        if (StringUtils.hasText(noteName)) {
            return noteName.trim();
        }
        if (user == null) {
            return "用户#" + userId;
        }
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }

    private String resolveRecordTypeLabel(String recordType) {
        return TYPE_INBOUND.equals(recordType) ? LABEL_INBOUND : LABEL_REPORT;
    }

    private String normalizeRecordType(String recordType) {
        if (TYPE_INBOUND.equalsIgnoreCase(recordType)) {
            return TYPE_INBOUND;
        }
        return TYPE_REPORT;
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeRemark(String remark) {
        return StringUtils.hasText(remark) ? remark.trim() : null;
    }

    private long valueOf(Integer quantity) {
        return quantity == null ? 0L : quantity.longValue();
    }

    private BigDecimal sum(BigDecimal left, BigDecimal right) {
        BigDecimal safeLeft = left == null ? BigDecimal.ZERO : left;
        BigDecimal safeRight = right == null ? BigDecimal.ZERO : right;
        return safeLeft.add(safeRight);
    }
}
