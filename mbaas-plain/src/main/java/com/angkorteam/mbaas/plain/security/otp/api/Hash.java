package com.angkorteam.mbaas.plain.security.otp.api;

/**
 * Created by socheat on 4/3/16.
 */
public enum Hash {

    SHA1("HMACSHA1");

    private String hash;

    Hash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return hash;
    }
}