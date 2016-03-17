package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.WicketRuntimeException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public class MariaDBFunction {

    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, Map<String, Object> attributes, Map<String, TypeEnum> typeEnums) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_ADD ( ").append(blob).append(", ");
        List<String> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            TypeEnum typeEnum = typeEnums.get(entry.getKey());
            if (typeEnum == TypeEnum.Boolean) {
                if (entry.getValue() instanceof Boolean) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not Boolean");
                }
            } else if (typeEnum == TypeEnum.Byte
                    || typeEnum == TypeEnum.Short
                    || typeEnum == TypeEnum.Integer
                    || typeEnum == TypeEnum.Long) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        ) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long");
                }
            } else if (typeEnum == TypeEnum.Float
                    || typeEnum == TypeEnum.Double) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        || entry.getValue() instanceof Float
                        || entry.getValue() instanceof Double
                        ) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS DOUBLE";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long, float, double");
                }
            } else if (typeEnum == TypeEnum.Character
                    || typeEnum == TypeEnum.String) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        || entry.getValue() instanceof Float
                        || entry.getValue() instanceof Double
                        || entry.getValue() instanceof Character
                        || entry.getValue() instanceof String
                        ) {
                    String field = "'" + entry.getKey() + "', '" + entry.getValue() + "' AS CHAR";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long, float, double");
                }
            } else if (typeEnum == TypeEnum.Time) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + DATETIME_FORMAT.format((Date) entry.getValue()) + "' AS TIME";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            } else if (typeEnum == TypeEnum.Date) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + DATETIME_FORMAT.format((Date) entry.getValue()) + "' AS DATE";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            } else if (typeEnum == TypeEnum.DateTime) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + DATETIME_FORMAT.format((Date) entry.getValue()) + "' AS DATETIME";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            }
        }
        column.append(StringUtils.join(fields, ", "));
        column.append(" )");
        return column.toString();
    }

    /**
     * Checks if a dynamic column blob is valid
     */
    public static String columnCheck(String blob) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_CHECK ( " + blob + " )");
        return column.toString();
    }

    /**
     * Returns a dynamic columns blob
     */
    public static String columnCreate(Map<String, Object> attributes, Map<String, TypeEnum> typeEnums) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_CREATE ( ");
        List<String> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            TypeEnum typeEnum = typeEnums.get(entry.getKey());
            if (typeEnum == TypeEnum.Boolean) {
                if (entry.getValue() instanceof Boolean) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not Boolean");
                }
            } else if (typeEnum == TypeEnum.Byte
                    || typeEnum == TypeEnum.Short
                    || typeEnum == TypeEnum.Integer
                    || typeEnum == TypeEnum.Long) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        ) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long");
                }
            } else if (typeEnum == TypeEnum.Float
                    || typeEnum == TypeEnum.Double) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        || entry.getValue() instanceof Float
                        || entry.getValue() instanceof Double
                        ) {
                    String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS DOUBLE";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long, float, double");
                }
            } else if (typeEnum == TypeEnum.Character
                    || typeEnum == TypeEnum.String) {
                if (entry.getValue() instanceof Boolean
                        || entry.getValue() instanceof Byte
                        || entry.getValue() instanceof Short
                        || entry.getValue() instanceof Integer
                        || entry.getValue() instanceof Long
                        || entry.getValue() instanceof Float
                        || entry.getValue() instanceof Double
                        || entry.getValue() instanceof Character
                        || entry.getValue() instanceof String
                        ) {
                    String field = "'" + entry.getKey() + "', '" + entry.getValue() + "' AS CHAR";
                    fields.add(field);
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not boolean, byte, short, integer, long, float, double");
                }
            } else if (typeEnum == TypeEnum.Time) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + TIME_FORMAT.format((Date) entry.getValue()) + "' AS TIME";
                    fields.add(field);
                } else if (entry.getValue() instanceof String) {
                    XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                    Date value = null;
                    try {
                        value = dateFormat.parse((String) entry.getValue());
                    } catch (ParseException e) {
                        dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                        try {
                            value = dateFormat.parse((String) entry.getValue());
                        } catch (ParseException e1) {
                        }
                    }
                    if (value != null) {
                        String field = "'" + entry.getKey() + "', '" + TIME_FORMAT.format(value) + "' AS TIME";
                        fields.add(field);
                    }
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            } else if (typeEnum == TypeEnum.Date) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + DATETIME_FORMAT.format((Date) entry.getValue()) + "' AS DATE";
                    fields.add(field);
                } else if (entry.getValue() instanceof String) {
                    XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                    Date value = null;
                    try {
                        value = dateFormat.parse((String) entry.getValue());
                    } catch (ParseException e) {
                        dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                        try {
                            value = dateFormat.parse((String) entry.getValue());
                        } catch (ParseException e1) {
                        }
                    }
                    if (value != null) {
                        String field = "'" + entry.getKey() + "', '" + DATE_FORMAT.format(value) + "' AS DATE";
                        fields.add(field);
                    }
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            } else if (typeEnum == TypeEnum.DateTime) {
                if (entry.getValue() instanceof Date) {
                    String field = "'" + entry.getKey() + "', '" + DATETIME_FORMAT.format((Date) entry.getValue()) + "' AS DATETIME";
                    fields.add(field);
                } else if (entry.getValue() instanceof String) {
                    XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                    Date value = null;
                    try {
                        value = dateFormat.parse((String) entry.getValue());
                    } catch (ParseException e) {
                    }
                    if (value != null) {
                        String field = "'" + entry.getKey() + "', '" + DATE_FORMAT.format(value) + "' AS DATETIME";
                        fields.add(field);
                    }
                } else {
                    throw new WicketRuntimeException(entry.getKey() + " is not date");
                }
            }
        }
        column.append(StringUtils.join(fields, ", "));
        column.append(" )");
        return column.toString();
    }

    /**
     * Deletes a dynamic column
     */
    public static String columnDelete(String blob, String name) {
        return columnDelete(blob, Arrays.asList(name));
    }

    /**
     * Deletes a dynamic column
     */
    public static String columnDelete(String blob, String... names) {
        return columnDelete(blob, Arrays.asList(names));
    }

    /**
     * Deletes a dynamic column
     */
    public static String columnDelete(String blob, List<String> names) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_DELETE ( ").append(blob);
        for (String name : names) {
            column.append(", ").append("'").append(name).append("'");
        }
        column.append(" )");
        return column.toString();
    }

    /**
     * Checks is a column exists
     */
    public static String columnExists(String blob, String name) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_EXISTS ( ").append(blob).append(", ");
        column.append("'").append(name).append("'");
        column.append(" )");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, String javaType) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        if (javaType.equals(TypeEnum.Boolean.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (javaType.equals(TypeEnum.Byte.getLiteral())
                || javaType.equals(TypeEnum.Short.getLiteral())
                || javaType.equals(TypeEnum.Integer.getLiteral())
                || javaType.equals(TypeEnum.Long.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (javaType.equals(TypeEnum.Float.getLiteral())
                || javaType.equals(TypeEnum.Double.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS DOUBLE(15,4)");
        } else if (javaType.equals(TypeEnum.Character.getLiteral())
                || javaType.equals(TypeEnum.String.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS CHAR");
        } else if (javaType.equals(TypeEnum.Time.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS TIME");
        } else if (javaType.equals(TypeEnum.Date.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS DATE");
        } else if (javaType.equals(TypeEnum.DateTime.getLiteral())) {
            column.append("'").append(name).append("'").append(" AS DATETIME");
        }
        column.append(" )");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, String javaType, String alias) {
        return columnGet(blob, name, javaType) + " AS " + alias;
    }

    /**
     * Returns a JSON representation of dynamic column blob data
     */
    public static String columnJson(String blob) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_JSON ( ").append(blob);
        column.append(" )");
        return column.toString();
    }

    /**
     * Returns comma-separated list
     */
    public static String columnList(String blob) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_LIST ( ").append(blob);
        column.append(" )");
        return column.toString();
    }
}
