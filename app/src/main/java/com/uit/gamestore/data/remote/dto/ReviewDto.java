package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("reviewText")
    private String reviewText;

    @SerializedName("customerId")
    private CustomerInfo customer;

    @SerializedName("customer")
    private CustomerInfo customerObj;

    @SerializedName("gameId")
    private String gameId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        // Return reviewText if comment is null
        return comment != null ? comment : reviewText;
    }

    public CustomerInfo getCustomer() {
        // Return customerObj if customer is null
        return customer != null ? customer : customerObj;
    }

    public String getGameId() {
        return gameId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Factory method to create ReviewDto from GameDto.ReviewInfo
    public static ReviewDto fromGameReviewInfo(GameDto.ReviewInfo reviewInfo) {
        ReviewDto dto = new ReviewDto();
        dto.id = reviewInfo.getId();
        dto.rating = reviewInfo.getRating();
        dto.reviewText = reviewInfo.getReviewText();
        dto.createdAt = reviewInfo.getCreatedAt();
        if (reviewInfo.getCustomer() != null) {
            dto.customerObj = new CustomerInfo();
            dto.customerObj.id = reviewInfo.getCustomer().getId();
            dto.customerObj.username = reviewInfo.getCustomer().getUsername();
            dto.customerObj.email = reviewInfo.getCustomer().getEmail();
        }
        return dto;
    }

    public static class CustomerInfo {
        @SerializedName("_id")
        String id;

        @SerializedName("id") 
        String idAlt;

        @SerializedName("username")
        String username;

        @SerializedName("email")
        String email;

        public String getId() {
            return id != null ? id : idAlt;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}
