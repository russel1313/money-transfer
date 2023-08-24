package com.monex.exception;

public class SameAccountException extends Exception{
    public SameAccountException(String exception) {
        super(exception);
    }
}