package com.angkorteam.mbaas.server.function;

import org.apache.commons.lang3.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public class MariaDBFunction {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, Map<String, Object> attributes) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_ADD ( ").append(blob).append(", ");
        List<String> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() instanceof Date) {
                String field = "'" + entry.getKey() + "', '" + DATE_FORMAT.format((Date) entry.getValue()) + "' AS DATETIME";
                fields.add(field);
            } else if (entry.getValue() instanceof String
                    || entry.getValue() instanceof Character) {
                String field = "'" + entry.getKey() + "', '" + String.valueOf(entry.getValue()) + "' AS CHAR";
                fields.add(field);
            } else if (entry.getValue() instanceof Double
                    || entry.getValue() instanceof Float) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS DOUBLE";
                fields.add(field);
            } else if (entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Long
                    || entry.getValue() instanceof Short
                    || entry.getValue() instanceof Byte) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                fields.add(field);
            } else if (entry.getValue() instanceof Boolean) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                fields.add(field);
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
    public static String columnCreate(Map<String, Object> attributes) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_CREATE ( ");
        List<String> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() instanceof Date) {
                String field = "'" + entry.getKey() + "', '" + DATE_FORMAT.format((Date) entry.getValue()) + "' AS DATETIME";
                fields.add(field);
            } else if (entry.getValue() instanceof String
                    || entry.getValue() instanceof Character) {
                String field = "'" + entry.getKey() + "', '" + String.valueOf(entry.getValue()) + "' AS CHAR";
                fields.add(field);
            } else if (entry.getValue() instanceof Double
                    || entry.getValue() instanceof Float) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS DOUBLE";
                fields.add(field);
            } else if (entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Long
                    || entry.getValue() instanceof Short
                    || entry.getValue() instanceof Byte) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                fields.add(field);
            } else if (entry.getValue() instanceof Boolean) {
                String field = "'" + entry.getKey() + "', " + entry.getValue() + " AS INTEGER";
                fields.add(field);
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
    public static String columnGet(String blob, String name, String clazz) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        if (clazz.equals(Integer.class.getName()) || clazz.equals(int.class.getName())
                || clazz.equals(Byte.class.getName()) || clazz.equals(byte.class.getName())
                || clazz.equals(Short.class.getName()) || clazz.equals(short.class.getName())
                || clazz.equals(Long.class.getName()) || clazz.equals(long.class.getName())
                ) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz.equals(Boolean.class.getName()) || clazz.equals(boolean.class.getName())
                ) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz.equals(Double.class.getName()) || clazz.equals(double.class.getName())
                || clazz.equals(Float.class.getName()) || clazz.equals(float.class.getName())) {
            column.append("'").append(name).append("'").append(" AS DOUBLE(15,4)");
        } else if (clazz.equals(Date.class.getName()) || clazz.equals(Time.class.getName()) || clazz.equals(Timestamp.class.getName())) {
            column.append("'").append(name).append("'").append(" AS DATETIME");
        } else if (clazz.equals(Character.class.getName()) || clazz.equals(char.class.getName())
                || clazz.equals(String.class.getName())) {
            column.append("'").append(name).append("'").append(" AS CHAR");
        }
        column.append(" ) ");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, String clazz, String alias) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        if (clazz.equals(Integer.class.getName()) || clazz.equals(int.class.getName())
                || clazz.equals(Byte.class.getName()) || clazz.equals(byte.class.getName())
                || clazz.equals(Short.class.getName()) || clazz.equals(short.class.getName())
                || clazz.equals(Long.class.getName()) || clazz.equals(long.class.getName())
                ) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz.equals(Boolean.class.getName()) || clazz.equals(boolean.class.getName())
                ) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz.equals(Double.class.getName()) || clazz.equals(double.class.getName())
                || clazz.equals(Float.class.getName()) || clazz.equals(float.class.getName())) {
            column.append("'").append(name).append("'").append(" AS DOUBLE(15,4)");
        } else if (clazz.equals(Date.class.getName()) || clazz.equals(Time.class.getName()) || clazz.equals(Timestamp.class.getName())) {
            column.append("'").append(name).append("'").append(" AS DATETIME");
        } else if (clazz.equals(Character.class.getName()) || clazz.equals(char.class.getName())
                || clazz.equals(String.class.getName())) {
            column.append("'").append(name).append("'").append(" AS CHAR");
        }
        column.append(" ) AS " + alias);
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, Class<?> clazz) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        if (clazz == Date.class) {
            column.append("'").append(name).append("'").append(" AS DATETIME");
        } else if (clazz == String.class
                || clazz == Character.class || clazz == char.class) {
            column.append("'").append(name).append("'").append(" AS CHAR");
        } else if (clazz == Double.class || clazz == double.class
                || clazz == Float.class || clazz == float.class) {
            column.append("'").append(name).append("'").append(" AS DOUBLE(15,4)");
        } else if (clazz == Integer.class || clazz == int.class
                || clazz == Long.class || clazz == long.class
                || clazz == Short.class || clazz == short.class
                || clazz == Byte.class || clazz == byte.class) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        }
        column.append(" ) ");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, Class<?> clazz, String alias) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        if (clazz == Date.class) {
            column.append("'").append(name).append("'").append(" AS DATETIME");
        } else if (clazz == String.class
                || clazz == Character.class || clazz == char.class) {
            column.append("'").append(name).append("'").append(" AS CHAR");
        } else if (clazz == Double.class || clazz == double.class
                || clazz == Float.class || clazz == float.class) {
            column.append("'").append(name).append("'").append(" AS DOUBLE(15,4)");
        } else if (clazz == Integer.class || clazz == int.class
                || clazz == Long.class || clazz == long.class
                || clazz == Short.class || clazz == short.class
                || clazz == Byte.class || clazz == byte.class) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            column.append("'").append(name).append("'").append(" AS INTEGER");
        }
        column.append(" ) AS " + alias);
        return column.toString();
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
