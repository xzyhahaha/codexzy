package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codexzy.dto.CheckInFormDTO;
import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.mapper.CheckInMapper;
import com.codexzy.service.CheckInService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
                .orderByDesc(CheckIn::getCheckDate));

        statDTO.setTotalCheckInDays(checkIns.size());

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        long monthCount = checkIns.stream()
                .filter(item -> !item.getCheckDate().isBefore(firstDayOfMonth))
                .count();
        statDTO.setCurrentMonthCount(monthCount);
        statDTO.setCurrentStreakDays(calculateCurrentStreak(checkIns));
        return statDTO;
    }

    @Override
    public IPage<CheckIn> getPage(Long userId, long current, long size) {
        Page<CheckIn> page = new Page<CheckIn>(current, size);
        return checkInMapper.selectPage(page, new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .orderByDesc(CheckIn::getCheckDate)
                .orderByDesc(CheckIn::getCreateTime));
    }

    @Override
    @Transactional
    public void create(Long userId, CheckInFormDTO formDTO) {
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setTitle(formDTO.getTitle());
        checkIn.setContent(formDTO.getContent());
        checkIn.setCheckDate(LocalDate.now());
        checkInMapper.insert(checkIn);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long checkInId) {
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null || !userId.equals(checkIn.getUserId())) {
            throw new IllegalArgumentException("打卡记录不存在");
        }
        checkInMapper.deleteById(checkInId);
    }

    private long calculateCurrentStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return 0;
        }

        Set<LocalDate> dates = new HashSet<LocalDate>();
        for (CheckIn checkIn : checkIns) {
            dates.add(checkIn.getCheckDate());
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
}
