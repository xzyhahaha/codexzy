package com.codexzy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codexzy.dto.CheckInStatDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.CheckIn;
import com.codexzy.entity.MemoFile;
import com.codexzy.entity.User;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.CheckInService;
import com.codexzy.service.MemoService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class HomeController {

    private static final DateTimeFormatter TODAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA);

    @Resource
    private UserService userService;

    @Resource
    private BusinessAccountService businessAccountService;

    @Resource
    private CheckInService checkInService;

    @Resource
    private MemoService memoService;

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        CheckInStatDTO stats = checkInService.getStatistics(currentUser.getId());
        IPage<CheckIn> recentPage = checkInService.getPage(currentUser.getId(), 1, 4);
        Map<Long, List<MemoFile>> groupedFiles = memoService.listFilesGroupedByCategory(currentUser.getId());
        long memoFileCount = groupedFiles.values().stream().mapToLong(List::size).sum();
        long memoCategoryCount = memoService.listCategories(currentUser.getId()).size();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("stats", stats);
        model.addAttribute("recentCheckIns", recentPage.getRecords());
        model.addAttribute("memoFileCount", memoFileCount);
        model.addAttribute("memoCategoryCount", memoCategoryCount);
        model.addAttribute("todayLabel", LocalDate.now().format(TODAY_FORMATTER));
        return "index";
    }

    @ResponseBody
    @GetMapping(value = "/.well-known/appspecific/com.chrome.devtools.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> chromeDevtoolsConfig() {
        return Map.of();
    }
}
