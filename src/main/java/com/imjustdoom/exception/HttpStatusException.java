package com.imjustdoom.exception;

public class HttpStatusException extends Exception {
    private int code;

    public HttpStatusException(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
