package io.insight.real.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Description :  오류 메세지 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private Integer code;
    private String message;

    public static ErrorResponse newInstance(Integer code, String message) {
        ErrorResponse instance = new ErrorResponse();

        instance.code = code;
        instance.message = message;
        return instance;
    }
}
