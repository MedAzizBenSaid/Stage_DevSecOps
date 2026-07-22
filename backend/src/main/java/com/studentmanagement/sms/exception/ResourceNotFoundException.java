package com.studentmanagement.sms.exception;

/**
 * Levée lorsqu'une ressource demandée (classe, étudiant, matière, note...)
 * n'existe pas en base.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
