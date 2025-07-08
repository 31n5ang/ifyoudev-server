package io.ifyoudev.ifyoudevserver.core.v1.posts.exception;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException(String detail) {
        super(
                detail,
                HttpStatus.NOT_FOUND,
                URI.create("about:blank"),
                "Post Not Found"
        );
    }
}
