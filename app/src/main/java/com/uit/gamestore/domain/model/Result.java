package com.uit.gamestore.domain.model;

public abstract class Result<T> {


    public static class Success<T> extends Result<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public static class Error<T> extends Result<T> {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
