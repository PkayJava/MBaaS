package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public enum QueryInputParamTypeEnum implements Serializable {

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
    List("List", false);

    private final boolean subType;

    private final String literal;

    QueryInputParamTypeEnum(String literal) {
        this.literal = literal;
        this.subType = true;
    }

    QueryInputParamTypeEnum(String literal, boolean subType) {
        this.subType = subType;
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }

    public final boolean isSubType() {
        return subType;
    }
}
