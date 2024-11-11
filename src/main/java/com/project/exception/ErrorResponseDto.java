package com.dmc.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {

    private  String apiPath;

    private int code;

    private  String message;

    private LocalDateTime errorTime;

    public ErrorResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
