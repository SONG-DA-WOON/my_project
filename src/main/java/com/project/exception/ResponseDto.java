package com.dmc.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ResponseDto<T> {

    private int code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(int code, String statusMsg) {
        this.code = code;
        this.message = statusMsg;
    }

    public ResponseDto(int code, String statusMsg, T data) {
        this.code = code;
        this.message = statusMsg;
        this.data = data;
    }

    public ResponseDto(ErrorResponseDto errorResponseDto) {
        this.code = errorResponseDto.getCode();
        this.message = errorResponseDto.getMessage();
    }
}
