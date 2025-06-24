package io.ifyoudev.ifyoudevserver.core.v1.auth.exception;

public class JwtTokenVerificationException extends RuntimeException {
    public JwtTokenVerificationException() {
    }

    public JwtTokenVerificationException(String message) {
        super(message);
    }

    public JwtTokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenVerificationException(Throwable cause) {
        super(cause);
    }
}
