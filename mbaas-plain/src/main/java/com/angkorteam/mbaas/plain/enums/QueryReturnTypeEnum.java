package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public enum QueryReturnTypeEnum implements Serializable {

    Boolean("Boolean"),
    Byte("Byte"),
    Short("Short"),
    Integer("Integer"),
    Long("Long"),
    Float("Float"),
    Double("Double"),
    Character("Character"),
    String("String"),
    Time("Time"),
    Date("Date"),
    DateTime("DateTime"),
    Map("Map"),
    List("List", false);

    private final String literal;

    private final boolean subType;

    QueryReturnTypeEnum(String literal) {
        this.literal = literal;
        this.subType = true;
    }

    QueryReturnTypeEnum(String literal, boolean subType) {
        this.literal = literal;
        this.subType = subType;
    }

    public final String getLiteral() {
        return literal;
    }

    public final boolean isSubType() {
        return subType;
    }
}
