package com.studentmanagement.sms.exception;

/**
 * Levée lorsqu'une requête viole une règle métier
 * (ex: coefficient <= 0, note hors intervalle, etc.)
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
