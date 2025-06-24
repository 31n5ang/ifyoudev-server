package io.ifyoudev.ifyoudevserver.core.v1.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 외부 라이브러리를 감싸서 편리하게 사용하기 위한 목적의 객체입니다.
 * @see DecodedJWT
 */
@Getter
@ToString
public class DecodedJwtToken {

    private final String userUuid;
    private final List<String> authorityNames;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final String tokenValue;

    public DecodedJwtToken(String userUuid, List<String> authorityNames, LocalDateTime issuedAt,
                           LocalDateTime expiresAt,
                           String tokenValue) {
        this.userUuid = userUuid;
        this.authorityNames = authorityNames;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.tokenValue = tokenValue;
    }

    public DecodedJwtToken(DecodedJWT decodedJWT) {
        this.userUuid = decodedJWT.getSubject();
        this.issuedAt = decodedJWT.getIssuedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.authorityNames = decodedJWT.getClaim("roles").asList(String.class);
        this.expiresAt = decodedJWT.getExpiresAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        this.tokenValue = decodedJWT.getToken();
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
