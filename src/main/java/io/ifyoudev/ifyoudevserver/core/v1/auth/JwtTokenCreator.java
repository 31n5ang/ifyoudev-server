package io.ifyoudev.ifyoudevserver.core.v1.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenCreator {

    private final JwtTokenProperties jwtTokenProperties;
    private final Algorithm jwtAlgorithm;

    public JwtTokenCreator(JwtTokenProperties jwtTokenProperties, Algorithm jwtAlgorithm) {
        this.jwtTokenProperties = jwtTokenProperties;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public String createAccessToken(AuthUser authUser) {
        return create(authUser, jwtTokenProperties.getAccessExpiresInSeconds());
    }

    public String createRefreshToken(AuthUser authUser) {
        return create(authUser, jwtTokenProperties.getRefreshExpiresInSeconds());
    }

    public String create(AuthUser authUser, long expirationSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        return JWT.create()
                .withSubject(authUser.getUserUuid())
                .withClaim("roles", authUser.getAuthorityNames())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(jwtAlgorithm);
    }
}
