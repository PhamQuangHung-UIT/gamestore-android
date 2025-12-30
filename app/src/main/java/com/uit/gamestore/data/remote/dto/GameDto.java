package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class GameDto {
    @SerializedName("_id")
    private String _id;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("genre")
    private String genre;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("version")
    private String version;

    @SerializedName("originalPrice")
    private double originalPrice;

    @SerializedName("discountPrice")
    private Double discountPrice;

    @SerializedName("releaseStatus")
    private String releaseStatus;

    @SerializedName("publisher")
    private PublisherDto publisher;

    @SerializedName("promotion")
    private PromotionDto promotion;

    @SerializedName("averageRating")
    private Double averageRating;

    @SerializedName("reviewCount")
    private Integer reviewCount;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        // Return _id if available, otherwise return id
        return _id != null ? _id : id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVersion() {
        return version;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public PublisherDto getPublisher() {
        return publisher;
    }

    public PromotionDto getPromotion() {
        return promotion;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public double getEffectivePrice() {
        if (discountPrice != null && discountPrice > 0 && discountPrice < originalPrice) {
            return discountPrice;
        }
        if (promotion != null && "Active".equals(promotion.getStatus())) {
            return originalPrice * (1 - promotion.getDiscountPercentage() / 100.0);
        }
        return originalPrice;
    }

    public boolean hasDiscount() {
        return (discountPrice != null && discountPrice > 0 && discountPrice < originalPrice)
                || (promotion != null && "Active".equals(promotion.getStatus()));
    }

    public static class PublisherDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("publisherName")
        private String publisherName;

        @SerializedName("email")
        private String email;

        public String getId() {
            return id;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class PromotionDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("promotionName")
        private String promotionName;

        @SerializedName("description")
        private String description;

        @SerializedName("discountPercentage")
        private double discountPercentage;

        @SerializedName("startDate")
        private String startDate;

        @SerializedName("endDate")
        private String endDate;

        @SerializedName("status")
        private String status;

        public String getId() {
            return id;
        }

        public String getPromotionName() {
            return promotionName;
        }

        public String getDescription() {
            return description;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStatus() {
            return status;
        }
    }
}
