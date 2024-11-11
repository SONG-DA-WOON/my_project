package com.dmc.config;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("jwt")
@Getter
@Validated
public class JwtConfig {

    @Length(min = 32)
    private final String secretKey;

    private final Duration expirationTime;


    @ConstructorBinding // Spring Boot 3.0 미만 필수 (생성자 자동 바인딩)
    public JwtConfig(String secretKey, @DefaultValue("10m") Duration expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }
}
