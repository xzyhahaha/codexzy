package com.codexzy.controller;

import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.User;
import com.codexzy.service.CheckInService;
import com.codexzy.service.MemoService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Resource
    private UserService userService;

    @Resource
    private CheckInService checkInService;

    @Resource
    private MemoService memoService;

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        CheckInStatDTO statDTO = checkInService.getStatistics(currentUser.getId());
        long memoFileCount = memoService.countByUserId(currentUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("stats", statDTO);
        model.addAttribute("memoFileCount", memoFileCount);
        return "index";
    }
}
