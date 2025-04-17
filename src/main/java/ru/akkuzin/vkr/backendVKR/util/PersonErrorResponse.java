package ru.akkuzin.vkr.backendVKR.util;

public class PersonErrorResponse {
    private String message;
    private long timestamp;

    public PersonErrorResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}