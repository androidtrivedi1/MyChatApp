package com.trivediinfoway.mychatapp;

/**
 * Created by TI A1 on 21-03-2018.
 */

public class UserDetails {
    static String username = "";
    static String password;

    public static String getProfile() {
        return profile;
    }

    public static void setProfile(String profile) {
        UserDetails.profile = profile;
    }

    static String profile = "";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        token = token;
    }

    static String token = "";

    public String getChatwith() {
        return chatwith;
    }

    public void setChatwith(String chatwith) {
        this.chatwith = chatwith;
    }

    static String chatwith = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
