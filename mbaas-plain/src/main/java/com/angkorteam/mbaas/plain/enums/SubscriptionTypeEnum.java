package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 5/6/16.
 */
public enum SubscriptionTypeEnum {

    BOTH("both"), FROM("from"), NONE("none"), REMOVE("remove"), TO("to");

    private final String literal;

    SubscriptionTypeEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
