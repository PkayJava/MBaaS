package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 3/10/16.
 */
public enum QueryReturnTypeEnum {

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
    List("List");

    private final String literal;

    QueryReturnTypeEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
