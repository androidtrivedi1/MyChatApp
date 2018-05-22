package com.trivediinfoway.mychatapp;

/**
 * Created by TI A1 on 08-05-2018.
 */

public class Message {
    private String username, message;

    public Message(){}

    public Message(String title, String message) {
        this.username = title;
        this.message = message;
    }

    public String getTitle() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
