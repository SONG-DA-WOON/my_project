package com.dmc.config.util;

import com.dmc.config.JwtConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

/**
 * JWT 유틸리티 클래스<br />
 * com.nimbusds.jose 라이브러리 사용 (spring-boot-starter-oauth2-client 에서 사용하는 라이브러리)
 *
 * @see JwtConfig
 * @since 2023-06-27
 * @author Tae-rok Hwang
 */
@Slf4j
@Service
@EnableConfigurationProperties(JwtConfig.class) // 설정 값 자동 바인딩
@ConditionalOnProperty(value = "jwt.secret-key") // 설정 값이 있을 때만 Bean 등록
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    public TokenBuilder builder() {
        return new TokenBuilder();
    }

    /**
     * 토큰 생성<br />
     * 토큰 만료 시간을 설정하지 않으면 기본 값은 jwt.expiration-time 값을 읽는다. (기본 값: 10분)
     *
     * @see #createToken(Map, Long)
     * @param payload 토큰에 담을 정보 (body)
     * @return String JSON Web Token
     * @author Tae-rok Hwang
     */
    public String createToken(Map<String, Object> payload) {
        return this.createToken(payload, jwtConfig.getExpirationTime().toSeconds());
    }

    /**
     * 토큰 생성
     *
     * @param payload 토큰에 담을 정보 (body)
     * @param expirationSeconds 토큰 만료 시간 (단위: 초)
     * @return String JSON Web Token
     * @author Tae-rok Hwang
     */
    public String createToken(Map<String, Object> payload, Long expirationSeconds) {
        Date now = new Date();

        JWTClaimsSet.Builder jwtBuilder = new JWTClaimsSet.Builder()
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + expirationSeconds * 1000));

        // Payload 추가
        for (String key : payload.keySet())
            jwtBuilder.claim(key, payload.get(key));

        JWTClaimsSet claimsSet = jwtBuilder.build();

        // JWT 문자열 빌드 및 서명
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS384).build(), claimsSet);
        try {
            jwt.sign(new MACSigner(jwtConfig.getSecretKey()));
        } catch (JOSEException e) {
            log.error("JWT 서명 암호키 길이 부족 (48자 이상): ", e);
            return null;
        }

        return jwt.serialize();
    }

    private SignedJWT parseToken(String token) {
        if (!hasText(token)) return null;

        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            log.warn("JWT 토큰 파싱 실패: ", e);
        }

        return null;
    }

    /**
     * JWT 토큰 검증
     *
     * @param token JSON Web Token
     * @return boolean 검증 결과
     * @author Tae-rok Hwang
     */
    public boolean verifyToken(String token) {
        try {
            Date now = new Date();
            SignedJWT signedJWT = this.parseToken(token);
            if (signedJWT == null) return false;

            // 서명 길이 검증 (HS384 알고리즘의 경우 64자)
            if (signedJWT.getSignature().toString().length() != 64) return false;

            // 토큰 서명 검증
            if (!signedJWT.verify(new MACVerifier(jwtConfig.getSecretKey()))) return false;

            // 토큰 만료 검증
            return now.before(signedJWT.getJWTClaimsSet().getExpirationTime());
        } catch (ParseException e) {
            log.warn("JWT 토큰 파싱 실패: ", e);
        } catch (JOSEException e) {
            log.warn("JWT 토큰 서명 검증 실패: ", e);
        }

        return false;
    }


    /**
     * JWT 토큰 복호화
     *
     * @param token JSON Web Token
     * @return Map<String, Object> 토큰 정보가 담긴 Map
     * @author Tae-rok Hwang
     */
    public Map<String, Object> getPayload(String token) {
        SignedJWT signedJWT = this.parseToken(token);
        if (signedJWT == null) return new HashMap<>();

        JWTClaimsSet claimsSet;
        try {
            claimsSet = signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            log.warn("JWT 토큰 파싱 실패: ", e);
            return new HashMap<>();
        }

        return claimsSet.getClaims();
    }

    /**
     * JWT 토큰 빌더 클래스
     */
    public class TokenBuilder {

        private final Map<String, Object> payload = new HashMap<>();
        private Long expirationSeconds;

        /**
         * 만료 시간 설정
         *
         * @param expirationSeconds 만료 시간 (단위: 초)
         * @return TokenBuilder (Method Chaining)
         * @author Tae-rok Hwang
         */
        public TokenBuilder expirationTime(Long expirationSeconds) {
            this.expirationSeconds = expirationSeconds;
            return this;
        }

        /**
         * 토큰에 담을 정보 추가
         *
         * @param key 키 이름
         * @param value 키 값
         * @return TokenBuilder (Method Chaining)
         * @author Tae-rok Hwang
         */
        public TokenBuilder claim(String key, Object value) {
            payload.put(key, value);
            return this;
        }

        /**
         * 토큰 생성
         *
         * @return String JSON Web Token
         * @author Tae-rok Hwang
         */
        public String build() {
            if (expirationSeconds != null)
                return createToken(payload, expirationSeconds);
            else
                return createToken(payload);
        }
    }

}
