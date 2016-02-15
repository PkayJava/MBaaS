package com.angkorteam.mbaas.mariadb;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public class JdbcFunction {

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, Map<String, Serializable> attributes) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_ADD ( ").append(blob).append(", ");
        for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
            column.append("'").append(entry.getKey()).append("'");
            column.append(", ");
            column.append("'").append(String.valueOf(entry.getValue())).append("'");
            if (entry.getValue() instanceof Date) {
                column.append(" as DATETIME");
            } else if (entry.getValue() instanceof String) {
                column.append(" as CHAR");
            } else if (entry.getValue() instanceof Double
                    || entry.getValue() instanceof Float) {
                column.append(" as DOUBLE");
            } else if (entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Long
                    || entry.getValue() instanceof Short
                    || entry.getValue() instanceof Byte) {
                column.append(" as INTEGER");
            }
        }
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
    public static String columnCreate(Map<String, Serializable> attributes) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_CREATE ( ");
        for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
            column.append("'").append(entry.getKey()).append("'");
            column.append(", ");
            column.append("'").append(String.valueOf(entry.getValue())).append("'");
            if (entry.getValue() instanceof Date) {
                column.append(" as DATETIME");
            } else if (entry.getValue() instanceof String) {
                column.append(" as CHAR");
            } else if (entry.getValue() instanceof Double
                    || entry.getValue() instanceof Float) {
                column.append(" as DOUBLE");
            } else if (entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Long
                    || entry.getValue() instanceof Short
                    || entry.getValue() instanceof Byte) {
                column.append(" as INTEGER");
            }
        }
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
    public static String columnGet(String blob, String name) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        column.append("'").append(name).append("'");
        column.append(" )");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name, Class<?> clazz) {
//        (dyncol_blob, column_nr as type);
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        column.append("'").append(name).append("'");
        if (clazz == Date.class) {
            column.append(" as DATETIME");
        } else if (clazz == String.class) {
            column.append(" as CHAR");
        } else if (clazz == Double.class
                || clazz == Float.class) {
            column.append(" as DOUBLE");
        } else if (clazz == Integer.class
                || clazz == Long.class
                || clazz == Short.class
                || clazz == Byte.class) {
            column.append(" as INTEGER");
        }
        column.append(" )");
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
