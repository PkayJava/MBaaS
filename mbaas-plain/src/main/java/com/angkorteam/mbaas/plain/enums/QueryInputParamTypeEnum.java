package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 3/10/16.
 */
public enum QueryInputParamTypeEnum {

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
    List("List"),
    DateTime("DateTime");

    private final String literal;

    QueryInputParamTypeEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }

}
