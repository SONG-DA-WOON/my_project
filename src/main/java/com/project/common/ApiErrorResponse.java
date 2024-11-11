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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;

/**
 * API 표준 오류 응답 객체
 *
 * @param timestamp 요청 시간
 * @param status HTTP 상태 코드
 * @param error HTTP 상태 문자열
 * @param message status=200: OK, 이외: 오류 메시지
 * @param path 요청 경로
 * @param errors Validation 실패 시 오류 내용 리스트
 * @author TAEROK HWANG
 */
public record ApiErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime timestamp,
        HttpStatus status,

        String error,

        String message,

        String path,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ApiFieldError> errors) {

    public ApiErrorResponse {
        timestamp = LocalDateTime.now();

        if (status == null)
            throw new IllegalArgumentException("status must not be null");

        if (!status.is4xxClientError() && !status.is5xxServerError())
            throw new IllegalArgumentException("status must be 4xx or 5xx");

        error = status.getReasonPhrase();

        if (!hasText(message))
            throw new IllegalArgumentException("message must not be empty");

        path = RequestUtils.getPath();
    }

    public ApiErrorResponse(HttpStatus status, String message) {
        this(null, status, null, message, null, null);
    }

    public ApiErrorResponse(HttpStatus status, String message, MethodArgumentNotValidException ex) {
        this(null, status, null, message, null, ApiFieldError.createFieldErrors(ex));
    }

    public ApiErrorResponse(HttpStatus status, String message, InvalidFormatException ex) {
        this(null, status, null, message, null, ApiFieldError.createFieldErrors(ex));
    }

    public ApiErrorResponse(HttpStatus status, String message, ConstraintViolationException ex) {
        this(null, status, null, message, null, ApiFieldError.createFieldErrors(ex));
    }

    @JsonGetter("status")
    public Integer getStatusValue() {
        return this.status.value();
    }

    public record ApiFieldError(String message, String field, Object rejectedValue) {
        public static List<ApiFieldError> createFieldErrors(MethodArgumentNotValidException ex) {
            if (ex == null) return null;

            return ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ApiFieldError(
                            error.getDefaultMessage(),
                            error.getField(),
                            error.getRejectedValue()))
                    .toList();
        }

        private static final Pattern ENUM_MESSAGE =
                Pattern.compile("not one of the values accepted for Enum class: (\\[.*])");

        public static List<ApiFieldError> createFieldErrors(InvalidFormatException ex) {
            if (ex == null) return null;

            Matcher matcher = ENUM_MESSAGE.matcher(ex.getMessage());
            final String message = matcher.find() ? "다음 값 중 하나여야 합니다: "
                    + matcher.group(1).replace("NONE, ", "") : ex.getMessage();

            return ex.getPath().stream()
                    .map(path -> new ApiFieldError(message, path.getFieldName(), ex.getValue()))
                    .toList();
        }

        public static List<ApiFieldError> createFieldErrors(ConstraintViolationException ex) {
            if (ex == null) return null;

            return ex.getConstraintViolations().stream()
                    .map(violation -> new ApiFieldError(
                            violation.getMessage(),
                            violation.getPropertyPath().toString(),
                            violation.getInvalidValue()))
                    .toList();
        }
    }

}
