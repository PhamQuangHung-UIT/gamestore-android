package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CustomerProfileDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("genderId")
    private GenderDto gender;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("username")
    private String username;

    @SerializedName("accountStatus")
    private String accountStatus;

    @SerializedName("accountBalance")
    private double accountBalance;

    @SerializedName("registrationDate")
    private String registrationDate;

    @SerializedName("socialMedia")
    private String socialMedia;

    @SerializedName("bankType")
    private String bankType;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("description")
    private String description;

    @SerializedName("wishlist")
    private java.util.List<String> wishlist;

    @SerializedName("ownedGameIds")
    private java.util.List<String> ownedGameIds;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public GenderDto getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getSocialMedia() {
        return socialMedia;
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

    public java.util.List<String> getWishlist() {
        return wishlist;
    }

    public java.util.List<String> getOwnedGameIds() {
        return ownedGameIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public static class GenderDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
