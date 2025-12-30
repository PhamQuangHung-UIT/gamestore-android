package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {
    @SerializedName("games")
    private final List<GameItem> games;

    @SerializedName("paymentMethod")
    private final String paymentMethod;

    public OrderRequest(List<GameItem> games, String paymentMethod) {
        this.games = games;
        this.paymentMethod = paymentMethod;
    }

    public List<GameItem> getGames() {
        return games;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public static class GameItem {
        @SerializedName("gameId")
        private final String gameId;

        public GameItem(String gameId) {
            this.gameId = gameId;
        }

        public String getGameId() {
            return gameId;
        }
    }
}
