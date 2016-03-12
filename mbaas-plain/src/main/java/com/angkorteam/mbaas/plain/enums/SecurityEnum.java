package com.angkorteam.mbaas.plain.enums;

public enum SecurityEnum {

    Granted("Granted"),
    Denied("Denied");

    private final String literal;

    SecurityEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
