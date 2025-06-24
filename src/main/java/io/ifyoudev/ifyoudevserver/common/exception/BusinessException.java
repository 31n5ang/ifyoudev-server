package io.ifyoudev.ifyoudevserver.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.net.URI;

@Getter
public abstract class BusinessException extends RuntimeException implements ErrorResponse {

    protected final HttpStatus status;
    protected final URI type;
    protected final String title;
    protected final String detail;

    public BusinessException(String detail, HttpStatus status, URI type, String title) {
        super(detail);
        this.detail = detail;
        this.status = status;
        this.type = type;
        this.title = title;
    }

    public BusinessException(String detail) {
        this(detail, HttpStatus.INTERNAL_SERVER_ERROR, URI.create("about:blank"), "서버 내부 오류");
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return this.status;
    }

    @Override
    public ProblemDetail getBody() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
