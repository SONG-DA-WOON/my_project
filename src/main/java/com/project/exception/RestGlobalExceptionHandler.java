package com.dmc.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * api  공통 예외처리
 * @see HtmlErrorControllerAdvice 충돌 방지 -> basePackages에 경로 추가 {"com.dmc.web.member.api" , "..."}
 */
@Slf4j
@RestControllerAdvice(basePackages = {
        "com.dmc.web.member.api" ,
        "com.dmc.web.category.api" ,
        "com.dmc.web.onlinerecruitmentcenter.dmc500.api",
        "com.dmc.web.onlinerecruitmentcenter.matchingday.api",
        "com.dmc.web.onlinerecruitmentcenter.dmc3.api",
        "com.dmc.web.curriculumvitae.api"
}
)
@Order(1)
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex,
                                                                               WebRequest request) {
        log.warn("{} [{}] {}", ex.getClass().getSimpleName(), HttpStatus.BAD_REQUEST, ex.getMessage());

        // 예외 메시지를 ResponseDto에 담아서 반환
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * [MethodArgumentNotValidException] 예외 처리 - {@code 400} <br />
     * 유효성 체크 예외 처리

     * {@link com.dmc.domain.member.dto.MemberEnterPriceSaveDto}

     * 예외 처리 순서를 위해  message 에 번호 부여
     * ->  message = 11제목을 입력해주세요 , message = 12내용을 입력해주세요

     * @return  HttpStatus.BAD_REQUEST.code , error message
     * @author Dong-Joon Oh
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("{} [{}] {}", ex.getClass().getSimpleName(), HttpStatus.BAD_REQUEST, ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();

        if (ex.hasErrors()) {
            List<FieldError> errorList = ex.getFieldErrors();
            for (FieldError error : errorList) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }

        if (!errors.isEmpty()) {
            Map<String, String> sortedErrors = errors.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                String value = entry.getValue();
                                if (value.length() >= 2) {
                                    return value.substring(2);
                                } else {
                                    return value;
                                }
                            },
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new
                    ));
            validationErrors.put("errorMessage", sortedErrors.entrySet().iterator().next().getValue());
        }

        // ResponseDto 상태 코드와 오류 메시지를 함께 담아 반환
        ResponseDto<Map<String, String>> responseDto = new ResponseDto<>(
                HttpStatus.BAD_REQUEST.value(), validationErrors.get("errorMessage"));

        // ResponseEntity<Object>로 반환
        return new ResponseEntity<>(responseDto, headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 예외 처리 - {@code 404 Not Found} <br />
     * 해당 엔티티 객체를 찾을 수 없는 경우 발생하는 예외를 처리하는 핸들러
     * {@link ResourceNotFoundException}
     * @author Dong-Joon Oh
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                            WebRequest webRequest) {
        log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.NOT_FOUND, exception.getMessage());

        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,
                                                                  WebRequest webRequest) {
       log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDTO);
    }

    // Thymeleaf 템플릿 관련 예외 처리
//    @ExceptionHandler(TemplateInputException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<ErrorResponseDto> handleTemplateInputException(TemplateInputException exception,
//                                                                         WebRequest webRequest) {
//        log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
//
//        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
//                webRequest.getDescription(false),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                exception.getMessage(),
//                LocalDateTime.now()
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(errorResponseDTO);
//    }
}
