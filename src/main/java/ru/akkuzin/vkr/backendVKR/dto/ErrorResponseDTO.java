package ru.akkuzin.vkr.backendVKR.dto;

import java.util.List;

public class ErrorResponseDTO {
    private String message;
    private List<String> details;

    public ErrorResponseDTO(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    // Геттеры
    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }
}