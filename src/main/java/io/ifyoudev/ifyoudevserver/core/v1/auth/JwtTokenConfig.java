package io.ifyoudev.ifyoudevserver.core.v1.auth;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT Token 관련 클래스에서 사용할 정보를 빈으로 등록합니다.
 * @author 31n5ang
 */
@Configuration
public class JwtTokenConfig {

    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenConfig(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;
    }

    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC512(jwtTokenProperties.getSecretKey());
    }
}
