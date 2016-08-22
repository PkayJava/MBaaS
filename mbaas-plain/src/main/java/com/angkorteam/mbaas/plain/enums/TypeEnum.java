package com.angkorteam.mbaas.plain.enums;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/10/16.
 */
public enum TypeEnum implements Serializable {

    Boolean(/*  */"Boolean", /*  */Boolean.class, /*  */"BIT", /*     */"1", /*   */"eav_boolean", /*  */true, true, true, true, true, true, true, true, true, true, true, true, true),
    Byte(/*     */"Byte", /*     */Byte.class, /*     */"", /*        */"",  /*   */"", /*             */false, false, false, false, false, false, true, false, false, false, false, true, true),
    Long(/*     */"Long", /*     */Long.class, /*     */"INT", /*     */"11", /*  */"eav_integer", /*  */true, true, true, true, true, true, true, true, true, true, true, true, true),
    Double(/*   */"Double", /*   */Double.class, /*   */"DECIMAL", /* */"15,4", /**/"eav_decimal", /*  */true, true, true, true, true, true, true, true, true, true, true, true, true),
    Character(/**/"Character", /**/Character.class, /**/"VARCHAR", /* */"1", /*   */"eav_character", /**/true, true, false, false, true, false, false, false, false, false, false, true, true),
    String(/*   */"String", /*   */String.class, /*   */"VARCHAR", /* */"255", /* */"eav_varchar", /*  */true, true, true, true, true, true, true, true, true, true, true, true, true),
    Text(/*     */"Text", /*     */String.class, /*   */"TEXT", /*    */"", /*    */"eav_text", /*     */true, true, false, false, false, false, false, false, false, false, false, true, true),
    Time(/*     */"Time", /*     */Time.class, /*     */"TIME", /*    */"", /*    */"eav_time", /*     */true, true, true, true, true, true, true, true, true, true, true, true, true),
    Date(/*     */"Date", /*     */Date.class, /*     */"DATE", /*    */"", /*    */"eav_date", /*     */true, true, true, true, true, true, true, true, true, true, true, true, true),
    DateTime(/* */"DateTime", /* */Timestamp.class, /**/"DATETIME", /**/"", /*    */"eav_datetime", /* */true, true, true, true, true, true, true, true, true, true, true, true, true),
    List(/*     */"List", /*     */List.class, /*     */"", /*        */"", /*    */"", /*             */false, false, true, true, false, true, false, true, false, true, false, true, true),
    Map(/*      */"Map", /*      */Map.class, /*      */"", /*        */"", /*    */"", /*             */false, false, true, true, false, true, true, false, false, false, false, true, true),
    Enum(/*     */"Enum", /*     */Enum.class, /*     */"", /*        */"", /*    */"", /*             */false, false, false, false, false, true, true, true, true, true, true, true, true),
    File(/*     */"File", /*     */File.class, /*     */"", /*        */"", /*    */"", /*             */false, false, false, false, false, true, false, false, false, false, false, true, true);

    private final String literal;

    private final Class<?> javaType;

    private final String length;

    private final String sqlType;

    private final boolean exposed;

    private final String eavTable;

    private final boolean attributeType;

    private final boolean queryType;

    private final boolean querySubType;

    private final boolean enumType;

    private final boolean bodyType;

    private final boolean bodySubType;

    private final boolean httpHeaderType;

    private final boolean httpHeaderSubType;

    private final boolean httpQueryType;

    private final boolean httpQuerySubType;

    private final boolean apiType;

    private final boolean apiSubType;

    TypeEnum(String literal,
             Class<?> javaType,
             String sqlType,
             String length,
             String eavTable,
             boolean exposed,
             boolean attributeType,
             boolean queryType,
             boolean querySubType,
             boolean enumType,
             boolean bodyType,
             boolean bodySubType,
             boolean httpHeaderType,
             boolean httpHeaderSubType,
             boolean httpQueryType,
             boolean httpQuerySubType,
             boolean apiType,
             boolean apiSubType) {
        this.literal = literal;
        this.javaType = javaType;
        this.length = length;
        this.sqlType = sqlType;
        this.exposed = exposed;
        this.eavTable = eavTable;
        this.attributeType = attributeType;
        this.queryType = queryType;
        this.querySubType = querySubType;
        this.enumType = enumType;
        this.bodyType = bodyType;
        this.bodySubType = bodySubType;
        this.httpHeaderType = httpHeaderType;
        this.httpHeaderSubType = httpHeaderSubType;
        this.httpQueryType = httpQueryType;
        this.httpQuerySubType = httpQuerySubType;
        this.apiType = apiType;
        this.apiSubType = apiSubType;
    }

    public boolean isHttpHeaderSubType() {
        return httpHeaderSubType;
    }

    public boolean isHttpQueryType() {
        return httpQueryType;
    }

    public boolean isHttpQuerySubType() {
        return httpQuerySubType;
    }

    public boolean isBodyType() {
        return bodyType;
    }

    public final String getLiteral() {
        return literal;
    }

    public final String getLength() {
        return length;
    }

    public final Class<?> getJavaType() {
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

    public boolean isAttributeType() {
        return attributeType;
    }

    public boolean isEnumType() {
        return enumType;
    }

    public boolean isBodySubType() {
        return bodySubType;
    }

    public boolean isQueryType() {
        return queryType;
    }

    public boolean isQuerySubType() {
        return querySubType;
    }

    public boolean isHttpHeaderType() {
        return httpHeaderType;
    }

    public static final TypeEnum parseExternalType(Object object) {
        if (object instanceof Boolean) {
            return TypeEnum.Boolean;
        } else if (object instanceof Byte) {
            return TypeEnum.Long;
        } else if (object instanceof Short) {
            return TypeEnum.Long;
        } else if (object instanceof Integer) {
            return TypeEnum.Long;
        } else if (object instanceof Long) {
            return TypeEnum.Long;
        } else if (object instanceof Float) {
            return TypeEnum.Double;
        } else if (object instanceof Double) {
            return TypeEnum.Double;
        } else if (object instanceof Character) {
            return TypeEnum.Character;
        } else if (object instanceof String) {
            return TypeEnum.String;
        } else if (object instanceof java.sql.Time || object instanceof LocalTime) {
            return TypeEnum.Time;
        } else if (object instanceof java.sql.Timestamp || object instanceof LocalDateTime) {
            return TypeEnum.DateTime;
        } else if (object instanceof java.sql.Date || object instanceof LocalDate || object instanceof java.util.Date) {
            return TypeEnum.Date;
        } else {
            throw new IllegalArgumentException("clazz must be byte, short, integer, long, float, double, character, string, date");
        }
    }
}