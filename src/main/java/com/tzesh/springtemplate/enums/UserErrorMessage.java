package com.tzesh.springtemplate.enums;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;

/**
 * UserErrorMessage is an enum for user error messages
 * @see BaseErrorMessage
 * @author tzesh
 */
public enum UserErrorMessage implements BaseErrorMessage {
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists with this email or username");

    private final String message;

    UserErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
