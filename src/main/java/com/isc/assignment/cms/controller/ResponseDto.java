package com.isc.assignment.cms.controller;

public class ResponseDto<T> {

    private T result;
    private String message;

    public T getResult() {

        return result;
    }

    public void setResult(T result) {

        this.result = result;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }
}
