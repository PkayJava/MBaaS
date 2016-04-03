package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public enum AttributeTypeEnum implements Serializable {

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
    Blob("Blob", java.lang.Byte[].class.getName(), "BLOB", false);

    private final String literal;

    private final String javaType;

    private final String sqlType;

    private final boolean exposed;

    AttributeTypeEnum(String literal, String javaType, String sqlType) {
        this(literal, javaType, sqlType, true);
    }

    AttributeTypeEnum(String literal, String javaType, String sqlType, boolean exposed) {
        this.literal = literal;
        this.javaType = javaType;
        this.sqlType = sqlType;
        this.exposed = exposed;
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

    public final boolean isExposed() {
        return exposed;
    }

    public static final AttributeTypeEnum parse(Class<?> clazz) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return AttributeTypeEnum.Boolean;
        } else if (clazz == Byte.class || clazz == byte.class) {
            return AttributeTypeEnum.Byte;
        } else if (clazz == Short.class || clazz == Short.class) {
            return AttributeTypeEnum.Short;
        } else if (clazz == Integer.class || clazz == int.class) {
            return AttributeTypeEnum.Integer;
        } else if (clazz == Long.class || clazz == long.class) {
            return AttributeTypeEnum.Long;
        } else if (clazz == Float.class || clazz == float.class) {
            return AttributeTypeEnum.Float;
        } else if (clazz == Double.class || clazz == double.class) {
            return AttributeTypeEnum.Double;
        } else if (clazz == Character.class || clazz == char.class) {
            return AttributeTypeEnum.Character;
        } else if (clazz == String.class) {
            return AttributeTypeEnum.String;
        } else if (clazz == java.util.Date.class) {
            return AttributeTypeEnum.DateTime;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }

    public static final AttributeTypeEnum parse(Object object) {
        if (object instanceof Boolean) {
            return AttributeTypeEnum.Boolean;
        } else if (object instanceof Byte) {
            return AttributeTypeEnum.Byte;
        } else if (object instanceof Short) {
            return AttributeTypeEnum.Short;
        } else if (object instanceof Integer) {
            return AttributeTypeEnum.Integer;
        } else if (object instanceof Long) {
            return AttributeTypeEnum.Long;
        } else if (object instanceof Float) {
            return AttributeTypeEnum.Float;
        } else if (object instanceof Double) {
            return AttributeTypeEnum.Double;
        } else if (object instanceof Character) {
            return AttributeTypeEnum.Character;
        } else if (object instanceof String) {
            return AttributeTypeEnum.String;
        } else if (object instanceof java.util.Date) {
            return AttributeTypeEnum.DateTime;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }
}
