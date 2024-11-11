package kr.co.steellink.user.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractApiException extends RuntimeException {

    private final HttpStatus status;
    private final int statusCode;

    public AbstractApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.statusCode = status.value();
    }

    public AbstractApiException(HttpStatus status, int statusCode, String message) {
        super(message);
        this.status = status;
        this.statusCode = statusCode;
    }

}
