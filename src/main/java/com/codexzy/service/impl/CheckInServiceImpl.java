package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codexzy.dto.CalendarDayDTO;
import com.codexzy.dto.CheckInFormDTO;
import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.mapper.CheckInMapper;
import com.codexzy.service.CheckInService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Resource
    private CheckInMapper checkInMapper;

    @Override
    public CheckInStatDTO getStatistics(Long userId) {
        CheckInStatDTO statDTO = new CheckInStatDTO();
        List<CheckIn> checkIns = checkInMapper.selectList(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .orderByDesc(CheckIn::getCheckDate)
                .orderByDesc(CheckIn::getCreateTime));

        Set<LocalDate> distinctDates = new HashSet<>();
        for (CheckIn checkIn : checkIns) {
            distinctDates.add(checkIn.getCheckDate());
        }
        statDTO.setTotalCheckInDays(distinctDates.size());

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        Set<LocalDate> currentMonthDates = new HashSet<>();
        for (LocalDate date : distinctDates) {
            if (!date.isBefore(firstDayOfMonth)) {
                currentMonthDates.add(date);
            }
        }
        statDTO.setCurrentMonthCount(currentMonthDates.size());
        statDTO.setCurrentStreakDays(calculateCurrentStreak(distinctDates));
        return statDTO;
    }

    @Override
    public IPage<CheckIn> getPage(Long userId, long current, long size) {
        Page<CheckIn> page = new Page<>(current, size);
        return checkInMapper.selectPage(page, new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .orderByDesc(CheckIn::getCheckDate)
                .orderByDesc(CheckIn::getCreateTime));
    }

    @Override
    public CheckIn getById(Long userId, Long checkInId) {
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null || !userId.equals(checkIn.getUserId())) {
            throw new IllegalArgumentException("打卡记录不存在");
        }
        return checkIn;
    }

    @Override
    public List<CheckIn> listByDate(Long userId, LocalDate date) {
        return checkInMapper.selectList(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .eq(CheckIn::getCheckDate, date)
                .orderByDesc(CheckIn::getCreateTime));
    }

    @Override
    @Transactional
    public void create(Long userId, CheckInFormDTO formDTO) {
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setTitle(formDTO.getTitle().trim());
        checkIn.setContent(normalizeContent(formDTO.getContent()));
        checkIn.setCheckDate(formDTO.getCheckDate());
        checkInMapper.insert(checkIn);
    }

    @Override
    @Transactional
    public void update(Long userId, Long checkInId, CheckInFormDTO formDTO) {
        CheckIn checkIn = getById(userId, checkInId);
        checkIn.setTitle(formDTO.getTitle().trim());
        checkIn.setContent(normalizeContent(formDTO.getContent()));
        checkIn.setCheckDate(formDTO.getCheckDate());
        checkInMapper.updateById(checkIn);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long checkInId) {
        CheckIn checkIn = getById(userId, checkInId);
        checkInMapper.deleteById(checkIn.getId());
    }

    @Override
    public List<CalendarDayDTO> getCalendar(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<CheckIn> checkIns = checkInMapper.selectList(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .between(CheckIn::getCheckDate, start, end)
                .orderByAsc(CheckIn::getCheckDate)
                .orderByDesc(CheckIn::getCreateTime));

        Map<LocalDate, List<CheckIn>> groupedByDate = new HashMap<>();
        for (CheckIn checkIn : checkIns) {
            groupedByDate.computeIfAbsent(checkIn.getCheckDate(), key -> new ArrayList<>()).add(checkIn);
        }

        List<CalendarDayDTO> days = new ArrayList<>();
        int firstWeekDay = start.getDayOfWeek().getValue();
        for (int i = 1; i < firstWeekDay; i++) {
            CalendarDayDTO empty = new CalendarDayDTO();
            empty.setCurrentMonth(false);
            days.add(empty);
        }

        LocalDate today = LocalDate.now();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            List<CheckIn> records = groupedByDate.get(date);

            CalendarDayDTO dto = new CalendarDayDTO();
            dto.setDayOfMonth(day);
            dto.setCurrentMonth(true);
            dto.setDate(date);
            dto.setToday(today.equals(date));
            dto.setCheckedIn(records != null && !records.isEmpty());
            dto.setRecordCount(records == null ? 0 : records.size());
            dto.setPrimaryTitle(records == null || records.isEmpty() ? null : records.get(0).getTitle());
            days.add(dto);
        }
        return days;
    }

    private long calculateCurrentStreak(Set<LocalDate> dates) {
        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate cursor = LocalDate.now();
        if (!dates.contains(cursor)) {
            cursor = cursor.minusDays(1);
        }

        long streak = 0;
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private String normalizeContent(String content) {
        return StringUtils.hasText(content) ? content.trim() : null;
    }
}