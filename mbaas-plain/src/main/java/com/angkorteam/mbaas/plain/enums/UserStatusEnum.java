package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 3/2/16.
 */
public enum UserStatusEnum {

    Active("ACTIVE"), Suspended("SUSPENDED");

    private final String literal;

    UserStatusEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
