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
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class CheckInController {

    private static final DateTimeFormatter DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");

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

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("stats", checkInService.getStatistics(currentUser.getId()));
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

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("stats", checkInService.getStatistics(currentUser.getId()));
        model.addAttribute("calendarDays", days);
        model.addAttribute("currentLabel", String.format("%d年%d月", current.getYear(), current.getMonthValue()));
        model.addAttribute("prev", current.minusMonths(1));
        model.addAttribute("next", current.plusMonths(1));
        model.addAttribute("today", LocalDate.now());
        return "checkin/calendar";
    }

    @GetMapping("/checkin/day")
    public String dayDetail(Authentication authentication,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                            Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        LocalDate targetDate = normalizeCheckDate(date);
        List<CheckIn> records = checkInService.listByDate(currentUser.getId(), targetDate);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("stats", checkInService.getStatistics(currentUser.getId()));
        model.addAttribute("selectedDate", targetDate);
        model.addAttribute("selectedDateLabel", targetDate.format(DATE_LABEL_FORMATTER));
        model.addAttribute("previousDate", targetDate.minusDays(1));
        model.addAttribute("nextDate", targetDate.plusDays(1));
        model.addAttribute("allowNextDate", targetDate.isBefore(LocalDate.now()));
        model.addAttribute("records", records);
        return "checkin/day";
    }

    @GetMapping("/checkin/form")
    public String form(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                       Model model) {
        if (!model.containsAttribute("formDTO")) {
            CheckInFormDTO formDTO = new CheckInFormDTO();
            formDTO.setCheckDate(normalizeCheckDate(date));
            model.addAttribute("formDTO", formDTO);
        }
        applyFormViewModel(model, true, null);
        return "checkin/form";
    }

    @GetMapping("/checkin/{id}/edit")
    public String edit(Authentication authentication,
                       @PathVariable("id") Long id,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        try {
            CheckIn checkIn = checkInService.getById(currentUser.getId(), id);
            if (!model.containsAttribute("formDTO")) {
                CheckInFormDTO formDTO = new CheckInFormDTO();
                formDTO.setTitle(checkIn.getTitle());
                formDTO.setContent(checkIn.getContent());
                formDTO.setCheckDate(checkIn.getCheckDate());
                model.addAttribute("formDTO", formDTO);
            }
            applyFormViewModel(model, false, id);
            return "checkin/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/checkin";
        }
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

    @PostMapping("/checkin/{id}")
    public String update(Authentication authentication,
                         @PathVariable("id") Long id,
                         @Valid @ModelAttribute("formDTO") CheckInFormDTO formDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/checkin/" + id + "/edit";
        }

        User currentUser = userService.getByUsername(authentication.getName());
        try {
            checkInService.update(currentUser.getId(), id, formDTO);
            redirectAttributes.addFlashAttribute("successMessage", "打卡已更新");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
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

    private void applyFormViewModel(Model model, boolean createMode, Long checkInId) {
        model.addAttribute("maxDate", LocalDate.now());
        model.addAttribute("isEdit", !createMode);
        model.addAttribute("formTitle", createMode ? "新建打卡" : "编辑打卡");
        model.addAttribute("formSubtitle", createMode
                ? "支持补卡，默认日期是今天。把今天最关键的动作和结果写成一条能回看的记录。"
                : "这里可以修改标题、内容和日期。保存后会直接覆盖原来的打卡内容。");
        model.addAttribute("formAction", createMode ? "/checkin" : "/checkin/" + checkInId);
        model.addAttribute("submitLabel", createMode ? "保存打卡" : "保存修改");
    }

    private LocalDate normalizeCheckDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date == null || date.isAfter(today)) {
            return today;
        }
        return date;
    }
}