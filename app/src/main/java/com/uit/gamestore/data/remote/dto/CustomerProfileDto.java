package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CustomerProfileDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("genderId")
    private Integer genderId;

    @SerializedName("bankType")
    private String bankType;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("description")
    private String description;

    @SerializedName("walletBalance")
    private double walletBalance;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public String getBankType() {
        return bankType;
    }

    public String getBankName() {
        return bankName;
    }

    public String getDescription() {
        return description;
    }

    public double getWalletBalance() {
        return walletBalance;
    }
}
