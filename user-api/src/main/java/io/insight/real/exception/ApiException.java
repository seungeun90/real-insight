package io.insight.real.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
    private final String messageCode;
    private final String[] args;
    private final int statusCode;

    public ApiException(ServiceErrorMessage errorMessage) {
        super(errorMessage.getMessageCode());
        this.messageCode = errorMessage.getMessageCode();
        this.args = errorMessage.getArgs();
        this.statusCode = errorMessage.getErrorCode();
    }

}