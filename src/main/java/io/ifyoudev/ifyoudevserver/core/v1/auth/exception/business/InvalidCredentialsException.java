package io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class InvalidCredentialsException extends BusinessException {

    private static final URI TYPE = URI.create("https://ifyoudev.io/problems/invalid-credentials");
    private static final String TITLE = "Invalid Credentials";

    public InvalidCredentialsException(String email) {
        super(
                "사용자 이메일 '" + email + "' 혹은 비밀번호가 일치하지 않습니다.",
                HttpStatus.UNAUTHORIZED,
                TYPE,
                TITLE
        );
    }
}
