package io.ifyoudev.ifyoudevserver.core.v1.auth;

import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.AuthTokensDto;
import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.EmailAndPasswordDto;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.JwtTokenVerificationException;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business.InvalidCredentialsException;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business.InvalidTokenException;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.tables.pojos.Users;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenCreator jwtTokenCreator;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final RefreshTokenStorage refreshTokenStorage;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;

    public AuthTokensDto refreshAccessToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException("Refresh Token이 존재하지 않습니다.");
        }

        DecodedJwtToken decodedJwtToken = decodeAndVerifyToken(refreshToken);

        validateRefreshTokenInStorage(decodedJwtToken.getUserUuid(), refreshToken);

        Users user = userRepository.findOneByUuid(decodedJwtToken.getUserUuid())
                .orElseThrow(() -> new InvalidTokenException("해당 사용자가 존재하지 않습니다."));

        List<String> roleNames = userRepository.findRoleNamesByUuid(decodedJwtToken.getUserUuid());

        AuthUser authUser = AuthUser.createWithUserUuid(user.getUuid(), roleNames);

        String newAccessToken = jwtTokenCreator.createAccessToken(authUser);

        return new AuthTokensDto(newAccessToken, refreshToken);
    }

    private DecodedJwtToken decodeAndVerifyToken(String tokenValue) {
        try {
            return jwtTokenVerifier.verifyAndDecode(tokenValue);
        } catch (JwtTokenVerificationException ex) {
            throw new InvalidTokenException("만료되거나 유효하지 않은 Refresh Token입니다.");
        }
    }

    private void validateRefreshTokenInStorage(String userUuid, String refreshToken) {
        String storedRefreshToken = refreshTokenStorage.findByUserUuid(userUuid)
                .orElseThrow(() -> new InvalidTokenException("해당 사용자에 대한 Refresh Token이 존재하지 않습니다. 다시 로그인 해주세요."));

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException("서버에 저장된 Refresh Token과 일치하지 않습니다. 다시 로그인 해주세요.");
        }
    }

    /**
     * Email, Password를 인증하고 로그인하는 비즈니스 로직입니다.
     */
    public AuthTokensDto authenticateAndCreateTokens(EmailAndPasswordDto emailAndPasswordDto) {
        Authentication authentication = authenticate(emailAndPasswordDto);
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AuthTokensDto authTokensDto = createTokens(authUser);
        storeRefreshToken(authUser.getUserUuid(), authTokensDto.refreshToken());
        return authTokensDto;
    }

    private Authentication authenticate(EmailAndPasswordDto emailAndPasswordDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(emailAndPasswordDto.email(), emailAndPasswordDto.password());
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException ex) {
            log.warn("로그인 '{}', 인증 실패", emailAndPasswordDto.email(), ex);
            throw new InvalidCredentialsException(emailAndPasswordDto.email());
        }
    }

    private AuthTokensDto createTokens(AuthUser authUser) {
        userRepository.findOneByUuid(authUser.getUserUuid())
                .orElseThrow(() -> new InvalidTokenException("해당 사용자가 존재하지 않습니다."));
        String accessToken = jwtTokenCreator.createAccessToken(authUser);
        String refreshToken = jwtTokenCreator.createRefreshToken(authUser);
        return new AuthTokensDto(accessToken, refreshToken);
    }

    private void storeRefreshToken(String userUuid, String tokenValue) {
        refreshTokenStorage.update(userUuid, tokenValue);
        log.debug("[{}] Refresh Token 업데이트됨", userUuid);
    }
}
