package com.example.brandfluenceandroid;  // Ensure this is the correct package

public class Message {
    private String messageText;   // Stores the chat message
    private String senderId;      // Stores the ID of the sender (e.g., brand or influencer)
    private long timestamp;       // Stores the time the message was sent

    // Empty constructor for Firebase (required)
    public Message() {
    }

    // Constructor with parameters
    public Message(String messageText, String senderId, long timestamp) {
        this.messageText = messageText;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getter for message text
    public String getMessage() {
        return messageText;
    }

    // Setter for message text
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    // Getter for sender ID
    public String getSenderId() {
        return senderId;
    }

    // Setter for sender ID
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // Getter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    // Setter for timestamp
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
