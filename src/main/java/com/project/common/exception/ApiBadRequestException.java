package kr.co.steellink.user.common.exception;

import org.springframework.http.HttpStatus;

public class ApiBadRequestException extends AbstractApiException {

    public ApiBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ApiBadRequestException(int statusCode, String message) {
        super(HttpStatus.BAD_REQUEST, statusCode, message);
    }

}
