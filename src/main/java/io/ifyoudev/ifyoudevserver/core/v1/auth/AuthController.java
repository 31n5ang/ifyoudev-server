package io.ifyoudev.ifyoudevserver.core.v1.auth;

import io.ifyoudev.ifyoudevserver.core.v1.CookieService;
import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.AuthTokensDto;
import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.EmailAndPasswordDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/token")
    public ResponseEntity<?> authentication(
            HttpServletResponse response,
            @Valid @RequestBody EmailAndPasswordDto emailAndPasswordDto
    ) {
        AuthTokensDto authTokensDto = authService.authenticateAndCreateTokens(emailAndPasswordDto);
        cookieService.addRefreshTokenCookie(response, authTokensDto.refreshToken());
        return ResponseEntity.ok(authTokensDto);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        AuthTokensDto authTokensDto = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(authTokensDto);
    }
}
