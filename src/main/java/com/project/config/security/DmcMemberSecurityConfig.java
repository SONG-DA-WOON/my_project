package com.dmc.config.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class DmcMemberSecurityConfig {

    private final MemberAuthenticationProvider memberAuthenticationProvider;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Value("${server.servlet.session.cookie.name}")
    private String sessionCookieName;


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(memberAuthenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(this.memberAuthenticationFailureHandler())
                        .successHandler(this.memberAuthenticationSuccessHandler())
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies(sessionCookieName)
                        .permitAll()
                )
                .addFilterBefore(new CustomRedirectFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public AuthenticationSuccessHandler memberAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // 로그인 오류 메시지 제거
            HttpSession session = request.getSession();
            session.removeAttribute("errorMessage");

            // 로그인한 회원 정보
//            Member member = (Member) authentication.getPrincipal();
//
//            if (Boolean.FALSE.equals(member.isFirstLoginYn())){
//                response.sendRedirect("/member/api/nice/company/popup/open");
//            } else {
//                // 로그인 전 페이지로 이동
//                SavedRequest savedRequest = requestCache.getRequest(request, response);
//
//                response.sendRedirect("/");
//            }
            response.sendRedirect("/");
        };
    }

    public AuthenticationFailureHandler memberAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            HttpSession session = request.getSession();

            session.setAttribute("errorMessage", exception.getMessage());
            response.sendRedirect("/login");
        };
    }

}
