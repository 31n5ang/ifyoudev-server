package io.ifyoudev.ifyoudevserver.core.v1.auth.exception;

public class EmailNotFoundException extends AuthenticationException {
    public EmailNotFoundException() {
    }

    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFoundException(Throwable cause) {
        super(cause);
    }
}
