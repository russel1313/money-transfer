package com.monex.exception;

public class NotSufficientBalanceException extends Exception{
    public NotSufficientBalanceException(String exception) {
        super(exception);
    }
}
