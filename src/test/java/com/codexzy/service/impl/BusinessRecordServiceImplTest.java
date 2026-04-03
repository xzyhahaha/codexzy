package com.codexzy.service.impl;

import com.codexzy.dto.BusinessRecordFormDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.BusinessRecord;
import com.codexzy.mapper.BusinessRecordMapper;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.BusinessReporterNoteService;
import com.codexzy.service.UserService;
import com.codexzy.util.BusinessRecordStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessRecordServiceImplTest {

    @Mock
    private BusinessRecordMapper businessRecordMapper;

    @Mock
    private BusinessAccountService businessAccountService;

    @Mock
    private UserService userService;

    @Mock
    private BusinessReporterNoteService businessReporterNoteService;

    @InjectMocks
    private BusinessRecordServiceImpl businessRecordService;

    @Test
    void createReportRecordShouldIgnoreSubmittedStatus() {
        BusinessAccount currentAccount = new BusinessAccount();
        currentAccount.setUserId(1L);
        BusinessAccount targetAccount = new BusinessAccount();
        targetAccount.setUserId(2L);

        when(businessAccountService.getOrCreateByUserId(1L)).thenReturn(currentAccount);
        when(businessAccountService.getByReportCode("TARGET-001")).thenReturn(targetAccount);
        when(businessRecordMapper.insert(any(BusinessRecord.class))).thenAnswer(invocation -> {
            BusinessRecord record = invocation.getArgument(0);
            record.setId(100L);
            return 1;
        });

        BusinessRecordFormDTO formDTO = new BusinessRecordFormDTO();
        formDTO.setRecordType("REPORT");
        formDTO.setTargetReportCode("TARGET-001");
        formDTO.setRecordStatus(BusinessRecordStatusUtil.SETTLED);
        formDTO.setOccurredAt(LocalDateTime.of(2026, 4, 3, 10, 30));
        formDTO.setProductName("product");
        formDTO.setQuantity(3);
        formDTO.setCostAmount(new BigDecimal("12.30"));
        formDTO.setFixedReturnAmount(new BigDecimal("1.00"));
        formDTO.setProfitAmount(new BigDecimal("2.00"));
        formDTO.setSoldAmount(new BigDecimal("15.30"));

        BusinessRecord record = businessRecordService.createRecord(1L, formDTO);

        ArgumentCaptor<BusinessRecord> recordCaptor = ArgumentCaptor.forClass(BusinessRecord.class);
        org.mockito.Mockito.verify(businessRecordMapper).insert(recordCaptor.capture());

        assertEquals(2L, record.getOwnerUserId());
        assertEquals(1L, record.getReporterUserId());
        assertEquals(BusinessRecordStatusUtil.UNINVENTORIED, record.getRecordStatus());
        assertEquals(BusinessRecordStatusUtil.UNINVENTORIED, recordCaptor.getValue().getRecordStatus());
    }
}
