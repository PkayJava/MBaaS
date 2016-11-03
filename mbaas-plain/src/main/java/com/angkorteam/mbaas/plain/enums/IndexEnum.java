package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/9/2016.
 */
public enum IndexEnum implements Serializable {

    INDEX("INDEX"),
    UNIQUE("UNIQUE"),
    FULLTEXT("FULLTEXT");

    private final String literal;

    IndexEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
