package com.codexzy.service;

import com.codexzy.dto.PasswordUpdateDTO;
import com.codexzy.dto.UserProfileUpdateDTO;
import com.codexzy.dto.UserRegisterDTO;
import com.codexzy.entity.User;

public interface UserService {

    User register(UserRegisterDTO registerDTO);

    User getByUsername(String username);

    User getById(Long id);

    User updateProfile(Long userId, UserProfileUpdateDTO updateDTO);

    void updatePassword(Long userId, PasswordUpdateDTO updateDTO);

    boolean existsByUsername(String username);
}
