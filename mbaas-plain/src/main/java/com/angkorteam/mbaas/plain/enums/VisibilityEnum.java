package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 4/14/16.
 */
public enum VisibilityEnum implements Serializable {

    Shown("Shown"),
    Hided("Hided");

    private final String literal;

    VisibilityEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
