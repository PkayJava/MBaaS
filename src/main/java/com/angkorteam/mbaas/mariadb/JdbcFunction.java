package com.angkorteam.mbaas.mariadb;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public class JdbcFunction {

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, Pair pair) {
        return columnAdd(blob, Arrays.asList(pair));
    }

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, Pair... pairs) {
        return columnAdd(blob, Arrays.asList(pairs));
    }

    /**
     * Adds or updates dynamic columns
     */
    public static String columnAdd(String blob, List<Pair> pairs) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_ADD ( ").append(blob).append(", ");
        for (Pair pair : pairs) {
            column.append("\"").append(pair.getName()).append("\"");
            column.append(", ");
            column.append("\"").append(String.valueOf(pair.getValue())).append("\"");
            if (pair.getValue() instanceof Date) {
                column.append(" as DATETIME");
            } else if (pair.getValue() instanceof String) {
                column.append(" as CHAR");
            } else if (pair.getValue() instanceof Double
                    || pair.getValue() instanceof Float) {
                column.append(" as DOUBLE");
            } else if (pair.getValue() instanceof Integer
                    || pair.getValue() instanceof Long
                    || pair.getValue() instanceof Short
                    || pair.getValue() instanceof Byte) {
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
    public static String columnCreate(String blob, Pair pair) {
        return columnCreate(blob, Arrays.asList(pair));
    }

    /**
     * Returns a dynamic columns blob
     */
    public static String columnCreate(String blob, Pair... pairs) {
        return columnCreate(blob, Arrays.asList(pairs));
    }

    /**
     * Returns a dynamic columns blob
     */
    public static String columnCreate(String blob, List<Pair> pairs) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_CREATE ( ");
        for (Pair pair : pairs) {
            column.append("\"").append(pair.getName()).append("\"");
            column.append(", ");
            column.append("\"").append(String.valueOf(pair.getValue())).append("\"");
            if (pair.getValue() instanceof Date) {
                column.append(" as DATETIME");
            } else if (pair.getValue() instanceof String) {
                column.append(" as CHAR");
            } else if (pair.getValue() instanceof Double
                    || pair.getValue() instanceof Float) {
                column.append(" as DOUBLE");
            } else if (pair.getValue() instanceof Integer
                    || pair.getValue() instanceof Long
                    || pair.getValue() instanceof Short
                    || pair.getValue() instanceof Byte) {
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
            column.append(", ").append("\"").append(name).append("\"");
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
        column.append("\"").append(name).append("\"");
        column.append(" )");
        return column.toString();
    }

    /**
     * Gets a dynamic column value by name
     */
    public static String columnGet(String blob, String name) {
        StringBuffer column = new StringBuffer();
        column.append("COLUMN_GET ( ").append(blob).append(", ");
        column.append("\"").append(name).append("\"");
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
        column.append("\"").append(name).append("\"");
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
