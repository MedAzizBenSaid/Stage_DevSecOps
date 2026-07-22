package com.studentmanagement.sms.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Format standard des réponses d'erreur renvoyées par l'API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details; // utilisé pour les erreurs de validation multiples
}
