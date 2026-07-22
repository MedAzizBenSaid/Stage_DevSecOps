package com.studentmanagement.sms.entity;

/**
 * Statut de résultat d'un étudiant.
 * Ce statut n'est jamais stocké en base : il est toujours calculé
 * à la volée à partir des notes (MAIN puis éventuellement CONTROL).
 */
public enum ResultStatus {
    ADMITTED,        // Admis
    CONTROL_SESSION, // Doit passer la session de contrôle
    FAILED           // Ajourné / redouble (après session de contrôle)
}
