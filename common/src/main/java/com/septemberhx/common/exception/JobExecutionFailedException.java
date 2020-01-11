package com.septemberhx.common.exception;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/11
 */
public class JobExecutionFailedException extends RuntimeException {
    private String message;

    public JobExecutionFailedException(String msg) {
        super(msg);
        this.message = msg;
    }
}
