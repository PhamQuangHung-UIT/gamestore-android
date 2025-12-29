package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("orderDetails")
    private List<OrderDetailDto> orderDetails;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderDetailDto> getOrderDetails() {
        return orderDetails;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public static class OrderDetailDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("unitPrice")
        private double unitPrice;

        @SerializedName("gameId")
        private GameDto game;

        @SerializedName("gameKeyId")
        private GameKeyDto gameKey;

        public String getId() {
            return id;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public GameDto getGame() {
            return game;
        }

        public GameKeyDto getGameKey() {
            return gameKey;
        }
    }

    public static class GameKeyDto {
        @SerializedName("_id")
        private String id;

        @SerializedName("keyCode")
        private String keyCode;

        @SerializedName("keyStatus")
        private String keyStatus;

        public String getId() {
            return id;
        }

        public String getKeyCode() {
            return keyCode;
        }

        public String getKeyStatus() {
            return keyStatus;
        }
    }
}
