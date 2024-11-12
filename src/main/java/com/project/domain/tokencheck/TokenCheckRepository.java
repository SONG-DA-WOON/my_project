package com.project.domain.tokencheck;


import com.project.domain.tokencheck.entity.TokenCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TokenCheckRepository extends JpaRepository<TokenCheck, Long>,
        QuerydslPredicateExecutor<TokenCheck>,
        TokenCheckRepositoryCustom {
    TokenCheck findByJwtToken(String token);
}
