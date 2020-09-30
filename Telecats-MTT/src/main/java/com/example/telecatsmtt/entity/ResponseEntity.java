package com.example.telecatsmtt.entity;

public class ResponseEntity {

    private String token;
    private String email;
    private String password;
    private String username;

    public ResponseEntity(String token, String email, String password, String username) {
        this.token = token;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
