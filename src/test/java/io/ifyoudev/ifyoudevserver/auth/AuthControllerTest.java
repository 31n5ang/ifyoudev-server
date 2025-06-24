package io.ifyoudev.ifyoudevserver.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.ifyoudev.ifyoudevserver.core.v1.CookieService;
import io.ifyoudev.ifyoudevserver.core.v1.auth.AuthController;
import io.ifyoudev.ifyoudevserver.core.v1.auth.AuthService;
import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.AuthTokensDto;
import io.ifyoudev.ifyoudevserver.core.v1.auth.dto.EmailAndPasswordDto;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business.InvalidCredentialsException;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import({
        CookieService.class,
})
@AutoConfigureRestDocs
@DisplayName("/api/v1/auth")
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("/token - 성공, 토큰 발급 및 쿠키 설정 확인")
    void authenticateAndCreateToken() throws Exception {
        // given
        final String email = "test@test.com";
        final String password = "test1234";
        final String accessToken = "accessTokenTest";
        final String refreshToken = "refreshTokenTest";
        final String REFRESH_TOKEN_COOKIE = "refreshToken";
        final AuthTokensDto authTokenResponse = new AuthTokensDto(accessToken, refreshToken);

        EmailAndPasswordDto emailAndPasswordDto = new EmailAndPasswordDto(email, password);

        when(authService.authenticateAndCreateTokens(any(EmailAndPasswordDto.class)))
                .thenReturn(authTokenResponse);

        // when: request
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailAndPasswordDto)))

                // then: response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(authTokenResponse.accessToken()))
                .andExpect(cookie().value(REFRESH_TOKEN_COOKIE, refreshToken))
                .andDo(print())
                .andDo(document("auth/token/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token - 실패, 이메일 혹은 비밀번호가 일치하지 않음")
    void authenticateFailedInvalidCredentails() throws Exception {
        // given
        final String email = "test@test.com";
        final String password = "test1234";

        EmailAndPasswordDto emailAndPasswordDto = new EmailAndPasswordDto(email, password);

        when(authService.authenticateAndCreateTokens(any(EmailAndPasswordDto.class)))
                .thenThrow(new InvalidCredentialsException(emailAndPasswordDto.email()));

        // when: request
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailAndPasswordDto)))

                // then: response
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andDo(print())
                .andDo(document("auth/token/fail/invalidCredentials",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token - 실패, 이메일 형식이 잘못됨")
    void authenticateFailedEmailInvalidArgument() throws Exception {
        // given
        final String invalidEmail = "test@";
        final String password = "test1234";

        EmailAndPasswordDto emailAndPasswordDto = new EmailAndPasswordDto(invalidEmail, password);

        // when: request
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailAndPasswordDto)))

                // then: response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[0].field").value("email"))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print())
                .andDo(document("auth/token/fail/EmailInvalidArgument",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token - 실패, 비밀번호 형식이 잘못됨")
    void authenticateFailedPasswordInvalidArgument() throws Exception {
        // given
        final String invalidEmail = "test@test.com";
        final String password = "a ";

        EmailAndPasswordDto emailAndPasswordDto = new EmailAndPasswordDto(invalidEmail, password);

        // when: request
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailAndPasswordDto)))

                // then: response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[0].field").value("password"))
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print())
                .andDo(document("auth/token/fail/PasswordInvalidArgument",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token - 실패, 이메일과 비밀번호 형식이 잘못됨")
    void authenticateFailedEmailAndPasswordInvalidArgument() throws Exception {
        // given
        final String invalidEmail = "test@";
        final String password = "a ";

        EmailAndPasswordDto emailAndPasswordDto = new EmailAndPasswordDto(invalidEmail, password);

        // when: request
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailAndPasswordDto)))

                // then: response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'email')]").hasJsonPath())
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'password')]").hasJsonPath())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(print())
                .andDo(document("auth/token/fail/EmailAndPasswordInvalidArgument",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token/refresh - 성공, 액세스 토큰 재발급")
    void refreshAccessToken() throws Exception {
        // given
        final String refreshToken = "validRefreshToken";
        final String newAccessToken = "newAccessToken";

        final Cookie cookie = new Cookie("refreshToken", refreshToken);

        when(authService.refreshAccessToken(refreshToken))
                .thenReturn(new AuthTokensDto(newAccessToken, refreshToken));

        // when: request
        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))

                // then: response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andDo(print())
                .andDo(document("auth/token/refresh/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token/refresh - 실패, 만료됐거나 유효하지 않은 토큰")
    void refreshAccessTokenFailedInvalidToken() throws Exception {
        // given
        final String invalidRefreshToken = "expiredOrInvalidToken";
        final Cookie cookie = new Cookie("refreshToken", invalidRefreshToken);

        when(authService.refreshAccessToken(invalidRefreshToken))
                .thenThrow(new InvalidTokenException("만료되거나 유효하지 않은 Refresh Token입니다."));

        // when: request
        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))

                // then: response
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("만료되거나 유효하지 않은 Refresh Token입니다."))
                .andDo(print())
                .andDo(document("auth/token/refresh/fail/invalid-token",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/token/refresh - 실패, 서버와 일치하지 않은 토큰")
    void refreshAccessTokenFailedInvalidTokenCompareStoredToken() throws Exception {
        // given
        final String mismatchedRefreshToken = "mismatchedRefreshToken";
        final Cookie cookie = new Cookie("refreshToken", mismatchedRefreshToken);

        when(authService.refreshAccessToken(mismatchedRefreshToken))
                .thenThrow(new InvalidTokenException("서버에 저장된 Refresh Token과 일치하지 않습니다. 다시 로그인 해주세요."));

        // when: request
        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))

                // then: response
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("서버에 저장된 Refresh Token과 일치하지 않습니다. 다시 로그인 해주세요."))
                .andDo(print())
                .andDo(document("auth/token/refresh/fail/mismatch-with-storage",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
