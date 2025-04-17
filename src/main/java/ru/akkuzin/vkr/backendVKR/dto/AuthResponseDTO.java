package ru.akkuzin.vkr.backendVKR.dto;

public class AuthResponseDTO {
    private String email;
    private String token;
    private String role;

    public AuthResponseDTO(String email, String token, String role) {
        this.email = email;
        this.token = token;
        this.role = role;
    }

    // Геттеры
    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}