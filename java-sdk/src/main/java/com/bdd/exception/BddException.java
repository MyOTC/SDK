package com.bdd.exception;


/**
 * 基础异常类
 * Created by xuejingtao
 */
public class BddException extends RuntimeException {

    private int code;
    private String errorCode;


    public BddException(int code) {
        super();
        this.code = code;
    }
    public BddException(int code, String message) {
        super(message);
        this.code = code;
        this.errorCode = "";
    }

    public BddException(String message) {
        super(message);
        this.code = 500;
    }

    public BddException(int code, String errorCode, String message) {
        super(message);
        this.code = code;
        this.errorCode = errorCode;
    }

    public int getCode() {
        return code;
    }
}
