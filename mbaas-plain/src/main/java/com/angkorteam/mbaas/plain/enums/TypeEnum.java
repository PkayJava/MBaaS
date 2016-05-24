package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public enum TypeEnum implements Serializable {

    Boolean("Boolean"),
    Byte("Byte", false),
    Short("Short", false),
    Integer("Integer"),
    Long("Long", false),
    Float("Float", false),
    Double("Double"),
    Character("Character"),
    String("String"),
    Time("Time"),
    Date("Date"),
    DateTime("DateTime"),
    Map("Map"),
    List("List");

    private final String literal;

    private final boolean subType;

    TypeEnum(String literal) {
        this.literal = literal;
        this.subType = true;
    }

    TypeEnum(String literal, boolean subType) {
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
