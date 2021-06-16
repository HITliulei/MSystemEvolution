package com.septemberhx.common.exception;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 */
public class MethodNotAllowException extends RuntimeException {
    private String message;

    public MethodNotAllowException(String msg) {
        super(msg);
        this.message = msg;
    }
}
