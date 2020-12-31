package com.crainyday.mychat.entity;

public class User {
    private String name;
    private String clientId;
    private String image;

    public User(String name, String clientId, String image) {
        this.name = name;
        this.clientId = clientId;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", clientId='" + clientId + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
