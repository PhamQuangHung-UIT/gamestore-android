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

        @SerializedName("accountStatus")
        private String accountStatus;

        @SerializedName("accountBalance")
        private double accountBalance;

        @SerializedName("registrationDate")
        private String registrationDate;

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

        public String getAccountStatus() {
            return accountStatus;
        }

        public double getAccountBalance() {
            return accountBalance;
        }

        public String getRegistrationDate() {
            return registrationDate;
        }
    }
}
