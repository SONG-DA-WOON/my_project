package com.dmc.config.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/login")
    public String adminLogin(HttpSession session , Model model) {
        String errorMessage = (String) session.getAttribute("errorMessage");

        // 로그인 실패 오류 메시지
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        return "member/login";
    }
}
