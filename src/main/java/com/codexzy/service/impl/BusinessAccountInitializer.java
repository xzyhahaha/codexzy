package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.entity.User;
import com.codexzy.mapper.UserMapper;
import com.codexzy.service.BusinessAccountService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BusinessAccountInitializer implements ApplicationRunner {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BusinessAccountService businessAccountService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<>());
        for (User user : users) {
            businessAccountService.getOrCreateByUserId(user.getId());
        }
    }
}
