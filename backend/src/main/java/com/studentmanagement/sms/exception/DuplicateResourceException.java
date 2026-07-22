package com.studentmanagement.sms.exception;

/**
 * Levée lorsqu'on tente de créer une ressource qui viole une contrainte
 * d'unicité métier (matricule en double, matière en double, note en double...).
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
