package com.codexzy.controller;

import com.codexzy.dto.MemoCategoryFormDTO;
import com.codexzy.dto.MemoUploadDTO;
import com.codexzy.entity.MemoCategory;
import com.codexzy.entity.MemoFile;
import com.codexzy.entity.User;
import com.codexzy.service.FileService;
import com.codexzy.service.MemoService;
import com.codexzy.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Controller
public class MemoController {

    @Resource
    private MemoService memoService;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @GetMapping("/memo")
    public String index(Authentication authentication, Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        List<MemoCategory> categories = memoService.listCategories(currentUser.getId());
        Map<Long, List<MemoFile>> groupedFiles = memoService.listFilesGroupedByCategory(currentUser.getId());

        if (!model.containsAttribute("categoryForm")) {
            model.addAttribute("categoryForm", new MemoCategoryFormDTO());
        }
        if (!model.containsAttribute("uploadForm")) {
            model.addAttribute("uploadForm", new MemoUploadDTO());
        }

        model.addAttribute("categories", categories);
        model.addAttribute("groupedFiles", groupedFiles);
        return "memo/index";
    }

    @PostMapping("/memo/category")
    public String createCategory(Authentication authentication,
                                 @Valid @ModelAttribute("categoryForm") MemoCategoryFormDTO categoryForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.categoryForm", bindingResult);
            redirectAttributes.addFlashAttribute("categoryForm", categoryForm);
            return "redirect:/memo";
        }

        try {
            User currentUser = userService.getByUsername(authentication.getName());
            memoService.createCategory(currentUser.getId(), categoryForm);
            redirectAttributes.addFlashAttribute("successMessage", "分类已创建");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/memo";
    }

    @PostMapping("/memo/category/{id}/delete")
    public String deleteCategory(Authentication authentication,
                                 @PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getByUsername(authentication.getName());
            memoService.deleteCategory(currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "分类已删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/memo";
    }

    @PostMapping("/memo/upload")
    public String upload(Authentication authentication,
                         @Valid @ModelAttribute("uploadForm") MemoUploadDTO uploadForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.uploadForm", bindingResult);
            redirectAttributes.addFlashAttribute("uploadForm", uploadForm);
            return "redirect:/memo";
        }

        try {
            User currentUser = userService.getByUsername(authentication.getName());
            memoService.uploadFile(currentUser.getId(), uploadForm);
            redirectAttributes.addFlashAttribute("successMessage", "文件已上传");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/memo";
    }

    @GetMapping("/memo/file/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> download(Authentication authentication,
                                                                         @PathVariable("id") Long id) {
        User currentUser = userService.getByUsername(authentication.getName());
        MemoFile memoFile = memoService.getFile(currentUser.getId(), id);
        Path filePath = fileService.resolve(memoFile.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "文件不存在或已被删除");
        }

        org.springframework.core.io.Resource resource = new FileSystemResource(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(memoFile.getFileName()).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/memo/file/{id}/delete")
    public String delete(Authentication authentication,
                         @PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getByUsername(authentication.getName());
            memoService.deleteFile(currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "文件已删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/memo";
    }
}