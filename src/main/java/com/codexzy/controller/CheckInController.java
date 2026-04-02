package com.codexzy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codexzy.dto.CalendarDayDTO;
import com.codexzy.dto.CheckInFormDTO;
import com.codexzy.entity.CheckIn;
import com.codexzy.entity.User;
import com.codexzy.service.CheckInService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;

@Controller
public class CheckInController {

    @Resource
    private CheckInService checkInService;

    @Resource
    private UserService userService;

    @GetMapping("/checkin")
    public String list(Authentication authentication,
                       @RequestParam(defaultValue = "1") long page,
                       Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        IPage<CheckIn> pageData = checkInService.getPage(currentUser.getId(), page, 10);
        model.addAttribute("pageData", pageData);
        return "checkin/list";
    }

    @GetMapping("/checkin/calendar")
    public String calendar(Authentication authentication,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        YearMonth current = year == null || month == null ? YearMonth.now() : YearMonth.of(year, month);
        List<CalendarDayDTO> days = checkInService.getCalendar(currentUser.getId(), current.getYear(), current.getMonthValue());

        model.addAttribute("calendarDays", days);
        model.addAttribute("currentYear", current.getYear());
        model.addAttribute("currentMonth", current.getMonthValue());
        model.addAttribute("currentLabel", current.getYear() + " 年 " + current.getMonthValue() + " 月");
        model.addAttribute("prev", current.minusMonths(1));
        model.addAttribute("next", current.plusMonths(1));
        return "checkin/calendar";
    }

    @GetMapping("/checkin/form")
    public String form(Model model) {
        if (!model.containsAttribute("formDTO")) {
            model.addAttribute("formDTO", new CheckInFormDTO());
        }
        return "checkin/form";
    }

    @PostMapping("/checkin")
    public String create(Authentication authentication,
                         @Valid @ModelAttribute("formDTO") CheckInFormDTO formDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/checkin/form";
        }

        User currentUser = userService.getByUsername(authentication.getName());
        checkInService.create(currentUser.getId(), formDTO);
        redirectAttributes.addFlashAttribute("successMessage", "打卡已创建");
        return "redirect:/checkin";
    }

    @PostMapping("/checkin/{id}/delete")
    public String delete(Authentication authentication,
                         @PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getByUsername(authentication.getName());
            checkInService.delete(currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "打卡已删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/checkin";
    }
}
