package com.uit.gamestore.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("order")
    private OrderDto order;

    @SerializedName("transactionId")
    private String transactionId;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public OrderDto getOrder() {
        return order;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
