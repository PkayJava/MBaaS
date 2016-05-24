package com.angkorteam.mbaas.plain.security.otp.api;

/**
 * Created by socheat on 4/3/16.
 */
public enum Digits {
    SIX(1000000), SEVEN(10000000), EIGHT(100000000);

    private int digits;

    Digits(int digits) {
        this.digits = digits;
    }

    public int getValue() {
        return digits;
    }
}