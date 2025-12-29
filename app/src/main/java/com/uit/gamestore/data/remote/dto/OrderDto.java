package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("games")
    private List<OrderGameDto> games;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() {
        return id;
    }

    public List<OrderGameDto> getGames() {
        return games;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static class OrderGameDto {
        @SerializedName("gameId")
        private GameDto game;

        @SerializedName("priceAtPurchase")
        private double priceAtPurchase;

        public GameDto getGame() {
            return game;
        }

        public double getPriceAtPurchase() {
            return priceAtPurchase;
        }
    }
}
