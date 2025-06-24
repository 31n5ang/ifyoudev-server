package io.ifyoudev.ifyoudevserver.common;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.business.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(BusinessException ex) {
        return new ResponseEntity<>(ex.getBody(), ex.getStatusCode());
    }

    /**
     * 요청 본문의 Bean Validation이 통과하지 못할 경우 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "요청 본문의 유효성 검사에 실패했습니다."
        );
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Request Validation Error");
        List<Map<String, String>> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(err -> {
                    Map<String, String> errMap = new HashMap<>();
                    if (err instanceof FieldError) {
                        errMap.put("field", ((FieldError) err).getField());
                    }
                    errMap.put("message", err.getDefaultMessage());
                    return errMap;
                })
                .collect(Collectors.toList());
        Map<String, Object> properties = new HashMap<>();
        properties.put("errors", errors);
        properties.put("timestamp", Instant.now());
        problemDetail.setProperty("properties", properties);
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    /**
     * 요청 본문을 정상적으로 읽을 수 없을 경우 처리합니다. (보통 JSON 문법 오류)
     * @author 31n5ang
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "요청 본문을 정상적으로 읽을 수 없습니다. 보통 본문 JSON의 문법 오류로 발생합니다."
        );
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Http Message Not Readable Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }
}
