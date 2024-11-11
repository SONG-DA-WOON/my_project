package kr.co.steellink.user.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.ConstraintViolationException;

import kr.co.steellink.user.util.RequestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.hasText;

/**
 * API 표준 응답 객체
 *
 * @param timestamp 요청 시간
 * @param status HTTP 상태 코드
 * @param message status=200: OK, 이외: 오류 메시지
 * @param path 요청 경로
 * @param data 응답 데이터
 * @param <T> 응답 데이터 타입
 * @author TAEROK HWANG
 */
public record ApiResponse<T>(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime timestamp,

        HttpStatus status,

        String message,

        String path,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data) {

    public ApiResponse {
        timestamp = LocalDateTime.now();

        if (status == null)
            status = HttpStatus.OK;

        if (!status.is2xxSuccessful())
            throw new IllegalArgumentException("status must be 2xx");

        if (!hasText(message))
            message = status.getReasonPhrase();

        path = RequestUtils.getPath();
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(null, HttpStatus.OK, null, null, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(null, HttpStatus.OK, null, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(null, HttpStatus.OK, message, null, data);
    }

    public static ApiErrorResponse error(HttpStatus status, String message) {
        return new ApiErrorResponse(status, message);
    }

    public static ApiErrorResponse error(HttpStatus status, String message, MethodArgumentNotValidException ex) {
        return new ApiErrorResponse(status, message, ex);
    }

    public static ApiErrorResponse error(HttpStatus status, String message, InvalidFormatException ex) {
        return new ApiErrorResponse(status, message, ex);
    }

    public static ApiErrorResponse error(HttpStatus status, String message, ConstraintViolationException ex) {
        return new ApiErrorResponse(status, message, ex);
    }

    @JsonGetter("status")
    public Integer statusValue() {
        return this.status.value();
    }

}
