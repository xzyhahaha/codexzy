package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.MemoFile;
import com.codexzy.mapper.MemoFileMapper;
import com.codexzy.service.MemoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemoServiceImpl implements MemoService {

    @Resource
    private MemoFileMapper memoFileMapper;

    @Override
    public long countByUserId(Long userId) {
        return memoFileMapper.selectCount(new LambdaQueryWrapper<MemoFile>()
                .eq(MemoFile::getUserId, userId));
    }
}
