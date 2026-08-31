package com.exceptions;



public class BusinessExceptions {

    private BusinessExceptions(){

    }

    public static class AccountLockedException extends RuntimeException{
        public AccountLockedException(String message){
            super(message);
        }
    }

    public static class AccountNotFoundException extends RuntimeException{
        public AccountNotFoundException(String message){
            super(message);
        }
    }

    public static class AccountAlreadyExitsException extends RuntimeException{
        public AccountAlreadyExitsException(String message){
            super(message);
        }
    }
}
