package com.example.jurisscan;

// ChatMessage.java
public class ChatMessage {
    private final String message;
    private final boolean isUser; // to identify if the message is from the user or AI

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
