package com.trivediinfoway.mychatapp;

/**
 * Created by Admin on 11-05-2018.
 */

public class DataClassMsg {

    String msg;
    String type;

    public boolean isTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(boolean typingStatus) {
        this.typingStatus = typingStatus;
    }

    boolean typingStatus;

    public String getMap_lat() {
        return map_lat;
    }

    public void setMap_lat(String map_lat) {
        this.map_lat = map_lat;
    }

    public String getMap_long() {
        return map_long;
    }

    public void setMap_long(String map_long) {
        this.map_long = map_long;
    }

    String map_lat;
    String map_long;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
