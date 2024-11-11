package com.dmc.config.security;

import com.dmc.domain.member.entity.Member;
import com.dmc.domain.member.enumset.MemberRole;
import com.dmc.common.YN;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomRedirectFilter extends GenericFilterBean {
    private static final String[] EXCLUDED_PATHS = {
            "/user/css/**", "/user/fonts/", "/user/images/", "/user/js/",
            "/login",
            "/swagger-ui/**" , "/v3/api-docs",
            "/api/", "/member/api/", "/error-page/", "/member/join/", "/member/api/nice/"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // 요청 URI가 제외된 경로에 포함될 경우 필터 체인을 계속 진행
        for (String excludedPath : EXCLUDED_PATHS) {
            if (requestURI.startsWith(excludedPath)) {
                chain.doFilter(request, response);
                return;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Member member
                && member.getRole() != MemberRole.USER
                && member.getFirstLoginYn() == YN.N && !httpResponse.isCommitted()) {
                httpResponse.sendRedirect("/member/api/nice/company/popup/open");
                return;
            }

        chain.doFilter(request, response);
    }
}
