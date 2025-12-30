package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaginatedGamesResponse {
    @SerializedName("data")
    private List<GameDto> data;

    @SerializedName("meta")
    private Meta meta;

    public List<GameDto> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        @SerializedName("total")
        private int total;

        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("totalPages")
        private int totalPages;

        public int getTotal() {
            return total;
        }

        public int getPage() {
            return page;
        }

        public int getLimit() {
            return limit;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}
