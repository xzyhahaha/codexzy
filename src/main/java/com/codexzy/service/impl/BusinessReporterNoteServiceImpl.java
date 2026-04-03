package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.BusinessReporterNote;
import com.codexzy.mapper.BusinessReporterNoteMapper;
import com.codexzy.service.BusinessReporterNoteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessReporterNoteServiceImpl implements BusinessReporterNoteService {

    @Resource
    private BusinessReporterNoteMapper businessReporterNoteMapper;

    @Override
    public String getNoteName(Long ownerUserId, Long reporterUserId) {
        BusinessReporterNote note = getNote(ownerUserId, reporterUserId);
        return note == null ? null : note.getNoteName();
    }

    @Override
    public Map<Long, String> getNoteNameMap(Long ownerUserId, Collection<Long> reporterUserIds) {
        Map<Long, String> noteNameMap = new LinkedHashMap<>();
        if (ownerUserId == null || CollectionUtils.isEmpty(reporterUserIds)) {
            return noteNameMap;
        }

        List<BusinessReporterNote> notes = businessReporterNoteMapper.selectList(new LambdaQueryWrapper<BusinessReporterNote>()
                .eq(BusinessReporterNote::getOwnerUserId, ownerUserId)
                .in(BusinessReporterNote::getReporterUserId, reporterUserIds));
        for (BusinessReporterNote note : notes) {
            if (StringUtils.hasText(note.getNoteName())) {
                noteNameMap.put(note.getReporterUserId(), note.getNoteName());
            }
        }
        return noteNameMap;
    }

    @Override
    @Transactional
    public BusinessReporterNote saveOrDelete(Long ownerUserId, Long reporterUserId, String noteName) {
        if (!StringUtils.hasText(noteName)) {
            delete(ownerUserId, reporterUserId);
            return null;
        }

        String trimmed = noteName.trim();
        BusinessReporterNote note = getNote(ownerUserId, reporterUserId);
        if (note == null) {
            note = new BusinessReporterNote();
            note.setOwnerUserId(ownerUserId);
            note.setReporterUserId(reporterUserId);
            note.setNoteName(trimmed);
            businessReporterNoteMapper.insert(note);
        } else {
            note.setNoteName(trimmed);
            businessReporterNoteMapper.updateById(note);
        }
        return note;
    }

    private BusinessReporterNote getNote(Long ownerUserId, Long reporterUserId) {
        if (ownerUserId == null || reporterUserId == null) {
            return null;
        }
        return businessReporterNoteMapper.selectOne(new LambdaQueryWrapper<BusinessReporterNote>()
                .eq(BusinessReporterNote::getOwnerUserId, ownerUserId)
                .eq(BusinessReporterNote::getReporterUserId, reporterUserId)
                .last("LIMIT 1"));
    }

    @Transactional
    protected void delete(Long ownerUserId, Long reporterUserId) {
        BusinessReporterNote note = getNote(ownerUserId, reporterUserId);
        if (note != null) {
            businessReporterNoteMapper.deleteById(note.getId());
        }
    }
}
