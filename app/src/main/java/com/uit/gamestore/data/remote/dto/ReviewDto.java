package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("customer")
    private CustomerInfo customer;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public CustomerInfo getCustomer() {
        return customer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static class CustomerInfo {
        @SerializedName("_id")
        private String id;

        @SerializedName("username")
        private String username;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }
    }
}
