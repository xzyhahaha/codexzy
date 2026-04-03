package com.codexzy.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.util.Locale;

public class MobileAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String MOBILE_TARGET_URL = "/business";
    private static final String DESKTOP_TARGET_URL = "/";

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
            redirectStrategy.sendRedirect(request, response, savedRequest.getRedirectUrl());
            return;
        }

        String targetUrl = isMobile(request.getHeader("User-Agent")) ? MOBILE_TARGET_URL : DESKTOP_TARGET_URL;
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private boolean isMobile(String userAgent) {
        if (userAgent == null) {
            return false;
        }
        String normalized = userAgent.toLowerCase(Locale.ROOT);
        return normalized.contains("android")
                || normalized.contains("iphone")
                || normalized.contains("ipad")
                || normalized.contains("ipod")
                || normalized.contains("mobile")
                || normalized.contains("windows phone");
    }
}
