package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by socheat on 3/10/16.
 */
public enum AttributeTypeEnum implements Serializable {

    Boolean("Boolean", java.lang.Boolean.class.getName(), "BIT(1)", true, "eav_boolean"),
    Byte("Byte", java.lang.Byte.class.getName(), "INT(11)", false, "eav_integer"),
    Short("Short", java.lang.Short.class.getName(), "INT(11)", false, "eav_integer"),
    Integer("Integer", java.lang.Integer.class.getName(), "INT(11)", true, "eav_integer"),
    Long("Long", java.lang.Long.class.getName(), "INT(11)", false, "eav_integer"),
    Float("Float", java.lang.Float.class.getName(), "DECIMAL(15,4)", false, "eav_decimal"),
    Double("Double", java.lang.Double.class.getName(), "DECIMAL(15,4)", true, "eav_decimal"),
    Character("Character", java.lang.Character.class.getName(), "VARCHAR(1)", true, "eav_character"),
    Text("Text", java.lang.String.class.getName(), "TEXT", true, "eav_text"),
    String("String", java.lang.String.class.getName(), "VARCHAR(255)", true, "eav_varchar"),
    Time("Time", java.sql.Time.class.getName(), "TIME", true, "eav_time"),
    Date("Date", java.sql.Date.class.getName(), "DATE", true, "eav_date"),
    DateTime("DateTime", java.sql.Timestamp.class.getName(), "DATETIME", true, "eav_datetime");

    private final String literal;

    private final String javaType;

    private final String sqlType;

    private final boolean exposed;

    private final String eavTable;

    AttributeTypeEnum(java.lang.String literal, java.lang.String javaType, java.lang.String sqlType, boolean exposed, java.lang.String eavTable) {
        this.literal = literal;
        this.javaType = javaType;
        this.sqlType = sqlType;
        this.exposed = exposed;
        this.eavTable = eavTable;
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

    public final String getEavTable() {
        return eavTable;
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
        } else if (object instanceof java.sql.Time || object instanceof LocalTime) {
            return AttributeTypeEnum.Time;
        } else if (object instanceof java.sql.Timestamp || object instanceof LocalDateTime) {
            return AttributeTypeEnum.DateTime;
        } else if (object instanceof java.sql.Date || object instanceof LocalDate || object instanceof java.util.Date) {
            return AttributeTypeEnum.Date;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }
}
