package com.codexzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codexzy.dto.PasswordUpdateDTO;
import com.codexzy.dto.UserProfileUpdateDTO;
import com.codexzy.dto.UserRegisterDTO;
import com.codexzy.entity.User;
import com.codexzy.mapper.UserMapper;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserRegisterDTO registerDTO) {
        if (existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getUsername());
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1"));
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateDTO updateDTO) {
        User user = requireUser(userId);
        user.setNickname(updateDTO.getNickname());
        user.setEmail(updateDTO.getEmail());
        userMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO updateDTO) {
        User user = requireUser(userId);
        if (!passwordEncoder.matches(updateDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return getByUsername(username) != null;
    }

    private User requireUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }
}
