package com.example.updateapp.models;

public class UserModel {

    private String name, email, number, password, profile;

    public UserModel(String name, String email, String number, String password, String profile) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.password = password;
        this.profile = profile;
    }

    public UserModel(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
