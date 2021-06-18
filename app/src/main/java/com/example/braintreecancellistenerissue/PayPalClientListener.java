package com.example.braintreecancellistenerissue;

public interface PayPalClientListener {

    public enum Result {
        SUCCESS,
        FAILURE,
        CANCELLED;
    }

    void onPayPalResponse(Result result);
    void onPayPalResponse(Result result, String txt);
}
