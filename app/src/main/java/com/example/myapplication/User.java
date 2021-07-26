package com.example.myapplication;

public class User {
    private String UUID;
    private String sfu_id;

    public User(String sfu_id) {
        this.UUID = null;
        this.sfu_id = sfu_id;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getSfu_id() {
        return sfu_id;
    }

    public void setSfu_id(String sfu_id) {
        this.sfu_id = sfu_id;
    }
}
