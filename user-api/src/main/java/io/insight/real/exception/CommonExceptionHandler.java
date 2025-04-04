package io.insight.real.exception;


import io.insight.real.dto.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        HttpStatus status = Optional.ofNullable(HttpStatus.resolve(ex.getStatusCode()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        log.error("class = {}, cause = {}",ex.getClass(),ex.getCause());


        ErrorResponse errorResponse = ErrorResponse.newInstance(ex.getStatusCode(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ResponseData.newInstance(false, null, errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentException(MethodArgumentNotValidException ex) {
        log.error("Unhandled exception occurred : {} ", ex.getMessage(), ex);
        int status = ex.getStatusCode().value();
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        StringBuffer sb = new StringBuffer();
        sb.append(fieldError.getField());
        sb.append(": ");
        sb.append(fieldError.getDefaultMessage());

        ErrorResponse errorResponse = ErrorResponse.newInstance(status, sb.toString());
        return ResponseEntity.status(status)
                .body(ResponseData.newInstance(false, null, errorResponse));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        log.error("Unhandled exception occurred : {} ", ex.getMessage(), ex);

        // 기본 상태 코드 설정
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof HttpStatusCodeException) {
            status = HttpStatus.valueOf(((HttpStatusCodeException) ex).getStatusCode().value());
        }

        ErrorResponse errorResponse = ErrorResponse.newInstance(status.value(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ResponseData.newInstance(false, null, errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        log.error("Unhandled exception occurred : {} ", ex.getMessage(), ex);

        // 기본 상태 코드 설정
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof HttpStatusCodeException) {
            // 예외가 HttpStatusCodeException일 경우, 상태 코드를 추출
            status = HttpStatus.valueOf(((HttpStatusCodeException) ex).getStatusCode().value());
        }

        ErrorResponse errorResponse = ErrorResponse.newInstance(status.value(), ex.getCause().getMessage());
        return ResponseEntity.status(status)
                .body(ResponseData.newInstance(false, null, errorResponse));

    }

}
