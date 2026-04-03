package com.codexzy.service;

import com.codexzy.entity.BusinessReporterNote;

import java.util.Collection;
import java.util.Map;

public interface BusinessReporterNoteService {

    String getNoteName(Long ownerUserId, Long reporterUserId);

    Map<Long, String> getNoteNameMap(Long ownerUserId, Collection<Long> reporterUserIds);

    BusinessReporterNote saveOrDelete(Long ownerUserId, Long reporterUserId, String noteName);
}
