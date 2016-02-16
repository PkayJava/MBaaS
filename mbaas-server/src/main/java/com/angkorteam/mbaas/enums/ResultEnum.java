package com.angkorteam.mbaas.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/7/2016.
 */
public enum ResultEnum implements Serializable {

    ERROR("ERROR"),
    OK("OK");

    private final String literal;

    ResultEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
