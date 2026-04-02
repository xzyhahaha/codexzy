package com.codexzy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public String calendar() {
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
