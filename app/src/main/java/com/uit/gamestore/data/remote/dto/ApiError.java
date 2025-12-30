package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ApiError {
    @SerializedName("error")
    private ErrorDetail error;

    public ErrorDetail getError() {
        return error;
    }

    public String getMessage() {
        return error != null ? error.getMessage() : "Unknown error";
    }

    public static class ErrorDetail {
        @SerializedName("statusCode")
        private int statusCode;

        @SerializedName("name")
        private String name;

        @SerializedName("message")
        private String message;

        public int getStatusCode() {
            return statusCode;
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }
    }
}
