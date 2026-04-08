package com.codexzy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codexzy.dto.BusinessRecordFormDTO;
import com.codexzy.dto.BusinessRecordViewDTO;
import com.codexzy.entity.BusinessAccount;
import com.codexzy.entity.User;
import com.codexzy.service.BusinessAccountService;
import com.codexzy.service.BusinessRecordService;
import com.codexzy.service.BusinessReporterNoteService;
import com.codexzy.service.BusinessReportTargetService;
import com.codexzy.service.UserService;
import com.codexzy.util.BusinessRecordStatusUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/business")
public class BusinessController {

    private static final String TYPE_ALL = "ALL";
    private static final String TYPE_REPORT = "REPORT";
    private static final String TYPE_INBOUND = "INBOUND";
    private static final long RECORD_PAGE_SIZE = 10L;
    private static final String MOBILE_VIEW_OVERVIEW = "overview";
    private static final String MOBILE_VIEW_RECORDS = "records";
    private static final String MOBILE_VIEW_FILTER = "filter";
    private static final String DEFAULT_RETURN_TO = "/business";
    private static final String SESSION_BUSINESS_START_DATE = "business.startDate";
    private static final String SESSION_BUSINESS_END_DATE = "business.endDate";
    private static final String SESSION_BUSINESS_SCOPE = "business.scope";
    private static final String SESSION_BUSINESS_VIEW = "business.view";

    @Resource
    private UserService userService;

    @Resource
    private BusinessAccountService businessAccountService;

    @Resource
    private BusinessRecordService businessRecordService;

    @Resource
    private BusinessReporterNoteService businessReporterNoteService;

    @Resource
    private BusinessReportTargetService businessReportTargetService;

    @GetMapping
    public String index(Authentication authentication,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                        @RequestParam(required = false) String scope,
                        @RequestParam(defaultValue = "1") long page,
                        @RequestParam(required = false) String view,
                        HttpSession session,
                        Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        LocalDate resolvedStartDate = startDate != null ? startDate : getSessionLocalDate(session, SESSION_BUSINESS_START_DATE);
        LocalDate resolvedEndDate = endDate != null ? endDate : getSessionLocalDate(session, SESSION_BUSINESS_END_DATE);
        String resolvedScope = hasBusinessScope(scope) ? scope : getSessionString(session, SESSION_BUSINESS_SCOPE);
        String resolvedView = hasBusinessView(view) ? view : getSessionString(session, SESSION_BUSINESS_VIEW);

        DateRange range = normalizeRange(resolvedStartDate, resolvedEndDate);
        String normalizedScope = normalizeScope(resolvedScope);
        String activeMobileView = normalizeMobileView(resolvedView);
        IPage<BusinessRecordViewDTO> pageData = businessRecordService.getRecordPage(
                currentUser.getId(), range.startDate, range.endDate, normalizedScope, page, RECORD_PAGE_SIZE);

        session.setAttribute(SESSION_BUSINESS_START_DATE, range.startDate);
        session.setAttribute(SESSION_BUSINESS_END_DATE, range.endDate);
        session.setAttribute(SESSION_BUSINESS_SCOPE, normalizedScope);
        session.setAttribute(SESSION_BUSINESS_VIEW, activeMobileView);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("startDate", range.startDate);
        model.addAttribute("endDate", range.endDate);
        model.addAttribute("scope", normalizedScope);
        model.addAttribute("activeMobileView", activeMobileView);
        model.addAttribute("summary", businessRecordService.getSummary(currentUser.getId(), range.startDate, range.endDate, normalizedScope));
        model.addAttribute("pageData", pageData);
        model.addAttribute("records", pageData.getRecords());
        model.addAttribute("reporterSummaries", businessRecordService.listReporterSummaries(currentUser.getId(), range.startDate, range.endDate, normalizedScope));
        model.addAttribute("reportStart", range.startDate.atStartOfDay());
        model.addAttribute("reportEnd", range.endDate.atTime(23, 59, 59));
        return "business/index";
    }

