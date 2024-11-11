package com.dmc.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class MemberSecurityConfig {

//    private final MemberAuthenticationProvider memberAuthenticationProvider;
//    private final PrincipalOauth2UserService principalOauth2UserService;
//    private final PrincipalOidcUserService principalOidcUserService;
//    private final MemberRepository memberRepository;
//
//    /**
//     * 회원 | 로그인 Filter Chain 설정
//     *
//     * @param http HttpSecurity
//     * @return SecurityFilterChain
//     * @throws Exception 예외가 발생한 경우
//     * @author 송다운
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authenticationProvider(memberAuthenticationProvider)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/user/css/**", "/user/fonts/**", "/user/images/**", "/user/js/**", "/join/**", "/search/**").permitAll()
//                        .requestMatchers("/login/**", "/logout", "/api/**", "/purchase/**", "/find/**", "/uploads/**").permitAll()
//                        .requestMatchers("/product/**", "/oauth2/**", "/test/**").permitAll()
//                        .requestMatchers("/board/**", "/event/**", "/service**", "/map**", "/sell/intro", "/review/**").permitAll()
//                        .anyRequest().hasRole("USER")
//                )
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                        .failureHandler(memberAuthenticationFailureHandler())
//                        .successHandler(memberAuthenticationSuccessHandler())
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")
//                        .tokenEndpoint(token -> token
//                                .accessTokenResponseClient(accessTokenResponseClient()))
//                        .redirectionEndpoint(redirect -> redirect
//                                .baseUri("/login/oauth2/code/*")
//                        )
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(principalOauth2UserService)
//                                .oidcUserService(principalOidcUserService))
//                        .successHandler(memberAuthenticationSuccessHandler())
//                        .failureHandler(memberAuthenticationFailureHandler()))
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login")
//                );
//        return http.build();
//    }
//
//    /**
//     * 애플 로그인 > 토큰 검증
//     *
//     * @author SungHa
//     */
//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
//        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
//        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter());
//
//        return accessTokenResponseClient;
//    }
//
    /**
     * 회원 | 성공 핸들러
     *
     * @author 송다운
     */
//    public AuthenticationSuccessHandler memberAuthenticationSuccessHandler() {
//        return (request, response, authentication) -> {
//            // 로그인 오류 메시지 제거
//            HttpSession session = request.getSession();
//            session.removeAttribute("errorMessage");
//
//            // 로그인 날짜
//            Member member = memberRepository.findByLoginIdAndStatusCode(authentication.getName(), MemberStatus.NORMAL);
//            member.setLastLoginAt(LocalDateTime.now());
//            memberRepository.save(member);
//
//            if ("N".equals(member.getAuthYn().getYn()) && member.getRegCode() != RegType.EMAIL) {
//                response.sendRedirect("/join/sns");
//            } else {
//                response.sendRedirect("/");
//            }
//        };
//    }
//
    /**
     * 회원 | 실패 핸들러
     *
     * @author 송다운
     */
//    public AuthenticationFailureHandler memberAuthenticationFailureHandler() {
//        return (request, response, exception) -> {
//            HttpSession session = request.getSession();
//            String requestURI = request.getRequestURI();
//            String[] splitRequestURI = requestURI.split("/");
//
//            // sns 로그인 중복 체크
//            if (splitRequestURI.length == 5) {
//                session.setAttribute("errorMessage", exception.getMessage());
//                response.sendRedirect("/login");
//            } else {
//                session.setAttribute("errorMessage", exception.getMessage());
//                response.sendRedirect("/login/email");
//            }
//        };
//    }
}
