package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

public enum UserTotpStatusEnum implements Serializable{

    Granted("Granted"),
    Denied("Denied");

    private final String literal;

    UserTotpStatusEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
