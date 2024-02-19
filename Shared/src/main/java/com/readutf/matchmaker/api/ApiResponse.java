package com.readutf.matchmaker.api;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    T data;
    boolean success;
    String message;

    private ApiResponse(T data, boolean success, String message) {
        this.data = data;
        this.success = success;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, true, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, false, message);
    }

}
