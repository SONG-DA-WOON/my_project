package com.dmc.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.util.StringUtils.hasText;

@ControllerAdvice
@RequiredArgsConstructor
public class ProjectModelAttribute {

    /**
     * 현재 페이지의 정보를 담고 있는 ServletUriComponentsBuilder 객체 반환
     *
     * @return ServletUriComponentsBuilder 객체
     * @author TAEROK HWANG
     */
    @ModelAttribute("uriBuilder")
    public ServletUriComponentsBuilder getUriBuilder() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }

    /**
     * 현재 페이지의 URI 반환
     *
     * @param request HttpServletRequest
     * @return 요청한 URI
     * @author TAEROK HWANG
     */
    @ModelAttribute("requestURI")
    public String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 현재 페이지의 QueryString 반환
     *
     * @param request HttpServletRequest
     * @return 요청 파라미터
     * @author TAEROK HWANG
     */
    @ModelAttribute("queryString")
    public String getQueryString(HttpServletRequest request) {
        return request.getQueryString();
    }

    /**
     * 현재 페이지의 QueryString 에 담겨있는 값 반환 (q=XXX)
     *
     * @param request HttpServletRequest
     * @return 요청 QueryString 값
     * @author TAEROK HWANG
     */
    @ModelAttribute("queryStringValue")
    public String getQueryStringValue(HttpServletRequest request) {
        String queryString = request.getParameter("q");

        if (!hasText(queryString))
            return "";

        return "?" + queryString;
    }

    /**
     * 현재 페이지의 referer (이전 페이지) 반환
     *
     * @param request HttpServletRequest
     * @return 요청 referer 값 (도메인이 일치하지 않거나 로그인 페이지라면 null 반환)
     * @author TAEROK HWANG
     */
    @ModelAttribute("referer")
    public String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("referer");

        // 로그인 관련 페이지에서 이동한 경우 referer 반환하지 않음
        if (referer != null && referer.contains("/login"))
            return null;

        // 도메인이 일치할 경우에만 referer 반환
        if (referer != null && referer.startsWith(request.getScheme() + "://" + request.getServerName()))
            return referer;

        return null;
    }
}
