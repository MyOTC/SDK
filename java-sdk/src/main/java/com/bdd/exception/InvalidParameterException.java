package com.bdd.exception;


public class InvalidParameterException extends BddException {

    private static final long serialVersionUID = -4530275043528852631L;

    public InvalidParameterException(String field){
        super(400,"InvalidParameter", String.format("The specified parameter '%s' is not valid",field));
    }

    public InvalidParameterException(String field, String message){
        super(400,"InvalidParameter", String.format("The specified parameter '%s' %s",field, message));
    }

}
