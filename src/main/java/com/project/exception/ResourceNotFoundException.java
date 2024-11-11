package com.dmc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public abstract class ResourceNotFoundException extends RuntimeException{

    /**
     * 객체를 찾지 못했을때 발생하는 예외
     * @param resourceName 이름
     * @param fieldName   필드
     * @param fieldValue   value
     *  예) [resourceName] 해당 [fieldName] 가 없습니다 . : [fieldValue]
     * @author Dong-Joon Oh
     */
    ResourceNotFoundException(String resourceName, String fieldName, String fieldValue){
        super(String.format("%s 해당 %s 가 없습니다. : '%s'", resourceName, fieldName, fieldValue));
    }

    /**
     * 예외처리 메세지 리턴
     * @param message 예외 메세지
     * @author Dong-Joon Oh
     */
    ResourceNotFoundException(String message) {
        super(message);
    }
}
