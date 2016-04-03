package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 4/2/16.
 */
public enum AuthenticationEnum implements Serializable {
    
    None("None"),
    TOTP("TOTP"),
    TwoSMS("TwoSMS"),
    TwoEMail("TwoEMail");

    private final String literal;

    AuthenticationEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
