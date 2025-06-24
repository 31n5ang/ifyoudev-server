package io.ifyoudev.ifyoudevserver.core.v1.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application-{profile}.yml에서 설정한 JWT 토큰 설정 정보를 제공합니다.
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtTokenProperties {
    private String secretKey;
    private long accessExpiresInSeconds;
    private long refreshExpiresInSeconds;
}
