package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 3/10/16.
 */
public enum TypeEnum {

    Boolean("Boolean", java.lang.Boolean.class.getName(), "BIT"),
    Byte("Byte", java.lang.Byte.class.getName(), "INT"),
    Short("Short", java.lang.Short.class.getName(), "INT"),
    Integer("Integer", java.lang.Integer.class.getName(), "INT"),
    Long("Long", java.lang.Long.class.getName(), "INT"),
    Float("Float", java.lang.Float.class.getName(), "DECIMAL"),
    Double("Double", java.lang.Double.class.getName(), "DECIMAL"),
    Character("Character", java.lang.Character.class.getName(), "VARCHAR"),
    String("String", java.lang.String.class.getName(), "VARCHAR"),
    Time("Time", java.sql.Time.class.getName(), "DATETIME"),
    Date("Date", java.sql.Date.class.getName(), "DATETIME"),
    DateTime("DateTime", java.sql.Timestamp.class.getName(), "DATETIME"),
    Blob("Blob", java.lang.Byte[].class.getName(), "BLOB");

    private final String literal;

    private final String javaType;

    private final String sqlType;

    TypeEnum(String literal, String javaType, String sqlType) {
        this.literal = literal;
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    public final String getLiteral() {
        return literal;
    }

    public final String getJavaType() {
        return javaType;
    }

    public final String getSqlType() {
        return sqlType;
    }
}
