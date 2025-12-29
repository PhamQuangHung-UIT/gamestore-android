package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("salePrice")
    private Double salePrice;

    @SerializedName("onSale")
    private boolean onSale;

    @SerializedName("genre")
    private String genre;

    @SerializedName("coverImage")
    private String coverImage;

    @SerializedName("screenshots")
    private List<String> screenshots;

    @SerializedName("averageRating")
    private Double averageRating;

    @SerializedName("ageRating")
    private String ageRating;

    @SerializedName("publisher")
    private PublisherDto publisher;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public String getGenre() {
        return genre;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public PublisherDto getPublisher() {
        return publisher;
    }

    public double getEffectivePrice() {
        return onSale && salePrice != null ? salePrice : price;
    }

    public static class PublisherDto {
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
