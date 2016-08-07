package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 8/7/16.
 */
public enum FieldEnum implements Serializable {

    Boolean("Boolean"),
    Long("Long"),
    Double("Double"),
    Enum("Enum"),
    Character("Character"),
    String("String"),
    Time("Time"),
    Date("Date"),
    DateTime("DateTime"),
    Map("Map"),
    List("List");

    private final String literal;

    FieldEnum(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
