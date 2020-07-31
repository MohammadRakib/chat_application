package com.example.chat_application.Data;

public class User_Data {
    String uid;
    String name;
    String image;

    public User_Data() {

    }

    public User_Data(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public User_Data(String uid, String name, String image) {
        this.uid = uid;
        this.name = name;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
