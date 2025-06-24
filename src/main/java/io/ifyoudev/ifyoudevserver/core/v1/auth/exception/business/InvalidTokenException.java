package io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class InvalidTokenException extends BusinessException {

    private static final URI TYPE = URI.create("https://ifyoudev.io/problems/invalid-token");
    private static final String TITLE = "Invalid Token";

    public InvalidTokenException(String detail) {
        super(
                detail,
                HttpStatus.UNAUTHORIZED,
                TYPE,
                TITLE
        );
    }
}
