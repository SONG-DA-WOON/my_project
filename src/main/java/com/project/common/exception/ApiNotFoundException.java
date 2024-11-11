package kr.co.steellink.user.common.exception;

import org.springframework.http.HttpStatus;

public class ApiNotFoundException extends AbstractApiException {

    public ApiNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ApiNotFoundException(int statusCode, String message) {
        super(HttpStatus.NOT_FOUND, statusCode, message);
    }

}
