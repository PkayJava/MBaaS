package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jooq.DSLContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavascriptControllerUtils {

    public static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    public static String parseStringToString(boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                return value;
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                return value;
            } else {
                return null;
            }
        }
    }

    public static Boolean parseStringToBoolean(boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                if (!"true".equals(value) && !"false".equals(value)) {
                    throw new IllegalArgumentException("is invalid");
                }
                return Boolean.valueOf(value);
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                if (!"true".equals(value) && !"false".equals(value)) {
                    throw new IllegalArgumentException("is invalid");
                }
                return Boolean.valueOf(value);
            } else {
                return null;
            }
        }
    }


    public static String parseBooleanToString(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Boolean) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Boolean) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Long parseStringToLong(boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                return tryLong(value);
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                return tryLong(value);
            } else {
                return null;
            }
        }
    }

    public static String parseLongToString(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Long) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Long) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Double parseStringToDouble(boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                return tryDouble(value);
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                return tryDouble(value);
            } else {
                return null;
            }
        }
    }

    public static String parseDoubleToString(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Double) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Double) {
                    return String.valueOf(value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }


    public static byte[] tryObjectToByteArray(List<Object> value) throws IllegalArgumentException {
        byte[] bytes = new byte[value.size()];
        for (int i = 0; i < value.size(); i++) {
            Object o = value.get(i);
            if (o instanceof Double) {
                String stringValue = String.valueOf(o);
                if (stringValue.endsWith(".0")) {
                    stringValue = stringValue.substring(0, stringValue.length() - 2);
                }
                try {
                    byte v = Byte.valueOf(stringValue);
                    bytes[i] = v;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else {
                throw new IllegalArgumentException("is invalid");
            }
        }
        return bytes;
    }

    public static byte[] parseObjectToByteArray(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof List) {
                    return tryObjectToByteArray((List<Object>) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof List) {
                    return tryObjectToByteArray((List<Object>) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            } else {
                return null;
            }
        }
    }

    public static Object parseObjectToByteArrayArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<byte[]> temp = new ArrayList<>();
        for (Object value : values) {
            byte[] object = parseObjectToByteArray(required, value);
            if (object != null && object.length > 0) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        Object array = Array.newInstance(byte[].class, temp.size());
        for (int i = 0; i < temp.size(); i++) {
            Array.set(array, i, temp.get(i));
        }
        return array;
    }

    public static byte[] parseMultipartFileToByteArray(boolean required, MultipartFile value) throws IllegalArgumentException {
        if (required) {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("is required");
            } else {
                try {
                    return value.getBytes();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        } else {
            if (value != null) {
                try {
                    return value.getBytes();
                } catch (IOException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            } else {
                return null;
            }
        }
    }

    public static Object parseStringToEnum(boolean required, String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                return tryEnum(enumType, enumItems, value);
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                return tryEnum(enumType, enumItems, value);
            } else {
                return null;
            }
        }
    }

    public static String parseEnumToString(boolean required, DateFormat format, String enumType, List<String> enumItems, Object value) throws IllegalArgumentException {
        String stringValue = null;
        if (value != null) {
            if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                stringValue = String.valueOf((Boolean) value);
            } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
                stringValue = String.valueOf((Long) value);
            } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                stringValue = String.valueOf((Double) value);
            } else if (enumType.equals(TypeEnum.Character.getLiteral())
                    || enumType.equals(TypeEnum.String.getLiteral())) {
                stringValue = (String) value;
            } else if (enumType.equals(TypeEnum.Time.getLiteral())) {
                stringValue = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format((Date) value);
            } else if (enumType.equals(TypeEnum.Date.getLiteral())) {
                stringValue = DateFormatUtils.ISO_DATE_FORMAT.format((Date) value);
            } else if (enumType.equals(TypeEnum.DateTime.getLiteral())) {
                if (format == null) {
                    stringValue = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format((Date) value);
                } else {
                    stringValue = format.format((Date) value);
                }
            }
        }
        if (required) {
            if (!Strings.isNullOrEmpty(stringValue)) {
                throw new IllegalArgumentException("is required");
            } else {
                if (!enumItems.contains(stringValue)) {
                    throw new IllegalArgumentException("is invalid");
                }
                return stringValue;
            }
        } else {
            if (!Strings.isNullOrEmpty(stringValue)) {
                if (!enumItems.contains(stringValue)) {
                    throw new IllegalArgumentException("is invalid");
                } else {
                    return stringValue;
                }
            } else {
                return null;
            }
        }
    }

    public static Object parseObjectToEnum(boolean required, String enumType, List<String> enumItems, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        return tryEnum(enumType, enumItems, String.valueOf(value));
                    }
                } else {
                    return tryEnum(enumType, enumItems, String.valueOf(value));
                }
            }
        } else {
            if (value != null) {
                if (value instanceof String) {
                    if (!"".equals(value)) {
                        return tryEnum(enumType, enumItems, (String) value);
                    }
                } else {
                    return tryEnum(enumType, enumItems, String.valueOf(value));
                }
            }
            return null;
        }
    }

    public static Object tryEnum(String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
        return tryEnum(null, enumType, enumItems, value);
    }

    public static Object tryEnum(DateFormat dateFormat, String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
        if (enumType.equals(TypeEnum.Boolean.getLiteral())
                || enumType.equals(TypeEnum.Double.getLiteral())
                || enumType.equals(TypeEnum.String.getLiteral())
                || enumType.equals(TypeEnum.Time.getLiteral())
                || enumType.equals(TypeEnum.Date.getLiteral())
                || enumType.equals(TypeEnum.DateTime.getLiteral())) {
            String newValue = value;
            if (enumType.equals(TypeEnum.Double.getLiteral())) {
                newValue = String.valueOf(Double.valueOf(newValue));
            }
            if (!enumItems.contains(newValue)) {
                throw new IllegalArgumentException("is invalid");
            }
            if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                return Boolean.valueOf(newValue);
            } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                return Double.valueOf(newValue);
            } else if (enumType.equals(TypeEnum.Character.getLiteral()) || enumType.equals(TypeEnum.String.getLiteral())) {
                return newValue;
            } else if (enumType.equals(TypeEnum.Time.getLiteral())) {
                try {
                    return DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(newValue);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Date.getLiteral())) {
                try {
                    return DateFormatUtils.ISO_DATE_FORMAT.parse(newValue);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.DateTime.getLiteral())) {
                try {
                    if (dateFormat == null) {
                        return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(newValue);
                    } else {
                        return dateFormat.parse(newValue);
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
            String newValue = value;
            if (newValue.endsWith(".0")) {
                newValue = newValue.substring(0, value.length() - 2);
            }
            if (!enumItems.contains(newValue)) {
                throw new IllegalArgumentException("is invalid");
            }
            return Long.valueOf(newValue);
        }
        return null;
    }

    public static Date parseStringToTime(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_TIME_NO_T_FORMAT, required, value);
    }

    public static String parseDateTimeToString(boolean required, FastDateFormat format, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Date) {
                    return format.format((Date) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Date) {
                    return format.format((Date) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static String parseDateTimeToString(boolean required, DateFormat format, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Date) {
                    return format.format((Date) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Date) {
                    return format.format((Date) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Date parseStringToDate(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_DATE_FORMAT, required, value);
    }

    public static Date parseStringToDateTime(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT, required, value);
    }

    public static Date parseStringFastDateFormat(FastDateFormat format, boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                try {
                    return format.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                try {
                    return format.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else {
                return null;
            }
        }
    }

    public static Boolean parseObjectToBoolean(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Boolean) {
                    return (Boolean) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Boolean) {
                    return (Boolean) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Boolean[] parseObjectToBooleanArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Boolean> temp = new ArrayList<>();
        for (Object value : values) {
            Boolean object = parseObjectToBoolean(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Boolean[temp.size()]);
    }

    public static Long[] parseObjectToLongArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Long> temp = new ArrayList<>();
        for (Object value : values) {
            Long object = parseObjectToLong(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Long[temp.size()]);
    }

    public static Double[] parseObjectToDoubleArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Double> temp = new ArrayList<>();
        for (Object value : values) {
            Double object = parseObjectToDouble(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Double[temp.size()]);
    }

    public static Date[] parseObjectToDateArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Date> temp = new ArrayList<>();
        for (Object value : values) {
            Date object = parseObjectToDate(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Date[temp.size()]);
    }

    public static Date[] parseObjectToTimeArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Date> temp = new ArrayList<>();
        for (Object value : values) {
            Date object = parseObjectToTime(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Date[temp.size()]);
    }

    public static Date[] parseObjectToDateTimeArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<Date> temp = new ArrayList<>();
        for (Object value : values) {
            Date object = parseObjectToDateTime(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new Date[temp.size()]);
    }

    public static String[] parseObjectToStringArray(boolean required, List<Object> values) throws IllegalArgumentException {
        List<String> temp = new ArrayList<>();
        for (Object value : values) {
            String object = parseObjectToString(required, value);
            if (object != null) {
                temp.add(object);
            }
        }
        if (temp.isEmpty()) {
            return null;
        }
        return temp.toArray(new String[temp.size()]);
    }

    public static Object parseObjectToEnumArray(boolean required, String enumType, List<String> enumItems, List<Object> values) throws IllegalArgumentException {
        List<Object> temp = new ArrayList<>();
        for (Object value : values) {
            Object object = parseObjectToEnum(required, enumType, enumItems, value);
            if (object != null) {
                temp.add(object);
            }
        }
        Object arrays = null;
        if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
            arrays = Array.newInstance(Boolean.class, temp.size());
        } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
            arrays = Array.newInstance(Double.class, temp.size());
        } else if (enumType.equals(TypeEnum.Character.getLiteral()) || enumType.equals(TypeEnum.String.getLiteral())) {
            arrays = Array.newInstance(String.class, temp.size());
        } else if (enumType.equals(TypeEnum.Time.getLiteral())
                || enumType.equals(TypeEnum.Date.getLiteral())
                || enumType.equals(TypeEnum.DateTime.getLiteral())) {
            arrays = Array.newInstance(Date.class, temp.size());
        } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
            arrays = Array.newInstance(Long.class, temp.size());
        }
        for (int i = 0; i < temp.size(); i++) {
            Array.set(arrays, i, temp.get(i));
        }
        return arrays;
    }

    public static String tryLongString(Double value) {
        String doubleString = String.valueOf(value);
        if (doubleString.endsWith(".0")) {
            return doubleString.substring(0, doubleString.length() - 2);
        } else {
            return doubleString;
        }
    }

    public static Long tryObjectToLong(Double value) {
        String stringValue = tryLongString(value);
        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    public static Long parseObjectToLong(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Double) {
                    return tryObjectToLong((Double) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Double) {
                    return tryObjectToLong((Double) value);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Double parseObjectToDouble(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof Double) {
                    return (Double) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof Double) {
                    return (Double) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static String parseObjectToString(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    }
                    return (String) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof String) {
                    return (String) value;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Date parseObjectToTime(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Date parseObjectToDate(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_DATE_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_DATE_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Date parseObjectToDateTime(boolean required, Object value) throws IllegalArgumentException {
        if (required) {
            if (value == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (value != null) {
                if (value instanceof String) {
                    if ("".equals(value)) {
                        throw new IllegalArgumentException("is required");
                    } else {
                        try {
                            return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse((String) value);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException("is invalid");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        return null;
    }

    public static Date parseStringDateFormat(DateFormat format, boolean required, String value) throws IllegalArgumentException {
        if (required) {
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("is required");
            } else {
                try {
                    return format.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (!Strings.isNullOrEmpty(value)) {
                try {
                    return format.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else {
                return null;
            }
        }
    }

    public static Long tryLong(String value) throws IllegalArgumentException {
        try {
            if (value.endsWith(".0")) {
                value = value.substring(0, 2);
            }
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    public static Double tryDouble(String value) throws IllegalArgumentException {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    public static String getPathInfo(HttpServletRequest request) {
        String pathInfo = request.getPathInfo().substring(11);
        if ("".equals(pathInfo)) {
            pathInfo = "/";
        } else {
            if (!"/".equals(pathInfo) && pathInfo.endsWith("/")) {
                pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
            }
        }
        return pathInfo;
    }

    public static byte[] getRequestBodyByteArray(HttpServletRequest request) {
        byte[] requestBody = null;
        if (ServletFileUpload.isMultipartContent(request)) {
            requestBody = ((ContentCachingRequestWrapper) ((AbstractMultipartHttpServletRequest) ((FirewalledRequest) request).getRequest()).getRequest()).getContentAsByteArray();
        } else {
            requestBody = ((ContentCachingRequestWrapper) ((FirewalledRequest) request).getRequest()).getContentAsByteArray();
        }
        if (requestBody == null || requestBody.length == 0) {
            try {
                requestBody = IOUtils.toByteArray(request.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return requestBody;
    }

    public static String getRequestContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType.contains(";")) {
            contentType = StringUtils.split(contentType, ';')[0];
        }
        return contentType;
    }

    public static ApplicationRecord getApplicationRecord(ServletContext servletContext, HostnameRecord hostnameRecord) {
        ApplicationContext applicationContext = ApplicationContext.get(servletContext);
        DSLContext context = applicationContext.getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);
        if (applicationRecord == null) {
            throw new IllegalArgumentException("is invalid");
        }
        return applicationRecord;
    }

    public static JavascriptController.Http getHttp(ScriptEngine scriptEngine, Map<String, Object> restObject, boolean stage) {
        JavascriptController.Http http = null;
        String stageScript = (String) restObject.get(Jdbc.Rest.STAGE_SCRIPT);
        String script = (String) restObject.get(Jdbc.Rest.SCRIPT);
        if (stage) {
            if ((stageScript == null || "".equals(stageScript))) {
                throw new IllegalArgumentException("is invalid");
            } else {
                try {
                    scriptEngine.eval(stageScript);
                } catch (ScriptException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                Invocable invocable = (Invocable) scriptEngine;
                http = invocable.getInterface(JavascriptController.Http.class);
            }
        } else {
            if (script == null || "".equals(script)) {
                throw new IllegalArgumentException("is invalid");
            } else {
                try {
                    scriptEngine.eval(script);
                } catch (ScriptException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                Invocable invocable = (Invocable) scriptEngine;
                http = invocable.getInterface(JavascriptController.Http.class);
            }
        }
        if (http == null) {
            throw new IllegalArgumentException("is invalid");
        }
        return http;
    }

    public static void fetchMetaData(NamedParameterJdbcTemplate named,
                                     JdbcTemplate jdbcTemplate,
                                     Map<String, Object> restObject,
                                     List<Map<String, Object>> requestQueryRecords,
                                     List<Map<String, Object>> requestHeaderRecords,
                                     List<Map<String, Object>> responseHeaderRecords,
                                     Map<String, Map<String, Object>> headerMetaData,
                                     Map<String, Map<String, Object>> queryParameterMetaData,
                                     Map<String, Map<String, Object>> enumMetaData,
                                     Map<String, List<String>> enumItemMetaData) {

        List<String> headerIds = new ArrayList<>();
        List<String> enumIds = new ArrayList<>();
        List<String> queryIds = new ArrayList<>();
        List<String> jsonIds = new ArrayList<>();

        for (Map<String, Object> header : requestHeaderRecords) {
            headerIds.add((String) header.get(Jdbc.RestRequestHeader.HTTP_HEADER_ID));
        }

        for (Map<String, Object> header : responseHeaderRecords) {
            headerIds.add((String) header.get(Jdbc.RestResponseHeader.HTTP_HEADER_ID));
        }

        Map<String, Object> where = null;
        where = new HashMap<>();
        where.put(Jdbc.HttpHeader.HTTP_HEADER_ID, headerIds);
        List<Map<String, Object>> headerRecords = named.queryForList("SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.HTTP_HEADER_ID + " in (:" + Jdbc.HttpHeader.HTTP_HEADER_ID + ")", where);
        for (Map<String, Object> header : headerRecords) {
            if (header.get(Jdbc.HttpHeader.ENUM_ID) != null && !"".equals(header.get(Jdbc.HttpHeader.ENUM_ID))) {
                if (!enumIds.contains((String) header.get(Jdbc.HttpHeader.ENUM_ID))) {
                    enumIds.add((String) header.get(Jdbc.HttpHeader.ENUM_ID));
                }
            }
        }

        for (Map<String, Object> headerRecord : headerRecords) {
            headerMetaData.put((String) headerRecord.get(Jdbc.HttpHeader.HTTP_HEADER_ID), headerRecord);
        }

        for (Map<String, Object> query : requestQueryRecords) {
            if (!queryIds.contains((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID))) {
                queryIds.add((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID));
            }
        }

        where = new HashMap<>();
        where.put(Jdbc.HttpQuery.HTTP_QUERY_ID, queryIds);
        List<Map<String, Object>> queryRecords = named.queryForList("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " in (:" + Jdbc.HttpQuery.HTTP_QUERY_ID + ")", where);
        for (Map<String, Object> queryRecord : queryRecords) {
            queryParameterMetaData.put((String) queryRecord.get(Jdbc.HttpQuery.HTTP_QUERY_ID), queryRecord);
        }

        for (Map<String, Object> query : queryRecords) {
            if (query.get(Jdbc.HttpQuery.ENUM_ID) != null && !"".equals(query.get(Jdbc.HttpQuery.ENUM_ID))) {
                if (!enumIds.contains((String) query.get(Jdbc.HttpQuery.ENUM_ID))) {
                    enumIds.add((String) query.get(Jdbc.HttpQuery.ENUM_ID));
                }
            }
        }

        if (restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
            if (!enumIds.contains((String) restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                enumIds.add((String) restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
            }
        }

        String method = (String) restObject.get(Jdbc.Rest.METHOD);

        Map<String, Object> requestBodyRecord = null;
        if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
            if (restObject.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID) != null && !"".equals(restObject.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                if (!enumIds.contains((String) restObject.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                    enumIds.add((String) restObject.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID));
                }
            }
            if (restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID) != null && !"".equals(restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID))) {
                requestBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID));
            }
        }

        Map<String, Object> responseBodyRecord = null;
        if (restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID) != null && !"".equals(restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID))) {
            responseBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID));
        }
        if (restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
            if (!enumIds.contains((String) restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                enumIds.add((String) restObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
            }
        }

        if (requestBodyRecord != null) {
            if (!jsonIds.contains((String) requestBodyRecord.get(Jdbc.Json.JSON_ID))) {
                jsonIds.add((String) requestBodyRecord.get(Jdbc.Json.JSON_ID));
            }
            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBodyRecord.get(Jdbc.Json.JSON_ID));
            if (jsonFields != null && !jsonFields.isEmpty()) {
                for (Map<String, Object> jsonField : jsonFields) {
                    processJsonField(jdbcTemplate, jsonIds, enumIds, jsonField);
                }
            }
        }

        if (responseBodyRecord != null) {
            if (!jsonIds.contains((String) responseBodyRecord.get(Jdbc.Json.JSON_ID))) {
                jsonIds.add((String) responseBodyRecord.get(Jdbc.Json.JSON_ID));
            }
            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", responseBodyRecord.get(Jdbc.Json.JSON_ID));
            if (jsonFields != null && !jsonFields.isEmpty()) {
                for (Map<String, Object> jsonField : jsonFields) {
                    processJsonField(jdbcTemplate, jsonIds, enumIds, jsonField);
                }
            }
        }

        List<Map<String, Object>> enumRecords = new ArrayList<>();
        if (!enumIds.isEmpty()) {
            where = new HashMap<>();
            where.put(Jdbc.Enum.ENUM_ID, enumIds);
            enumRecords = named.queryForList("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " in (:" + Jdbc.Enum.ENUM_ID + ")", where);
        }

        for (Map<String, Object> enumRecord : enumRecords) {
            enumMetaData.put((String) enumRecord.get(Jdbc.Enum.ENUM_ID), enumRecord);
        }

        List<Map<String, Object>> enumItemRecords = new ArrayList<>();
        if (!enumIds.isEmpty()) {
            where = new HashMap<>();
            where.put(Jdbc.EnumItem.ENUM_ID, enumIds);
            enumItemRecords = named.queryForList("SELECT * FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ID + " in (:" + Jdbc.EnumItem.ENUM_ID + ")", where);
        }

        for (Map<String, Object> enumItemRecord : enumItemRecords) {
            String item = (String) enumItemRecord.get(Jdbc.EnumItem.VALUE);
            if (!enumItemMetaData.containsKey((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID))) {
                List<String> items = new ArrayList<>();
                items.add(item);
                enumItemMetaData.put((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID), items);
            } else {
                List<String> items = enumItemMetaData.get((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID));
                items.add(item);
            }
        }
    }

    public static JdbcTemplate getJdbcTemplate(ServletContext servletContext, ApplicationRecord applicationRecord) {
        ApplicationContext applicationContext = ApplicationContext.get(servletContext);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        JdbcTemplate jdbcTemplate = applicationContext.getApplicationDataSource().getJdbcTemplate(applicationRecord.getCode(), jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("is invalid");
        }
        return jdbcTemplate;
    }

    public static ScriptEngine getScriptEngine(ServletContext servletContext) {
        ApplicationContext applicationContext = ApplicationContext.get(servletContext);
        DSLContext context = applicationContext.getDSLContext();
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new JavaFilter(context));
        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        JavascripUtils.eval(engine);
        return engine;
    }

    public static Map<String, Object> getRestObject(JdbcTemplate jdbcTemplate, String pathInfo, HttpServletRequest request) {
        Map<String, Object> restObject = null;
        try {
            restObject = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.REST + " WHERE " + Jdbc.Rest.PATH + " = ? AND " + Jdbc.Rest.METHOD + " = ?", pathInfo, StringUtils.upperCase(request.getMethod()));
        } catch (EmptyResultDataAccessException e) {
        }
        if (restObject == null) {
            throw new IllegalArgumentException("is invalid");
        }
        return restObject;
    }

    public static HostnameRecord getHostnameRecord(ServletContext servletContext, HttpServletRequest request) {
        ApplicationContext applicationContext = ApplicationContext.get(servletContext);
        DSLContext context = applicationContext.getDSLContext();
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(request.getServerName())).fetchOneInto(hostnameTable);
        if (hostnameRecord == null) {
            throw new IllegalArgumentException("is invalid");
        }
        return hostnameRecord;
    }

    public static void validateJsonField(JdbcTemplate jdbcTemplate, Map<String, Object> iRequestBody, Map<String, Object> error, Map<String, Object> json, Map<String, Object> jsonField, Map<String, List<String>> enumItemDictionary, Map<String, Map<String, Object>> enumDictionary) {
        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);
        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
        Boolean required = (Boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
        try {
            if (TypeEnum.Boolean.getLiteral().equals(type)) {
                Boolean value = parseObjectToBoolean(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Long.getLiteral().equals(type)) {
                Long value = parseObjectToLong(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Double.getLiteral().equals(type)) {
                Double value = parseObjectToDouble(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.String.getLiteral().equals(type)) {
                String value = parseObjectToString(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Time.getLiteral().equals(type)) {
                Date value = parseObjectToTime(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Date.getLiteral().equals(type)) {
                Date value = parseObjectToDate(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                Date value = parseObjectToDateTime(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                Map<String, Object> enumRecord = enumDictionary.get(enumId);
                String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                List<String> enumItemValues = enumItemDictionary.get(enumId);
                Object value = parseObjectToEnum(required, enumType, enumItemValues, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.Map.getLiteral().equals(type)) {
                try {
                    Map<String, Object> fieldJson = (Map<String, Object>) json.get(name);
                    if (fieldJson != null) {
                        Map<String, Object> i = new HashMap<>();
                        Map<String, Object> fieldError = new HashMap<>();
                        List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                        for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                            validateJsonField(jdbcTemplate, i, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                        }
                        iRequestBody.put(name, i);
                        if (!fieldError.isEmpty()) {
                            error.put(name, fieldError);
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.File.getLiteral().equals(type)) {
                byte[] value = parseObjectToByteArray(required, json.get(name));
                iRequestBody.put(name, value);
            } else if (TypeEnum.List.getLiteral().equals(type)) {
                if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                    try {
                        Boolean[] value = parseObjectToBooleanArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                    try {
                        Long[] value = parseObjectToLongArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                    try {
                        Double[] value = parseObjectToDoubleArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.String.getLiteral().equals(subType)) {
                    try {
                        String[] value = parseObjectToStringArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToTimeArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToDateArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToDateTimeArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                    Map<String, Object> enumRecord = enumDictionary.get(enumId);
                    String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    List<String> enumItemValues = enumItemDictionary.get(enumId);
                    try {
                        Object value = parseObjectToEnumArray(required, enumType, enumItemValues, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.File.getLiteral().equals(subType)) {
                    try {
                        Object value = parseObjectToByteArrayArray(required, (List<Object>) json.get(name));
                        iRequestBody.put(name, value);
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Map.getLiteral().equals(subType)) {
                    try {
                        List<Map<String, Object>> fieldJsons = (List<Map<String, Object>>) json.get(name);
                        if (fieldJsons != null) {
                            List<Map<String, Object>> is = new ArrayList<>();
                            for (Map<String, Object> fieldJson : fieldJsons) {
                                if (fieldJson != null) {
                                    Map<String, Object> i = new HashMap<>();
                                    Map<String, Object> fieldError = new HashMap<>();
                                    List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                                    for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                                        validateJsonField(jdbcTemplate, i, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                                    }
                                    is.add(i);
                                    if (!fieldError.isEmpty()) {
                                        error.put(name, fieldError);
                                    }
                                } else {
                                    error.put(name, "is required");
                                    break;
                                }
                            }
                            iRequestBody.put(name, is);
                        } else {
                            error.put(name, "is required");
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            error.put(name, e.getMessage());
        }
    }

    /**
     * process to build enumId
     *
     * @param jdbcTemplate
     * @param jsonIdList
     * @param enumIdList
     * @param jsonField
     */
    public static void processJsonField(JdbcTemplate jdbcTemplate, List<String> jsonIdList, List<String> enumIdList, Map<String, Object> jsonField) {
        if (jsonField.get(Jdbc.JsonField.ENUM_ID) != null && !"".equals(jsonField.get(Jdbc.JsonField.ENUM_ID))) {
            if (!enumIdList.contains((String) jsonField.get(Jdbc.JsonField.ENUM_ID))) {
                enumIdList.add((String) jsonField.get(Jdbc.JsonField.ENUM_ID));
            }
        }
        if (jsonField.get(Jdbc.JsonField.MAP_JSON_ID) != null && !"".equals(jsonField.get(Jdbc.JsonField.MAP_JSON_ID))) {
            if (!jsonIdList.contains((String) jsonField.get(Jdbc.JsonField.MAP_JSON_ID))) {
                jsonIdList.add((String) jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                List<Map<String, Object>> fields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                if (fields != null && !fields.isEmpty()) {
                    for (Map<String, Object> field : fields) {
                        processJsonField(jdbcTemplate, jsonIdList, enumIdList, field);
                    }
                }
            }
        }
    }

    public static void parseParameter(Map<String, List<String>> dictionary, String name, String value) {
        if (!dictionary.containsKey(name)) {
            List<String> values = new ArrayList<>();
            values.add(value);
            dictionary.put(name, values);
        } else {
            List<String> values = dictionary.get(name);
            values.add(value);
        }
    }


    public static Object queryRequestDictionary(HttpServletRequest request,
                                                Gson gson,
                                                byte[] requestBody,
                                                String contentType,
                                                Map<String, List<String>> requestQueryParameterDictionary,
                                                Map<String, List<String>> requestHeaderDictionary,
                                                Map<String, List<String>> requestBodyApplicationFormUrlencodedDictionary,
                                                Map<String, List<String>> requestBodyMultipartFormDataStringItemDictionary, Map<String, List<MultipartFile>> requestBodyMultipartFormDataFileItemDictionary) {
        String queryString = request.getQueryString();
        if (queryString != null && !"".equals(queryString)) {
            String[] params = StringUtils.split(queryString, '&');
            for (String param : params) {
                String tmp[] = StringUtils.split(param, '=');
                String name = tmp[0];
                String value = tmp[1];
                parseParameter(requestQueryParameterDictionary, name, value);
            }
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> tempValues = request.getHeaders(headerName);
            List<String> headerValues = new ArrayList<>();
            while (tempValues.hasMoreElements()) {
                headerValues.add(tempValues.nextElement());
            }
            requestHeaderDictionary.put(headerName, headerValues);
        }

        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)) {
            String bodyString = null;
            if (requestBody != null && requestBody.length > 0) {
                try {
                    bodyString = URLDecoder.decode(IOUtils.toString(requestBody, "UTF-8"), "UTF-8");
                } catch (IOException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if (bodyString != null && !"".equals(bodyString)) {
                String[] params = StringUtils.split(bodyString, '&');
                for (String param : params) {
                    String tmp[] = StringUtils.split(param, '=');
                    String name = tmp[0];
                    String value = null;
                    if (tmp.length >= 2) {
                        value = tmp[1];
                    }
                    parseParameter(requestBodyApplicationFormUrlencodedDictionary, name, value);
                }
            }
        }

        if (ServletFileUpload.isMultipartContent(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) ((FirewalledRequest) request).getRequest();
            if (multipartHttpServletRequest.getParameterMap() != null && !multipartHttpServletRequest.getParameterMap().isEmpty()) {
                for (Map.Entry<String, String[]> item : multipartHttpServletRequest.getParameterMap().entrySet()) {
                    if (!requestQueryParameterDictionary.containsKey(item.getKey())) {
                        requestBodyMultipartFormDataStringItemDictionary.put(item.getKey(), Arrays.asList(item.getValue()));
                    }
                }
            }
            if (multipartHttpServletRequest.getFileMap() != null && !multipartHttpServletRequest.getFileMap().isEmpty()) {
                for (Map.Entry<String, MultipartFile> item : multipartHttpServletRequest.getFileMap().entrySet()) {
                    if (!requestBodyMultipartFormDataFileItemDictionary.containsKey(item.getKey())) {
                        List<MultipartFile> values = new ArrayList<>();
                        values.add(item.getValue());
                        requestBodyMultipartFormDataFileItemDictionary.put(item.getKey(), values);
                    } else {
                        List<MultipartFile> values = requestBodyMultipartFormDataFileItemDictionary.get(item.getKey());
                        values.add(item.getValue());
                    }
                }
            }
        }

        String json = null;
        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            if (requestBody != null && requestBody.length > 0) {
                try {
                    json = new String(requestBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }
        if (json != null && !"".equals(json)) {
            return gson.fromJson(json, Object.class);
        }
        return null;
    }

    public static void validationRequestQueryParameter(List<Map<String, Object>> requestQueryRecords,
                                                       Map<String, Map<String, Object>> queryParameterMetaData,
                                                       Map<String, Map<String, Object>> enumMetaData,
                                                       Map<String, List<String>> enumItemMetaData,
                                                       Map<String, List<String>> requestQueryParameterDictionary,
                                                       Map<String, Object> newQueryParameter,
                                                       Map<String, String> requestQueryErrors) {
        for (Map<String, Object> requestQueryRecord : requestQueryRecords) {
            String queryId = (String) requestQueryRecord.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID);
            Boolean required = (Boolean) requestQueryRecord.get(Jdbc.RestRequestQuery.REQUIRED);
            Map<String, Object> httpQuery = queryParameterMetaData.get(queryId);
            String name = (String) httpQuery.get(Jdbc.HttpQuery.NAME);
            String enumId = (String) httpQuery.get(Jdbc.HttpQuery.ENUM_ID);
            String type = (String) httpQuery.get(Jdbc.HttpQuery.TYPE);
            String subType = (String) httpQuery.get(Jdbc.HttpQuery.SUB_TYPE);

            List<String> strings = new ArrayList<>();
            String newType;
            if (!TypeEnum.List.getLiteral().equals(type)) {
                if (requestQueryParameterDictionary.get(name) != null && !requestQueryParameterDictionary.get(name).isEmpty()) {
                    strings.add(requestQueryParameterDictionary.get(name).get(0));
                }
                newType = type;
            } else {
                if (requestQueryParameterDictionary.get(name) != null && !requestQueryParameterDictionary.get(name).isEmpty()) {
                    strings.addAll(requestQueryParameterDictionary.get(name));
                }
                newType = subType;
            }
            if (required) {
                if (strings.isEmpty()) {
                    requestQueryErrors.put(name, "is required");
                }
            }
            Map<String, Object> enumRecord = null;
            String enumType = null;
            List<String> enumItems = null;
            if (TypeEnum.Enum.getLiteral().equals(newType)) {
                enumRecord = enumMetaData.get(enumId);
                enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                enumItems = enumItemMetaData.get(enumId);
            }
            try {
                List<Object> values = new ArrayList<>();
                for (String string : strings) {
                    Object value = null;
                    if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                        value = parseStringToBoolean(required, string);
                    } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                        value = parseStringToLong(required, string);
                    } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                        value = parseStringToDouble(required, string);
                    } else if (TypeEnum.String.getLiteral().equals(newType)) {
                        value = JavascriptControllerUtils.parseStringToString(required, string);
                    } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                        value = parseStringToTime(required, string);
                    } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                        value = parseStringToDate(required, string);
                    } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                        value = parseStringToDateTime(required, string);
                    } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                        value = parseStringToEnum(required, enumType, enumItems, string);
                    }
                    if (value != null) {
                        values.add(value);
                    }
                }

                if (TypeEnum.Boolean.getLiteral().equals(type)
                        || TypeEnum.Long.getLiteral().equals(type)
                        || TypeEnum.Double.getLiteral().equals(type)
                        || TypeEnum.String.getLiteral().equals(type)
                        || TypeEnum.Time.getLiteral().equals(type)
                        || TypeEnum.Date.getLiteral().equals(type)
                        || TypeEnum.DateTime.getLiteral().equals(type)
                        || TypeEnum.Enum.getLiteral().equals(type)) {
                    newQueryParameter.put(name, values.isEmpty() ? null : values.get(0));
                } else if (TypeEnum.List.getLiteral().equals(type)) {
                    Object newValues = null;
                    if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Boolean.class, values.size());
                    } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Long.class, values.size());
                    } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Double.class, values.size());
                    } else if (TypeEnum.String.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(String.class, values.size());
                    } else if (TypeEnum.Time.getLiteral().equals(subType)
                            || TypeEnum.Date.getLiteral().equals(subType)
                            || TypeEnum.DateTime.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Date.class, values.size());
                    } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                        if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                            newValues = Array.newInstance(Boolean.class, values.size());
                        } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
                            newValues = Array.newInstance(Long.class, values.size());
                        } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                            newValues = Array.newInstance(Double.class, values.size());
                        } else if (enumType.equals(TypeEnum.Character.getLiteral())
                                || enumType.equals(TypeEnum.String.getLiteral())) {
                            newValues = Array.newInstance(String.class, values.size());
                        } else if (enumType.equals(TypeEnum.Time.getLiteral())
                                || enumType.equals(TypeEnum.Date.getLiteral())
                                || enumType.equals(TypeEnum.DateTime.getLiteral())) {
                            newValues = Array.newInstance(Date.class, values.size());
                        }
                    }
                    for (int i = 0; i < values.size(); i++) {
                        Array.set(newValues, i, values.get(i));
                    }
                    newQueryParameter.put(name, newValues);
                }
            } catch (IllegalArgumentException e) {
                requestQueryErrors.put(name, e.getMessage());
            }
        }
    }

    public static void validationRequestHeader(List<Map<String, Object>> requestHeaderRecords,
                                               Map<String, Map<String, Object>> headerMetaData,
                                               Map<String, Map<String, Object>> enumMetaData,
                                               Map<String, List<String>> enumItemMetaData,
                                               Map<String, List<String>> requestHeaderDictionary,
                                               Map<String, Object> newRequestHeader,
                                               Map<String, String> requestHeaderErrors) {
        for (Map<String, Object> requestHeaderRecord : requestHeaderRecords) {
            String headerId = (String) requestHeaderRecord.get(Jdbc.RestRequestHeader.HTTP_HEADER_ID);
            Boolean required = (Boolean) requestHeaderRecord.get(Jdbc.RestRequestHeader.REQUIRED);
            Map<String, Object> httpHeader = headerMetaData.get(headerId);
            String name = (String) httpHeader.get(Jdbc.HttpHeader.NAME);
            String enumId = (String) httpHeader.get(Jdbc.HttpHeader.ENUM_ID);
            String type = (String) httpHeader.get(Jdbc.HttpHeader.TYPE);
            String subType = (String) httpHeader.get(Jdbc.HttpHeader.SUB_TYPE);

            String newType;
            List<String> strings = new ArrayList<>();
            if (!TypeEnum.List.getLiteral().equals(type)) {
                if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                    strings.add(requestHeaderDictionary.get(name).get(0));
                }
                newType = type;
            } else {
                if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                    strings.addAll(requestHeaderDictionary.get(name));
                }
                newType = subType;
            }
            if (required) {
                if (strings.isEmpty()) {
                    requestHeaderErrors.put(name, "is required");
                }
            }
            Map<String, Object> enumRecord = null;
            String enumType = null;
            List<String> enumItems = null;
            if (TypeEnum.Enum.getLiteral().equals(newType)) {
                enumRecord = enumMetaData.get(enumId);
                enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                enumItems = enumItemMetaData.get(enumId);
            }
            try {

                List<Object> values = new ArrayList<>();
                for (String string : strings) {
                    Object value = null;
                    if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                        value = parseStringToBoolean(required, string);
                    } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                        value = parseStringToLong(required, string);
                    } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                        value = parseStringToDouble(required, string);
                    } else if (TypeEnum.String.getLiteral().equals(newType)) {
                        value = JavascriptControllerUtils.parseStringToString(required, string);
                    } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                        value = parseStringToTime(required, string);
                    } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                        value = parseStringToDate(required, string);
                    } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                        value = parseStringDateFormat(HTTP_DATE_FORMAT, required, string);
                    } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                        value = parseStringToEnum(required, enumType, enumItems, string);
                    }
                    if (value != null) {
                        values.add(value);
                    }
                }

                if (TypeEnum.Boolean.getLiteral().equals(type)
                        || TypeEnum.Long.getLiteral().equals(type)
                        || TypeEnum.Double.getLiteral().equals(type)
                        || TypeEnum.String.getLiteral().equals(type)
                        || TypeEnum.Time.getLiteral().equals(type)
                        || TypeEnum.Date.getLiteral().equals(type)
                        || TypeEnum.DateTime.getLiteral().equals(type)
                        || TypeEnum.Enum.getLiteral().equals(type)) {
                    newRequestHeader.put(name, values.isEmpty() ? null : values.get(0));
                } else if (TypeEnum.List.getLiteral().equals(type)) {
                    Object newValues = null;
                    if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Boolean.class, values.size());
                    } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Long.class, values.size());
                    } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Double.class, values.size());
                    } else if (TypeEnum.String.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(String.class, values.size());
                    } else if (TypeEnum.Time.getLiteral().equals(subType)
                            || TypeEnum.Date.getLiteral().equals(subType)
                            || TypeEnum.DateTime.getLiteral().equals(subType)) {
                        newValues = Array.newInstance(Date.class, values.size());
                    } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                        if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                            newValues = Array.newInstance(Boolean.class, values.size());
                        } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
                            newValues = Array.newInstance(Long.class, values.size());
                        } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                            newValues = Array.newInstance(Double.class, values.size());
                        } else if (enumType.equals(TypeEnum.Character.getLiteral())
                                || enumType.equals(TypeEnum.String.getLiteral())) {
                            newValues = Array.newInstance(String.class, values.size());
                        } else if (enumType.equals(TypeEnum.Time.getLiteral())
                                || enumType.equals(TypeEnum.Date.getLiteral())
                                || enumType.equals(TypeEnum.DateTime.getLiteral())) {
                            newValues = Array.newInstance(Date.class, values.size());
                        }
                    }
                    for (int i = 0; i < values.size(); i++) {
                        Array.set(newValues, i, values.get(i));
                    }
                    newRequestHeader.put(name, newValues);
                }

            } catch (IllegalArgumentException e) {
                requestHeaderErrors.put(name, e.getMessage());
            }
        }
    }

    public static void restObjectExtraction(JdbcTemplate jdbcTemplate, Map<String, Object> restObject, Map<String, Object> requestBodyObject, Map<String, Object> responseBodyObject) {
        String method = (String) restObject.get(Jdbc.Rest.METHOD);

        if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
            if (restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID) != null && !"".equals(restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID))) {
                Map<String, Object> requestBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID));
                if (requestBodyRecord != null) {
                    requestBodyObject.putAll(requestBodyRecord);
                }
            }
        }

        if (restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID) != null && !"".equals(restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID))) {
            Map<String, Object> responseBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID));
            if (responseBodyRecord != null) {
                responseBodyObject.putAll(responseBodyRecord);
            }
        }
    }


    public static void validateBoolean(boolean required, Object object) {
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof Boolean) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof Boolean) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateLong(boolean required, Object object) {
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof Integer || object instanceof Long) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof Integer || object instanceof Long) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateDouble(boolean required, Object object) {
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof Float || object instanceof Float) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof Float || object instanceof Float) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateString(boolean required, Object object) {
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof String) {
                    if ("".equals(object)) {
                        throw new IllegalArgumentException("is required");
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof String) {
                    if ("".equals(object)) {
                        throw new IllegalArgumentException("is required");
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateDate(boolean required, Object object) {
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof Date) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof Date) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateEnum(boolean required, String enumType, List<String> enumItems, Object object) {
        String stringValue = null;
        if (object != null) {
            if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                if (object instanceof Boolean) {
                    stringValue = String.valueOf(object);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
                if (object instanceof Integer || object instanceof Long) {
                    stringValue = String.valueOf(object);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                if (object instanceof Float || object instanceof Double) {
                    stringValue = String.valueOf(object);
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Character.getLiteral())
                    || enumType.equals(TypeEnum.String.getLiteral())) {
                if (object instanceof String) {
                    stringValue = (String) object;
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Time.getLiteral())
                    || enumType.equals(TypeEnum.Date.getLiteral())
                    || enumType.equals(TypeEnum.DateTime.getLiteral())) {
                if (object instanceof Date) {
                    if (enumType.equals(TypeEnum.Time.getLiteral())) {
                        stringValue = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format((Date) object);
                    } else if (enumType.equals(TypeEnum.Date.getLiteral())) {
                        stringValue = DateFormatUtils.ISO_DATE_FORMAT.format((Date) object);
                    } else if (enumType.equals(TypeEnum.DateTime.getLiteral())) {
                        stringValue = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format((Date) object);
                    }
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
        // TODO : checked
        if (required) {
            if (object == null) {
                throw new IllegalArgumentException("is required");
            } else {
                if (object instanceof Date) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else {
            if (object != null) {
                if (object instanceof Date) {
                } else {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        }
    }

    public static void validateMapField(JdbcTemplate jdbcTemplate, Map<String, Object> error, Map<String, Object> json, Map<String, Object> jsonField, Map<String, List<String>> enumItemDictionary, Map<String, Map<String, Object>> enumDictionary) {
        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);
        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
        Boolean required = (Boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
        try {
            if (TypeEnum.Boolean.getLiteral().equals(type)) {
                validateBoolean(required, json.get(name));
            } else if (TypeEnum.Long.getLiteral().equals(type)) {
                validateLong(required, json.get(name));
            } else if (TypeEnum.Double.getLiteral().equals(type)) {
                validateDouble(required, json.get(name));
            } else if (TypeEnum.String.getLiteral().equals(type)) {
                validateString(required, json.get(name));
            } else if (TypeEnum.Time.getLiteral().equals(type)
                    || TypeEnum.Date.getLiteral().equals(type)
                    || TypeEnum.DateTime.getLiteral().equals(type)) {
                validateDate(required, json.get(name));
            } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                Map<String, Object> enumRecord = enumDictionary.get(enumId);
                String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                List<String> enumItemValues = enumItemDictionary.get(enumId);
                Object value = parseObjectToEnum(required, enumType, enumItemValues, json.get(name));
            } else if (TypeEnum.Map.getLiteral().equals(type)) {
                try {
                    Map<String, Object> fieldJson = (Map<String, Object>) json.get(name);
                    if (fieldJson != null) {
                        Map<String, Object> fieldError = new HashMap<>();
                        List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                        for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                            validateMapField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                        }
                        if (!fieldError.isEmpty()) {
                            error.put(name, fieldError);
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.File.getLiteral().equals(type)) {
                byte[] value = parseObjectToByteArray(required, json.get(name));
            } else if (TypeEnum.List.getLiteral().equals(type)) {
                if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                    try {
                        Boolean[] value = parseObjectToBooleanArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                    try {
                        Long[] value = parseObjectToLongArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                    try {
                        Double[] value = parseObjectToDoubleArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.String.getLiteral().equals(subType)) {
                    try {
                        String[] value = parseObjectToStringArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToTimeArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToDateArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                    try {
                        Date[] value = parseObjectToDateTimeArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                    Map<String, Object> enumRecord = enumDictionary.get(enumId);
                    String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    List<String> enumItemValues = enumItemDictionary.get(enumId);
                    try {
                        Object value = parseObjectToEnumArray(required, enumType, enumItemValues, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.File.getLiteral().equals(subType)) {
                    try {
                        Object value = parseObjectToByteArrayArray(required, (List<Object>) json.get(name));
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Map.getLiteral().equals(subType)) {
                    try {
                        List<Map<String, Object>> fieldJsons = (List<Map<String, Object>>) json.get(name);
                        if (fieldJsons != null) {
                            for (Map<String, Object> fieldJson : fieldJsons) {
                                if (fieldJson != null) {
                                    Map<String, Object> fieldError = new HashMap<>();
                                    List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                                    for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                                        validateMapField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                                    }
                                    if (!fieldError.isEmpty()) {
                                        error.put(name, fieldError);
                                    }
                                } else {
                                    error.put(name, "is required");
                                    break;
                                }
                            }
                        } else {
                            error.put(name, "is required");
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            error.put(name, e.getMessage());
        }
    }


}
