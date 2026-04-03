package com.codexzy.service;

import com.codexzy.dto.BusinessRecordFormDTO;
import com.codexzy.dto.BusinessRecordSummaryDTO;
import com.codexzy.dto.BusinessRecordViewDTO;
import com.codexzy.dto.BusinessReporterSummaryDTO;
import com.codexzy.entity.BusinessRecord;

import java.time.LocalDate;
import java.util.List;

public interface BusinessRecordService {

    BusinessRecord createRecord(Long currentUserId, BusinessRecordFormDTO formDTO);

    BusinessRecord getById(Long ownerUserId, Long recordId);

    void updateRecord(Long ownerUserId, Long recordId, BusinessRecordFormDTO formDTO);

    void deleteRecord(Long ownerUserId, Long recordId);

    BusinessRecordSummaryDTO getSummary(Long ownerUserId, LocalDate startDate, LocalDate endDate, String scope);

    List<BusinessRecordViewDTO> listRecords(Long ownerUserId, LocalDate startDate, LocalDate endDate, String scope);

    List<BusinessReporterSummaryDTO> listReporterSummaries(Long ownerUserId, LocalDate startDate, LocalDate endDate, String scope);

    BusinessRecordSummaryDTO getReporterSummary(Long ownerUserId, Long reporterUserId, LocalDate startDate, LocalDate endDate, String scope);

    List<BusinessRecordViewDTO> listRecordsByReporter(Long ownerUserId, Long reporterUserId, LocalDate startDate, LocalDate endDate, String scope);
}
