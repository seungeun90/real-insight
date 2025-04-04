package io.insight.real.dto.response;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Description : API 응답 표준화
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ResponseData<T> {
    private boolean success;
    private T data;
    private ErrorResponse error;
    private Pagination pagination;

    public static <T> ResponseData<T> newInstance(boolean success, T data, ErrorResponse error) {
        ResponseData<T> instance = new ResponseData<>();
        instance.success = success;
        instance.data = data;
        instance.error = error;
        instance.pagination = null;
        return instance;
    }
    public static <T> ResponseData<T> newInstance(boolean success, T data, ErrorResponse error, Pagination pagination) {
        ResponseData<T> instance = new ResponseData<>();
        instance.success = success;
        instance.data = data;
        instance.error = error;
        instance.pagination = pagination;
        return instance;
    }
    public static <T> ResponseEntity<?> created(T data) {
        return success(data, HttpStatus.CREATED, true, null);
    }

    public static <T> ResponseEntity<?> deleted() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<?> success(T data) {
        return success(data, HttpStatus.OK,true, null);
    }

    public static <T> ResponseEntity<?> success(T data, @Nullable Pagination pagination) {
        return success(data, HttpStatus.OK,true, pagination);
    }

    public static <T> ResponseEntity<?> success(T data, HttpStatus status, boolean wrapper, @Nullable Pagination pagination) {
        return ResponseEntity.status(status).body(wrapper ? newInstance(true, data, null, pagination) : data);
    }

    public static ResponseEntity<?> error(ErrorResponse error) {
        return ResponseEntity.status(error.getCode()).body(error);
    }

    public static ResponseEntity<?> error(Integer code, String message) {
        return error(ErrorResponse.newInstance(code, message));
    }


}
