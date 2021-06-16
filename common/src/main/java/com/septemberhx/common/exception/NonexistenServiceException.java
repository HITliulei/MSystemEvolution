package com.septemberhx.common.exception;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class NonexistenServiceException extends RuntimeException {
    private String message;

    public NonexistenServiceException(String msg) {
        super(msg);
        this.message = msg;
    }
}
