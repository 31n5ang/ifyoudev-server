package io.ifyoudev.ifyoudevserver.core.v1.posts.exception;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class PostCreateException extends BusinessException {
    public PostCreateException(String detail) {
        super(
                detail,
                HttpStatus.INTERNAL_SERVER_ERROR,
                URI.create("about:blank"),
                "Post Create Failed"
        );
    }
}