    @GetMapping("/form")
    public String form(Authentication authentication,
                       @RequestParam(required = false, defaultValue = TYPE_REPORT) String type,
                       @RequestParam(required = false) String returnTo,
                       Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        String normalizedType = normalizeRecordType(type);
        boolean inboundMode = TYPE_INBOUND.equals(normalizedType);
        var boundTargets = businessReportTargetService.listOptions(currentUser.getId());

        if (!model.containsAttribute("formDTO")) {
            BusinessRecordFormDTO formDTO = new BusinessRecordFormDTO();
            formDTO.setRecordType(normalizedType);
            formDTO.setRecordStatus(BusinessRecordStatusUtil.defaultStatus(normalizedType));
            formDTO.setTargetReportCode(inboundMode
                    ? businessAccount.getReportCode()
                    : (boundTargets.isEmpty() ? "" : boundTargets.get(0).getTargetReportCode()));
            formDTO.setBindTarget(Boolean.FALSE);
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
                ? "这条记录会进入你自己的经营页。"
                : "填目标报码后，记录会进入对方的经营页。");
        model.addAttribute("submitLabel", "保存记录");
        model.addAttribute("boundTargets", boundTargets);
        model.addAttribute("returnTo", resolveReturnTarget(returnTo));
        return "business/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(Authentication authentication,
                       @PathVariable("id") Long id,
                       @RequestParam(required = false) String returnTo,
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
            model.addAttribute("formSubtitle", "可以修改时间、产品、数量、金额和备注。记录类型和报码保持原状态。");
            model.addAttribute("submitLabel", "保存修改");
            model.addAttribute("recordId", id);
            model.addAttribute("recordTypeLabel", TYPE_INBOUND.equals(record.getRecordType()) ? "商品入库" : "报单");
            model.addAttribute("boundTargets", businessReportTargetService.listOptions(currentUser.getId()));
            model.addAttribute("returnTo", resolveReturnTarget(returnTo));
            return "business/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:" + resolveReturnTarget(returnTo);
        }
    }

    @PostMapping
    public String create(Authentication authentication,
                         @Valid @ModelAttribute("formDTO") BusinessRecordFormDTO formDTO,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String returnTo,
                         RedirectAttributes redirectAttributes) {
        String normalizedType = normalizeRecordType(formDTO.getRecordType());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/form?type=" + normalizedType + buildReturnToParam(returnTo);
        }

        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.createRecord(currentUser.getId(), formDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    TYPE_REPORT.equals(normalizedType) && Boolean.TRUE.equals(formDTO.getBindTarget())
                            ? "经营记录已保存，目标账号已加入常用报单列表"
                            : "经营记录已保存");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/form?type=" + normalizedType + buildReturnToParam(returnTo);
        }
        return "redirect:" + resolveReturnTarget(returnTo);
    }

    @PostMapping("/{id}")
    public String update(Authentication authentication,
                         @PathVariable("id") Long id,
                         @Valid @ModelAttribute("formDTO") BusinessRecordFormDTO formDTO,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String returnTo,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formDTO", bindingResult);
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/" + id + "/edit" + buildReturnToQuery(returnTo);
        }

        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.updateRecord(currentUser.getId(), id, formDTO);
            redirectAttributes.addFlashAttribute("successMessage", "经营记录已更新");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("formDTO", formDTO);
            return "redirect:/business/" + id + "/edit" + buildReturnToQuery(returnTo);
        }
        return "redirect:" + resolveReturnTarget(returnTo);
    }

    @PostMapping("/{id}/delete")
    public String delete(Authentication authentication,
                         @PathVariable("id") Long id,
                         @RequestParam(required = false) Long reporterId,
                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                         @RequestParam(defaultValue = TYPE_ALL) String scope,
                         @RequestParam(required = false) Long page,
                         @RequestParam(required = false) String view,
                         RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        try {
            businessRecordService.deleteRecord(currentUser.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "经营记录已删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        String query = buildQuery(startDate, endDate, scope, page, view);
        if (reporterId != null) {
            return "redirect:/business/reporters/" + reporterId + query;
        }
        return "redirect:/business" + query;
    }

    @GetMapping("/reporters/{reporterId}")
    public String reporter(Authentication authentication,
                           @PathVariable("reporterId") Long reporterId,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                           @RequestParam(defaultValue = TYPE_ALL) String scope,
                           @RequestParam(defaultValue = "1") long page,
                           @RequestParam(defaultValue = MOBILE_VIEW_OVERVIEW) String view,
                           Model model) {
        User currentUser = userService.getByUsername(authentication.getName());
        BusinessAccount businessAccount = businessAccountService.getOrCreateByUserId(currentUser.getId());
        User reporterUser = userService.getById(reporterId);
        if (reporterUser == null) {
            return "redirect:/business";
        }

        DateRange range = normalizeRange(startDate, endDate);
        String normalizedScope = normalizeScope(scope);
        String activeMobileView = normalizeMobileView(view);
        IPage<BusinessRecordViewDTO> pageData = businessRecordService.getReporterRecordPage(
                currentUser.getId(), reporterId, range.startDate, range.endDate, normalizedScope, page, RECORD_PAGE_SIZE);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("businessAccount", businessAccount);
        model.addAttribute("reporterUser", reporterUser);
        model.addAttribute("startDate", range.startDate);
        model.addAttribute("endDate", range.endDate);
        model.addAttribute("scope", normalizedScope);
        model.addAttribute("activeMobileView", activeMobileView);
        model.addAttribute("pageData", pageData);
        model.addAttribute("records", pageData.getRecords());
        model.addAttribute("isSelfReporter", currentUser.getId().equals(reporterId));
        model.addAttribute("reporterNote", businessReporterNoteService.getNoteName(currentUser.getId(), reporterId));
        model.addAttribute("reporterSummary", businessRecordService.getReporterSummary(currentUser.getId(), reporterId, range.startDate, range.endDate, normalizedScope));
        return "business/reporter";
    }

    @PostMapping("/reporters/{reporterId}/note")
    public String updateReporterNote(Authentication authentication,
                                     @PathVariable("reporterId") Long reporterId,
                                     @RequestParam(required = false) String noteName,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                     @RequestParam(defaultValue = TYPE_ALL) String scope,
                                     @RequestParam(required = false) Long page,
                                     @RequestParam(required = false) String view,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = userService.getByUsername(authentication.getName());
        User reporterUser = userService.getById(reporterId);
        if (reporterUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "报单人不存在");
            return "redirect:/business";
        }

        businessReporterNoteService.saveOrDelete(currentUser.getId(), reporterId, noteName);
        redirectAttributes.addFlashAttribute("successMessage", "备注已保存");
        return "redirect:/business/reporters/" + reporterId + buildQuery(startDate, endDate, scope, page, view);
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

    private String normalizeMobileView(String view) {
        if (MOBILE_VIEW_RECORDS.equalsIgnoreCase(view)) {
            return MOBILE_VIEW_RECORDS;
        }
        if (MOBILE_VIEW_FILTER.equalsIgnoreCase(view)) {
            return MOBILE_VIEW_FILTER;
        }
        return MOBILE_VIEW_OVERVIEW;
    }

    private LocalDateTime defaultOccurredAt() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }

    private String buildQuery(LocalDate startDate, LocalDate endDate, String scope, Long page, String view) {
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;
        String normalizedView = normalizeMobileView(view);
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
            hasParam = true;
        }
        if (page != null && page > 1) {
            query.append(hasParam ? "&" : "?").append("page=").append(page);
            hasParam = true;
        }
        if (!MOBILE_VIEW_OVERVIEW.equals(normalizedView)) {
            query.append(hasParam ? "&" : "?").append("view=").append(normalizedView);
        }
        return query.toString();
    }

    private String resolveReturnTarget(String returnTo) {
        if (StringUtils.hasText(returnTo)
                && returnTo.startsWith("/business")
                && !returnTo.startsWith("//")) {
            return returnTo;
        }
        return DEFAULT_RETURN_TO;
    }

    private String buildReturnToParam(String returnTo) {
        String resolvedReturnTo = resolveReturnTarget(returnTo);
        if (DEFAULT_RETURN_TO.equals(resolvedReturnTo)) {
            return "";
        }
        return "&returnTo=" + UriUtils.encode(resolvedReturnTo, StandardCharsets.UTF_8);
    }

    private String buildReturnToQuery(String returnTo) {
        String resolvedReturnTo = resolveReturnTarget(returnTo);
        if (DEFAULT_RETURN_TO.equals(resolvedReturnTo)) {
            return "";
        }
        return "?returnTo=" + UriUtils.encode(resolvedReturnTo, StandardCharsets.UTF_8);
    }

    private LocalDate getSessionLocalDate(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof LocalDate localDate ? localDate : null;
    }

    private String getSessionString(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof String text ? text : null;
    }

    private boolean hasBusinessScope(String scope) {
        return TYPE_REPORT.equalsIgnoreCase(scope) || TYPE_INBOUND.equalsIgnoreCase(scope) || TYPE_ALL.equalsIgnoreCase(scope);
    }

    private boolean hasBusinessView(String view) {
        return MOBILE_VIEW_OVERVIEW.equalsIgnoreCase(view)
                || MOBILE_VIEW_RECORDS.equalsIgnoreCase(view)
                || MOBILE_VIEW_FILTER.equalsIgnoreCase(view);
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}
