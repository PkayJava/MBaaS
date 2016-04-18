package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public enum AttributeTypeEnum implements Serializable {

    Boolean("Boolean", java.lang.Boolean.class.getName(), "BIT(1)"),
    Byte("Byte", java.lang.Byte.class.getName(), "INT(11)"),
    Short("Short", java.lang.Short.class.getName(), "INT(11)"),
    Integer("Integer", java.lang.Integer.class.getName(), "INT(11)"),
    Long("Long", java.lang.Long.class.getName(), "INT(11)"),
    Float("Float", java.lang.Float.class.getName(), "DECIMAL(15,4)"),
    Double("Double", java.lang.Double.class.getName(), "DECIMAL(15,4)"),
    Character("Character", java.lang.Character.class.getName(), "VARCHAR(1)"),
    Text("Text", java.lang.String.class.getName(), "TEXT"),
    String("String", java.lang.String.class.getName(), "VARCHAR(255)"),
    Time("Time", java.sql.Time.class.getName(), "TIME"),
    Date("Date", java.sql.Date.class.getName(), "DATE"),
    DateTime("DateTime", java.sql.Timestamp.class.getName(), "DATETIME");

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

    public static final AttributeTypeEnum parseExternalAttributeType(Object object) {
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
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }
}
