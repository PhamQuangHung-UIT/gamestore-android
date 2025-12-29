package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("customer")
    private CustomerDto customer;

    public String getToken() {
        return token;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public static class CustomerDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("username")
        private String username;

        @SerializedName("phoneNumber")
        private String phoneNumber;

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

        public double getWalletBalance() {
            return walletBalance;
        }
    }
}
