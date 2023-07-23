package com.example.exception;

/**
 * 系统内部异常
 * @author zhw
 */
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = -994962710559017255L;

    public SystemException(String message) {
        super(message);
    }
}
