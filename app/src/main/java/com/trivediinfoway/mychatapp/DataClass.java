package com.trivediinfoway.mychatapp;

/**
 * Created by TI A1 on 09-05-2018.
 */

public class DataClass {

    String token;
    String username;
    boolean typingStatus;

    public boolean isTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(boolean typingStatus) {
        this.typingStatus = typingStatus;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    String last_seen;

    public boolean isFlag_online() {
        return flag_online;
    }

    public void setFlag_online(boolean flag_online) {
        this.flag_online = flag_online;
    }

    boolean flag_online;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    String image_url;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
