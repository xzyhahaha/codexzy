package com.codexzy.service.impl;

import com.codexzy.entity.User;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class BusinessAuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Resource
    private UserService userService;

    @Resource
    private BusinessAccountService businessAccountService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        User user = userService.getByUsername(username);
        if (user != null) {
            businessAccountService.getOrCreateByUserId(user.getId());
        }
    }
}
