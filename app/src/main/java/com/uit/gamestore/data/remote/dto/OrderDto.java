package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDto {
    @SerializedName("id")
    private String id;

    @SerializedName("_id")
    private String _id;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("totalValue")
    private double totalValue;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("orderDetails")
    private List<OrderDetailDto> orderDetails;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id != null ? id : _id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalValue() {
        return totalValue > 0 ? totalValue : totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
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
        @SerializedName("id")
        private String id;

        @SerializedName("_id")
        private String _id;

        @SerializedName("value")
        private double value;

        @SerializedName("game")
        private GameDto game;

        @SerializedName("gameKey")
        private GameKeyDto gameKey;

        public String getId() {
            return id != null ? id : _id;
        }

        public double getValue() {
            return value;
        }

        public GameDto getGame() {
            return game;
        }

        public GameKeyDto getGameKey() {
            return gameKey;
        }
    }

    public static class GameKeyDto {
        @SerializedName("id")
        private String id;

        @SerializedName("_id")
        private String _id;

        @SerializedName("keyCode")
        private String keyCode;

        @SerializedName("gameVersion")
        private String gameVersion;

        @SerializedName("activationStatus")
        private String activationStatus;

        public String getId() {
            return id != null ? id : _id;
        }

        public String getKeyCode() {
            return keyCode;
        }

        public String getGameVersion() {
            return gameVersion;
        }

        public String getActivationStatus() {
            return activationStatus;
        }
    }
}
