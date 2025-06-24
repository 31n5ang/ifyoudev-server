package io.ifyoudev.ifyoudevserver.core.v1.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.JwtTokenVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenVerifier {

    private final JWTVerifier verifier;
    private final Algorithm jwtAlgorithm;

    public JwtTokenVerifier(Algorithm jwtAlgorithm) {
        this.jwtAlgorithm = jwtAlgorithm;
        this.verifier = JWT.require(jwtAlgorithm).build();
    }

    /**
     * JWT 토큰을 검증하고, 성공적으로 검증되면 해당 토큰의 디코딩된 정보(클레임)를 담은 {@link DecodedJwtToken} 객체를 반환합니다.
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효하게 검증된 토큰의 정보를 담은 {@link DecodedJwtToken} 객체
     * @throws io.ifyoudev.ifyoudevserver.core.v1.auth.exception.JwtTokenVerificationException 토큰 검증에 실패했을 경우.
     */
    public DecodedJwtToken verifyAndDecode(String token) {
        return new DecodedJwtToken(verify(token));
    }

    private DecodedJWT verify(String token) {
        try {
            return verifier.verify(token);
        } catch (RuntimeException ex) {
            throw new JwtTokenVerificationException("JWT 토큰 검증에 실패했습니다.", ex);
        }
    }
}
