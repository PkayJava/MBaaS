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

    public static final TypeEnum parse(Class<?> clazz) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return TypeEnum.Boolean;
        } else if (clazz == Byte.class || clazz == byte.class) {
            return TypeEnum.Byte;
        } else if (clazz == Short.class || clazz == Short.class) {
            return TypeEnum.Short;
        } else if (clazz == Integer.class || clazz == int.class) {
            return TypeEnum.Integer;
        } else if (clazz == Long.class || clazz == long.class) {
            return TypeEnum.Long;
        } else if (clazz == Float.class || clazz == float.class) {
            return TypeEnum.Float;
        } else if (clazz == Double.class || clazz == double.class) {
            return TypeEnum.Double;
        } else if (clazz == Character.class || clazz == char.class) {
            return TypeEnum.Character;
        } else if (clazz == String.class) {
            return TypeEnum.String;
        } else if (clazz == java.util.Date.class) {
            return TypeEnum.DateTime;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }

    public static final TypeEnum parse(Object object) {
        if (object instanceof Boolean) {
            return TypeEnum.Boolean;
        } else if (object instanceof Byte) {
            return TypeEnum.Byte;
        } else if (object instanceof Short) {
            return TypeEnum.Short;
        } else if (object instanceof Integer) {
            return TypeEnum.Integer;
        } else if (object instanceof Long) {
            return TypeEnum.Long;
        } else if (object instanceof Float) {
            return TypeEnum.Float;
        } else if (object instanceof Double) {
            return TypeEnum.Double;
        } else if (object instanceof Character) {
            return TypeEnum.Character;
        } else if (object instanceof String) {
            return TypeEnum.String;
        } else if (object instanceof java.util.Date) {
            return TypeEnum.DateTime;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }
}
