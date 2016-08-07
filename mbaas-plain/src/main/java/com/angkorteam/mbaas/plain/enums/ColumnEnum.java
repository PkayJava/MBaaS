package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/9/2016.
 */
public enum ColumnEnum implements Serializable {
    TABLE_SCHEMA("TABLE_SCHEMA"),
    TABLE_NAME("TABLE_NAME"),
    COLUMN_NAME("COLUMN_NAME"),
    DATA_TYPE("DATA_TYPE"),
    TYPE_NAME("TYPE_NAME"),
    COLUMN_SIZE("COLUMN_SIZE"),
    BUFFER_LENGTH("BUFFER_LENGTH"),
    NUMERIC_SCALE("NUMERIC_SCALE"),
    NUM_PREC_RADIX("NUM_PREC_RADIX"),
    NULLABLE("NULLABLE"),
    COLUMN_COMMENT("COLUMN_COMMENT"),
    COLUMN_DEFAULT("COLUMN_DEFAULT"),
    SQL_DATA_TYPE("SQL_DATA_TYPE"),
    SQL_DATETIME_SUB("SQL_DATETIME_SUB"),
    CHAR_OCTET_LENGTH("CHAR_OCTET_LENGTH"),
    ORDINAL_POSITION("ORDINAL_POSITION"),
    IS_NULLABLE("IS_NULLABLE"),
    SCOPE_CATALOG("SCOPE_CATALOG"),
    SCOPE_SCHEMA("SCOPE_SCHEMA"),
    SCOPE_TABLE("SCOPE_TABLE"),
    SOURCE_DATA_TYPE("SOURCE_DATA_TYPE"),
    IS_AUTOINCREMENT("IS_AUTOINCREMENT");

    private final String literal;

    ColumnEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
