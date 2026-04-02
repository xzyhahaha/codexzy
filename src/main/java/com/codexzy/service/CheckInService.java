package com.codexzy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codexzy.dto.CalendarDayDTO;
import com.codexzy.dto.CheckInFormDTO;
import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.CheckIn;

import java.util.List;

public interface CheckInService {

    CheckInStatDTO getStatistics(Long userId);

    IPage<CheckIn> getPage(Long userId, long current, long size);

    void create(Long userId, CheckInFormDTO formDTO);

    void delete(Long userId, Long checkInId);

    List<CalendarDayDTO> getCalendar(Long userId, int year, int month);
}
