package com.dmc.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 *
 * 공통 error page ExceptionHandler
 * @author Dong-Joon Oh */
@Slf4j
@ControllerAdvice
@Order(2)
public class HtmlErrorControllerAdvice {

    /**
     * [HttpRequestMethodNotSupportedException] 예외 처리 - {@code 405} <br />
     * POST 요청에 GET 요청이 들어왔을때 예외 처리
     * @author Dong-Joon Oh
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                            WebRequest request) {
        log.warn("{} [{}] {}", ex.getClass().getSimpleName(), HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("error-page/error"); // HTML 오류 페이지의 경로
        modelAndView.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        modelAndView.addObject("message", "Method Not Allowed");
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.addObject("status", HttpStatus.METHOD_NOT_ALLOWED.value());

        return modelAndView;
    }

    /**
     * [NoResourceFoundException] 예외 처리 - {@code 404} <br />
     * 페이지를 찾지 못했을때 발생하는 예외처리
     * @author Dong-Joon Oh
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoResourceFoundException exception, WebRequest webRequest) {
        log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.NOT_FOUND, exception.getMessage());

        ModelAndView modelAndView = new ModelAndView("error-page/error"); // HTML 오류 페이지의 경로
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", "Method Not Allowed");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("error", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ModelAndView handleAuthorizationDeniedException(AuthorizationDeniedException exception, WebRequest webRequest) {
        log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.FORBIDDEN, exception.getMessage());

        ModelAndView modelAndView = new ModelAndView("main/index"); // HTML 오류 페이지의 경로
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.addObject("message", "Method Not Allowed");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("error", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception exception, WebRequest webRequest) {
        log.warn("{} [{}] {}", exception.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

        ModelAndView modelAndView = new ModelAndView("error-page/error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("message", "Internal Server Error");

        return modelAndView;
    }
}
