package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {
    @SerializedName("items")
    private final List<OrderItem> items;

    public OrderRequest(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public static class OrderItem {
        @SerializedName("gameId")
        private final String gameId;

        @SerializedName("quantity")
        private final int quantity;

        public OrderItem(String gameId, int quantity) {
            this.gameId = gameId;
            this.quantity = quantity;
        }

        public OrderItem(String gameId) {
            this(gameId, 1);
        }

        public String getGameId() {
            return gameId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
