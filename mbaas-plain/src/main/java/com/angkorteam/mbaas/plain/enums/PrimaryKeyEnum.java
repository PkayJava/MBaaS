package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/9/2016.
 */
public enum PrimaryKeyEnum implements Serializable {
    TABLE_SCHEMA("TABLE_SCHEMA"),
    TABLE_SCHEM("TABLE_SCHEM"),
    TABLE_NAME("TABLE_NAME"),
    COLUMN_NAME("COLUMN_NAME"),
    SEQ_IN_INDEX("SEQ_IN_INDEX"),
    PK_NAME("PK_NAME");

    private final String literal;

    PrimaryKeyEnum(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
