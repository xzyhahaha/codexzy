package com.codexzy.controller;

import com.codexzy.dto.BusinessRecordFormDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.User;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.BusinessRecordService;
import com.codexzy.service.BusinessReporterNoteService;
import com.codexzy.service.UserService;
import com.codexzy.util.BusinessRecordStatusUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/business")
public class BusinessController {

    private static final String TYPE_ALL = "ALL";
    private static final String TYPE_REPORT = "REPORT";
    private static final String TYPE_INBOUND = "INBOUND";

    @Resource
    private UserService userService;

    @Resource
    private BusinessAccountService businessAccountService;

    @Resource
    private BusinessRecordService businessRecordService;

    @Resource
    private BusinessReporterNoteService businessReporterNoteService;

    @GetMapping
    public String index(Authentication authentication,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                        @RequestParam(defaultValue = TYPE_ALL) String scope,
                        Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        DateRange range = normalizeRange(startDate, endDate);
        String normalizedScope = normalizeScope(scope);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("startDate", range.startDate);
        model.addAttribute("endDate", range.endDate);
        model.addAttribute("scope", normalizedScope);
        model.addAttribute("summary", businessRecordService.getSummary(currentUser.getId(), range.startDate, range.endDate, normalizedScope));
        model.addAttribute("records", businessRecordService.listRecords(currentUser.getId(), range.startDate, range.endDate, normalizedScope));
        model.addAttribute("reporterSummaries", businessRecordService.listReporterSummaries(currentUser.getId(), range.startDate, range.endDate, normalizedScope));
        model.addAttribute("reportStart", range.startDate.atStartOfDay());
        model.addAttribute("reportEnd", range.endDate.atTime(23, 59, 59));
        return "business/index";
    }

