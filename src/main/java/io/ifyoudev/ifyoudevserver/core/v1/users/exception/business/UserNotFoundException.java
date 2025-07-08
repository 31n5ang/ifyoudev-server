package io.ifyoudev.ifyoudevserver.core.v1.users.exception.business;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String detail) {
        super(
                detail,
                HttpStatus.NOT_FOUND,
                URI.create("about:blank"),
                "User Not Found"
        );
    }
}
