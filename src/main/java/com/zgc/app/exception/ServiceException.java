package com.zgc.app.exception;

/**
 * 这是一个自定义的异常，我们可以通过统一异常的捕获，来处理异常的信息
 */
public class ServiceException extends RuntimeException{
    private String message ;
    public ServiceException(String message) {
        super(message);
        this.message = message;
    }
}