    @GetMapping("/form")
    public String form(Authentication authentication,
                       @RequestParam(required = false, defaultValue = TYPE_REPORT) String type,
                       Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        String normalizedType = normalizeRecordType(type);
        boolean inboundMode = TYPE_INBOUND.equals(normalizedType);

        if (!model.containsAttribute("formDTO")) {
            BusinessRecordFormDTO formDTO = new BusinessRecordFormDTO();
            formDTO.setRecordType(normalizedType);
            formDTO.setRecordStatus(BusinessRecordStatusUtil.defaultStatus(normalizedType));
            formDTO.setTargetReportCode(inboundMode ? businessAccount.getReportCode() : "");
            formDTO.setOccurredAt(defaultOccurredAt());
            model.addAttribute("formDTO", formDTO);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("type", normalizedType);
        model.addAttribute("isInbound", inboundMode);
        model.addAttribute("isEdit", false);
        model.addAttribute("formAction", "/business");
        model.addAttribute("formTitle", inboundMode ? "商品入库" : "报单");
        model.addAttribute("formSubtitle", inboundMode
                ? "这条记录会进入你自己的经营后台。"
                : "填对方的报码后，记录会进入对方的经营后台。");
        model.addAttribute("submitLabel", "保存记录");
        return "business/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(Authentication authentication,
                       @PathVariable("id") Long id,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        try {
            var record = businessRecordService.getById(currentUser.getId(), id);

            if (!model.containsAttribute("formDTO")) {
                BusinessRecordFormDTO formDTO = new BusinessRecordFormDTO();
                formDTO.setRecordType(record.getRecordType());
                formDTO.setRecordStatus(record.getRecordStatus() != null
                        ? record.getRecordStatus()
                        : BusinessRecordStatusUtil.defaultStatus(record.getRecordType()));
                formDTO.setTargetReportCode(businessAccount.getReportCode());
                formDTO.setOccurredAt(record.getOccurredAt());
                formDTO.setProductName(record.getProductName());
                formDTO.setQuantity(record.getQuantity());
                formDTO.setCostAmount(record.getCostAmount());
                formDTO.setFixedReturnAmount(record.getFixedReturnAmount());
                formDTO.setProfitAmount(record.getProfitAmount());
                formDTO.setSoldAmount(record.getSoldAmount());
                formDTO.setRemark(record.getRemark());
                model.addAttribute("formDTO", formDTO);
            }

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("businessAccount", businessAccount);
            model.addAttribute("isInbound", TYPE_INBOUND.equals(record.getRecordType()));
            model.addAttribute("isEdit", true);
            model.addAttribute("formAction", "/business/" + id);
            model.addAttribute("formTitle", "编辑经营记录");
            model.addAttribute("formSubtitle", "可以修改时间、产品、数量、金额和备注。记录类型和报码默认保持原状态。");
            model.addAttribute("submitLabel", "保存修改");
            model.addAttribute("recordId", id);
            model.addAttribute("recordTypeLabel", TYPE_INBOUND.equals(record.getRecordType()) ? "商品入库" : "报单");
            return "business/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/business";
        }
    }

    @PostMapping
    public String create(Authentication authentication,
                         @Valid @ModelAttribute("formDTO") BusinessRecordFormDTO formDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        String normalizedType = normalizeRecordType(formDTO.getRecordType());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/form?type=" + normalizedType;
        }

        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.createRecord(currentUser.getId(), formDTO);
            redirectAttributes.addFlashAttribute("successMessage", "经营记录已保存");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/form?type=" + normalizedType;
        }
        return "redirect:/business";
    }

    @PostMapping("/{id}")
    public String update(Authentication authentication,
                         @PathVariable("id") Long id,
                         @Valid @ModelAttribute("formDTO") BusinessRecordFormDTO formDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/" + id + "/edit";
        }

        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.updateRecord(currentUser.getId(), id, formDTO);
            redirectAttributes.addFlashAttribute("successMessage", "经营记录已更新");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/" + id + "/edit";
        }
        return "redirect:/business";
    }

    @PostMapping("/{id}/delete")
    public String delete(Authentication authentication,
                         @PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.deleteRecord(currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "经营记录已删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/business";
    }

    @GetMapping("/reporters/{reporterId}")
    public String reporter(Authentication authentication,
                           @PathVariable("reporterId") Long reporterId,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                           @RequestParam(defaultValue = TYPE_ALL) String scope,
                           Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        User reporterUser = userService.getById(reporterId);
        if (reporterUser == null) {
            return "redirect:/business";
        }

        DateRange range = normalizeRange(startDate, endDate);
        String normalizedScope = normalizeScope(scope);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("reporterUser", reporterUser);
        model.addAttribute("startDate", range.startDate);
        model.addAttribute("endDate", range.endDate);
        model.addAttribute("scope", normalizedScope);
        model.addAttribute("isSelfReporter", currentUser.getId().equals(reporterId));
        model.addAttribute("reporterNote", businessReporterNoteService.getNoteName(currentUser.getId(), reporterId));
        model.addAttribute("reporterSummary", businessRecordService.getReporterSummary(currentUser.getId(), reporterId, range.startDate, range.endDate, normalizedScope));
        model.addAttribute("records", businessRecordService.listRecordsByReporter(currentUser.getId(), reporterId, range.startDate, range.endDate, normalizedScope));
        return "business/reporter";
    }

    @PostMapping("/reporters/{reporterId}/note")
    public String updateReporterNote(Authentication authentication,
                                     @PathVariable("reporterId") Long reporterId,
                                     @RequestParam(required = false) String noteName,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                     @RequestParam(defaultValue = TYPE_ALL) String scope,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        User reporterUser = userService.getById(reporterId);
        if (reporterUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "报单人不存在");
            return "redirect:/business";
        }

        businessReporterNoteService.saveOrDelete(currentUser.getId(), reporterId, noteName);
        redirectAttributes.addFlashAttribute("successMessage", "备注已保存");
        return "redirect:/business/reporters/" + reporterId + buildQuery(startDate, endDate, scope);
    }

    private DateRange normalizeRange(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate start = startDate != null ? startDate : today.withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : today;
        if (start.isAfter(end)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }
        return new DateRange(start, end);
    }

    private String normalizeScope(String scope) {
        if (TYPE_REPORT.equalsIgnoreCase(scope)) {
            return TYPE_REPORT;
        }
        if (TYPE_INBOUND.equalsIgnoreCase(scope)) {
            return TYPE_INBOUND;
        }
        return TYPE_ALL;
    }

    private String normalizeRecordType(String type) {
        if (TYPE_INBOUND.equalsIgnoreCase(type)) {
            return TYPE_INBOUND;
        }
        return TYPE_REPORT;
    }

    private LocalDateTime defaultOccurredAt() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }

    private String buildQuery(LocalDate startDate, LocalDate endDate, String scope) {
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;
        if (startDate != null) {
            query.append("?startDate=").append(startDate);
            hasParam = true;
        }
        if (endDate != null) {
            query.append(hasParam ? "&" : "?").append("endDate=").append(endDate);
            hasParam = true;
        }
        if (scope != null && !TYPE_ALL.equalsIgnoreCase(scope)) {
            query.append(hasParam ? "&" : "?").append("scope=").append(scope);
        }
        return query.toString();
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}
