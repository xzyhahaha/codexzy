package com.codexzy.controller;

import com.codexzy.dto.PasswordUpdateDTO;
import com.codexzy.dto.UserProfileUpdateDTO;
import com.codexzy.entity.User;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/user/profile")
    public String profile(Authentication authentication, Model model) {
        User currentUser = userService.getByUsername(authentication.getName());

        if (!model.containsAttribute("profileDTO")) {
            UserProfileUpdateDTO profileDTO = new UserProfileUpdateDTO();
            profileDTO.setNickname(currentUser.getNickname());
            profileDTO.setEmail(currentUser.getEmail());
            model.addAttribute("profileDTO", profileDTO);
        }

        if (!model.containsAttribute("passwordDTO")) {
            model.addAttribute("passwordDTO", new PasswordUpdateDTO());
        }

        model.addAttribute("currentUser", currentUser);
        return "user/profile";
    }

    @PostMapping("/user/profile")
    public String updateProfile(Authentication authentication,
                                @Valid @ModelAttribute("profileDTO") UserProfileUpdateDTO profileDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileDTO", bindingResult);
            redirectAttributes.addFlashAttribute("profileDTO", profileDTO);
            return "redirect:/user/profile";
        }

        User currentUser = userService.getByUsername(authentication.getName());
        userService.updateProfile(currentUser.getId(), profileDTO);
        redirectAttributes.addFlashAttribute("successMessage", "资料已更新");
        return "redirect:/user/profile";
    }

    @PostMapping("/user/password")
    public String updatePassword(Authentication authentication,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 @Valid @ModelAttribute("passwordDTO") PasswordUpdateDTO passwordDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordDTO", bindingResult);
            redirectAttributes.addFlashAttribute("passwordDTO", passwordDTO);
            return "redirect:/user/profile";
        }

        try {
            User currentUser = userService.getByUsername(authentication.getName());
            userService.updatePassword(currentUser.getId(), passwordDTO);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            redirectAttributes.addFlashAttribute("successMessage", "密码已更新，请重新登录");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("passwordDTO", passwordDTO);
            return "redirect:/user/profile";
        }
    }
}