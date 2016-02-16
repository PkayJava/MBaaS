package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/9/2016.
 */
public enum IndexInfoEnum implements Serializable {
    TABLE_SCHEM("TABLE_SCHEM"),
    TABLE_NAME("TABLE_NAME"),
    NON_UNIQUE("NON_UNIQUE"),
    TABLE_SCHEMA("TABLE_SCHEMA"),
    INDEX_NAME("INDEX_NAME"),
    TYPE("TYPE"),
    SEQ_IN_INDEX("SEQ_IN_INDEX"),
    COLUMN_NAME("COLUMN_NAME"),
    COLLATION("COLLATION"),
    CARDINALITY("CARDINALITY"),
    PAGES("PAGES"),
    FILTER_CONDITION("FILTER_CONDITION");

    private final String literal;

    IndexInfoEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
