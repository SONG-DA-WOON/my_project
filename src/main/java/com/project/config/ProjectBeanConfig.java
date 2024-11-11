package com.dmc.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class ProjectBeanConfig {
    /**
     * 비밀번호 암호화, 복호화를 위한 Bean 등록<br />
     * {@link PasswordEncoder}를 Autowired 받아서 사용
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JPA Auditing(등록자, 수정자 자동 삽입)을 위한 Bean 등록<br />
     * {@link EnableJpaAuditing} annotation 필요
     *
     * @author Tae-rok Hwang
     * @return AuditorAware<String>
     */
    @Bean
    public AuditorAware<String> auditProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String auditorId = authentication != null ? authentication.getName() : "anonymousUser";

            return Optional.of(auditorId);
        };
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * QueryDSL RequireAllArgsConstructor 사용을 위한 설정
     *
     * @return JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
