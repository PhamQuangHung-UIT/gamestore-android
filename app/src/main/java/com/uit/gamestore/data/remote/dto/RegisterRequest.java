package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    private final String email;
    
    @SerializedName("password")
    private final String password;
    
    @SerializedName("username")
    private final String username;
    
    @SerializedName("phoneNumber")
    private final String phoneNumber;

    public RegisterRequest(String email, String password, String username, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
