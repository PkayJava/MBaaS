package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by socheat on 2/27/16.
 */
@Controller
@RequestMapping(path = "/javascript")
public class JavascriptController {

    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private Gson gson;

    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    private Type listType = new TypeToken<List<Object>>() {
    }.getType();

    private static final Logger LOGGER = LoggerFactory.getLogger(com.angkorteam.mbaas.server.MBaaS.class);

    protected Boolean parseStringToBoolean(boolean required, String value) throws IllegalArgumentException {
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

    protected Long parseStringToLong(boolean required, String value) throws IllegalArgumentException {
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

    protected Double parseStringToDouble(boolean required, String value) throws IllegalArgumentException {
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

    protected String parseStringToString(boolean required, String value) throws IllegalArgumentException {
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

    protected byte[] tryObjectToByteArray(List<Object> value) throws IllegalArgumentException {
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

    protected byte[] parseObjectToByteArray(boolean required, Object value) throws IllegalArgumentException {
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

    protected byte[] parseMultipartFileToByteArray(boolean required, MultipartFile value) throws IllegalArgumentException {
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

    protected Object parseStringToEnum(boolean required, String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
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

    protected Object parseObjectToEnum(boolean required, String enumType, List<String> enumItems, Object value) throws IllegalArgumentException {
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

    protected Object tryEnum(String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
        return tryEnum(null, enumType, enumItems, value);
    }

    protected Object tryEnum(DateFormat dateFormat, String enumType, List<String> enumItems, String value) throws IllegalArgumentException {
        if (enumType.equals(TypeEnum.Boolean.getLiteral())
                || enumType.equals(TypeEnum.Double.getLiteral())
                || enumType.equals(TypeEnum.String.getLiteral())
                || enumType.equals(TypeEnum.Time.getLiteral())
                || enumType.equals(TypeEnum.Date.getLiteral())
                || enumType.equals(TypeEnum.DateTime.getLiteral())) {
            if (!enumItems.contains(value)) {
                throw new IllegalArgumentException("is invalid");
            }
            if (enumType.equals(TypeEnum.Boolean.getLiteral())) {
                return Boolean.valueOf(value);
            } else if (enumType.equals(TypeEnum.Double.getLiteral())) {
                return Double.valueOf(value);
            } else if (enumType.equals(TypeEnum.Character.getLiteral()) || enumType.equals(TypeEnum.String.getLiteral())) {
                return value;
            } else if (enumType.equals(TypeEnum.Time.getLiteral())) {
                try {
                    return DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.Date.getLiteral())) {
                try {
                    return DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            } else if (enumType.equals(TypeEnum.DateTime.getLiteral())) {
                try {
                    if (dateFormat == null) {
                        return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                    } else {
                        return dateFormat.parse(value);
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("is invalid");
                }
            }
        } else if (enumType.equals(TypeEnum.Long.getLiteral())) {
            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }
            if (!enumItems.contains(value)) {
                throw new IllegalArgumentException("is invalid");
            }
            return Long.valueOf(value);
        }
        return null;
    }

    protected Date parseStringToTime(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_TIME_NO_T_FORMAT, required, value);
    }

    protected Date parseStringToDate(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_DATE_FORMAT, required, value);
    }

    protected Date parseStringToDateTime(boolean required, String value) throws IllegalArgumentException {
        return parseStringFastDateFormat(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT, required, value);
    }

    protected Date parseStringFastDateFormat(FastDateFormat format, boolean required, String value) throws IllegalArgumentException {
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

    protected Boolean parseObjectToBoolean(boolean required, Object value) throws IllegalArgumentException {
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

    protected Boolean[] parseObjectToBooleanArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Long[] parseObjectToLongArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Double[] parseObjectToDoubleArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Date[] parseObjectToDateArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Date[] parseObjectToTimeArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Date[] parseObjectToDateTimeArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected String[] parseObjectToStringArray(boolean required, List<Object> values) throws IllegalArgumentException {
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

    protected Object parseObjectToEnumArray(boolean required, String enumType, List<String> enumItems, List<Object> values) throws IllegalArgumentException {
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

    protected String tryLongString(Double value) {
        String doubleString = String.valueOf(value);
        if (doubleString.endsWith(".0")) {
            return doubleString.substring(0, doubleString.length() - 2);
        } else {
            return doubleString;
        }
    }

    protected Long tryObjectToLong(Double value) {
        String stringValue = tryLongString(value);
        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    protected Long parseObjectToLong(boolean required, Object value) throws IllegalArgumentException {
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

    protected Double parseObjectToDouble(boolean required, Object value) throws IllegalArgumentException {
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

    protected String parseObjectToString(boolean required, Object value) throws IllegalArgumentException {
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

    protected Date parseObjectToTime(boolean required, Object value) throws IllegalArgumentException {
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

    protected Date parseObjectToDate(boolean required, Object value) throws IllegalArgumentException {
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

    protected Date parseObjectToDateTime(boolean required, Object value) throws IllegalArgumentException {
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

    protected Date parseStringDateFormat(DateFormat format, boolean required, String value) throws IllegalArgumentException {
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

    protected Long tryLong(String value) throws IllegalArgumentException {
        try {
            if (value.endsWith(".0")) {
                value = value.substring(0, 2);
            }
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    protected Double tryDouble(String value) throws IllegalArgumentException {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("is invalid");
        }
    }

    @RequestMapping(path = "/**")
    public ResponseEntity<JavaScriptExecuteResponse> execute(
            HttpServletRequest req,
            Identity identity
    ) throws IOException, ServletException {

        try {

            byte[] requestBody = null;

            String springRequestContentType = req.getContentType();
            if (springRequestContentType.contains(";")) {
                springRequestContentType = StringUtils.split(springRequestContentType, ';')[0];
            }

            if (ServletFileUpload.isMultipartContent(req)) {
                requestBody = ((ContentCachingRequestWrapper) ((AbstractMultipartHttpServletRequest) ((FirewalledRequest) req).getRequest()).getRequest()).getContentAsByteArray();
            } else {
                requestBody = ((ContentCachingRequestWrapper) ((FirewalledRequest) req).getRequest()).getContentAsByteArray();
            }
            if (requestBody == null || requestBody.length == 0) {
                requestBody = IOUtils.toByteArray(req.getInputStream());
            }
            // region stage, pathInfo
            boolean stage = ServletRequestUtils.getBooleanParameter(req, "stage", false);
            String pathInfo = req.getPathInfo().substring(11);
            if ("".equals(pathInfo)) {
                pathInfo = "/";
            } else {
                if (!"/".equals(pathInfo) && pathInfo.endsWith("/")) {
                    pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
                }
            }
            String hostname = req.getServerName();
            ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
            DSLContext context = applicationContext.getDSLContext();
            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
            HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
            HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);
            if (hostnameRecord == null) {
                return null;
            }
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);
            if (applicationRecord == null) {
                return null;
            }
            String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
            JdbcTemplate jdbcTemplate = applicationContext.getApplicationDataSource().getJdbcTemplate(applicationRecord.getCode(), jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
            if (jdbcTemplate == null) {
                return null;
            }
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
            // endregion

            // region restRecord
            Map<String, Object> restRecord = null;
            try {
                restRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.REST + " WHERE " + Jdbc.Rest.PATH + " = ? AND " + Jdbc.Rest.METHOD + " = ?", pathInfo, StringUtils.upperCase(req.getMethod()));
            } catch (EmptyResultDataAccessException e) {
            }
            if (restRecord == null) {
                return null;
            }
            // endregion

            Map<String, Object> newRequestHeader = new TreeMap<>();
            Map<String, Object> newRequestBody = new TreeMap<>();
            Map<String, Object> newRequestParameter = new TreeMap<>();

            // region http
            Http http = null;
            ScriptEngine scriptEngine = getScriptEngine(context);
            String stageScript = (String) restRecord.get(Jdbc.Rest.STAGE_SCRIPT);
            String script = (String) restRecord.get(Jdbc.Rest.SCRIPT);
            if (stage) {
                if ((stageScript == null || "".equals(stageScript))) {
                    return null;
                } else {
                    try {
                        scriptEngine.eval(stageScript);
                    } catch (ScriptException e) {
                        return null;
                    }
                    Invocable invocable = (Invocable) scriptEngine;
                    http = invocable.getInterface(Http.class);
                }
            } else {
                if (script == null || "".equals(script)) {
                    return null;
                } else {
                    try {
                        scriptEngine.eval(script);
                    } catch (ScriptException e) {
                        return null;
                    }
                    Invocable invocable = (Invocable) scriptEngine;
                    http = invocable.getInterface(Http.class);
                }
            }
            // endregion

            // region nobody
            List<String> headerIds = new ArrayList<>();
            List<String> enumIds = new ArrayList<>();
            List<String> queryIds = new ArrayList<>();
            List<String> jsonIds = new ArrayList<>();
            // endregion

            // region requestHeaderRecords
            List<Map<String, Object>> requestHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_HEADER + " WHERE " + Jdbc.RestRequestHeader.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
            for (Map<String, Object> header : requestHeaderRecords) {
                headerIds.add((String) header.get(Jdbc.RestRequestHeader.HTTP_HEADER_ID));
            }
            // endregion

            // region responseHeaderRecords
            List<Map<String, Object>> responseHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_RESPONSE_HEADER + " WHERE " + Jdbc.RestResponseHeader.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
            for (Map<String, Object> header : responseHeaderRecords) {
                headerIds.add((String) header.get(Jdbc.RestResponseHeader.HTTP_HEADER_ID));
            }
            // endregion

            // region nobody
            Map<String, Object> where = new HashMap<>();
            where.put(Jdbc.HttpHeader.HTTP_HEADER_ID, headerIds);
            List<Map<String, Object>> headerRecords = named.queryForList("SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.HTTP_HEADER_ID + " in (:" + Jdbc.HttpHeader.HTTP_HEADER_ID + ")", where);
            for (Map<String, Object> header : headerRecords) {
                if (header.get(Jdbc.HttpHeader.ENUM_ID) != null && !"".equals(header.get(Jdbc.HttpHeader.ENUM_ID))) {
                    if (!enumIds.contains((String) header.get(Jdbc.HttpHeader.ENUM_ID))) {
                        enumIds.add((String) header.get(Jdbc.HttpHeader.ENUM_ID));
                    }
                }
            }
            // endregion

            // region headerDictionary
            Map<String, Map<String, Object>> headerDictionary = new HashMap<>();
            for (Map<String, Object> headerRecord : headerRecords) {
                headerDictionary.put((String) headerRecord.get(Jdbc.HttpHeader.HTTP_HEADER_ID), headerRecord);
            }
            List<Map<String, Object>> requestQueryRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_QUERY + " WHERE " + Jdbc.RestRequestQuery.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
            for (Map<String, Object> query : requestQueryRecords) {
                if (!queryIds.contains((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID))) {
                    queryIds.add((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID));
                }
            }
            // endregion

            // region httpQueryDictionary
            where.clear();
            where.put(Jdbc.HttpQuery.HTTP_QUERY_ID, queryIds);
            List<Map<String, Object>> queryRecords = named.queryForList("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " in (:" + Jdbc.HttpQuery.HTTP_QUERY_ID + ")", where);
            Map<String, Map<String, Object>> httpQueryDictionary = new HashMap<>();
            for (Map<String, Object> queryRecord : queryRecords) {
                httpQueryDictionary.put((String) queryRecord.get(Jdbc.HttpQuery.HTTP_QUERY_ID), queryRecord);
            }

            for (Map<String, Object> query : queryRecords) {
                if (query.get(Jdbc.HttpQuery.ENUM_ID) != null && !"".equals(query.get(Jdbc.HttpQuery.ENUM_ID))) {
                    if (!enumIds.contains((String) query.get(Jdbc.HttpQuery.ENUM_ID))) {
                        enumIds.add((String) query.get(Jdbc.HttpQuery.ENUM_ID));
                    }
                }
            }
            // endregion

            // region nobody
            if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                    enumIds.add((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
                }
            }

            String method = (String) restRecord.get(Jdbc.Rest.METHOD);

            Map<String, Object> requestBodyRecord = null;
            if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
                if (restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                    if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                        enumIds.add((String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID));
                    }
                }
                if (restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID))) {
                    requestBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID));
                }
            }
            Map<String, Object> responseBodyRecord = null;
            if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID))) {
                responseBodyRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID));
            }
            if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                    enumIds.add((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
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
                where.clear();
                where.put(Jdbc.Enum.ENUM_ID, enumIds);
                enumRecords = named.queryForList("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " in (:" + Jdbc.Enum.ENUM_ID + ")", where);
            }

            // endregion

            // region enumDictionary
            Map<String, Map<String, Object>> enumDictionary = new HashMap<>();
            for (Map<String, Object> enumRecord : enumRecords) {
                enumDictionary.put((String) enumRecord.get(Jdbc.Enum.ENUM_ID), enumRecord);
            }
            // endregion

            // region nobody
            List<Map<String, Object>> enumItemRecords = new ArrayList<>();
            if (!enumIds.isEmpty()) {
                where.clear();
                where.put(Jdbc.EnumItem.ENUM_ID, enumIds);
                enumItemRecords = named.queryForList("SELECT * FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ID + " in (:" + Jdbc.EnumItem.ENUM_ID + ")", where);
            }
            // endregion

            // region enumItemDictionary
            Map<String, List<String>> enumItemDictionary = new HashMap<>();
            for (Map<String, Object> enumItemRecord : enumItemRecords) {
                String item = (String) enumItemRecord.get(Jdbc.EnumItem.VALUE);
                if (!enumItemDictionary.containsKey((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID))) {
                    List<String> items = new ArrayList<>();
                    items.add(item);
                    enumItemDictionary.put((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID), items);
                } else {
                    List<String> items = enumItemDictionary.get((String) enumItemRecord.get(Jdbc.EnumItem.ENUM_ID));
                    items.add(item);
                }
            }
            // endregion

            // region requestQueryDictionary
            Map<String, List<String>> requestQueryDictionary = new HashMap<>();
            String queryString = req.getQueryString();
            if (queryString != null && !"".equals(queryString)) {
                String[] params = StringUtils.split(queryString, '&');
                for (String param : params) {
                    String tmp[] = StringUtils.split(param, '=');
                    String name = tmp[0];
                    String value = tmp[1];
                    if (!requestQueryDictionary.containsKey(name)) {
                        List<String> values = new ArrayList<>();
                        values.add(value);
                        requestQueryDictionary.put(name, values);
                    } else {
                        List<String> values = requestQueryDictionary.get(name);
                        values.add(value);
                    }
                }
            }
            // endregion

            // region requestHeaderDictionary
            Map<String, List<String>> requestHeaderDictionary = new HashMap<>();
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                Enumeration<String> tempValues = req.getHeaders(headerName);
                List<String> headerValues = new ArrayList<>();
                while (tempValues.hasMoreElements()) {
                    headerValues.add(tempValues.nextElement());
                }
                requestHeaderDictionary.put(headerName, headerValues);
            }
            // endregion

            // region requestBodyJsonDictionary, requestBodyFormDictionary, requestBodyFormDataDictionary, requestBodyFormFileDictionary
            Map<String, List<String>> requestBodyFormDictionary = new HashMap<>();
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(springRequestContentType)) {
                String bodyString = "";
                if (requestBody != null && requestBody.length > 0) {
                    bodyString = URLDecoder.decode(IOUtils.toString(requestBody, "UTF-8"), "UTF-8");
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
                        if (!requestBodyFormDictionary.containsKey(name)) {
                            List<String> values = new ArrayList<>();
                            values.add(value);
                            requestBodyFormDictionary.put(name, values);
                        } else {
                            List<String> values = requestBodyFormDictionary.get(name);
                            values.add(value);
                        }
                    }
                }
            }

            Map<String, List<String>> requestBodyFormDataDictionary = new HashMap<>();
            Map<String, List<MultipartFile>> requestBodyFormFileDictionary = new HashMap<>();
            if (ServletFileUpload.isMultipartContent(req)) {
                MultipartHttpServletRequest request = (MultipartHttpServletRequest) ((FirewalledRequest) req).getRequest();
                if (request.getParameterMap() != null && !request.getParameterMap().isEmpty()) {
                    for (Map.Entry<String, String[]> item : request.getParameterMap().entrySet()) {
                        if (!requestQueryDictionary.containsKey(item.getKey())) {
                            requestBodyFormDataDictionary.put(item.getKey(), Arrays.asList(item.getValue()));
                        }
                    }
                }
                if (request.getFileMap() != null && !request.getFileMap().isEmpty()) {
                    for (Map.Entry<String, MultipartFile> item : request.getFileMap().entrySet()) {
                        if (!requestBodyFormFileDictionary.containsKey(item.getKey())) {
                            List<MultipartFile> values = new ArrayList<>();
                            values.add(item.getValue());
                            requestBodyFormFileDictionary.put(item.getKey(), values);
                        } else {
                            List<MultipartFile> values = requestBodyFormFileDictionary.get(item.getKey());
                            values.add(item.getValue());
                        }
                    }
                }
            }

            String json = "";
            if (MediaType.APPLICATION_JSON_VALUE.equals(springRequestContentType)) {
                if (requestBody != null && requestBody.length > 0) {
                    json = new String(requestBody, "UTF-8");
                }
            }
            // endregion

            // region Validation Request Query Parameter
            Map<String, String> requestQueryErrors = new HashMap<>();
            for (Map<String, Object> requestQueryRecord : requestQueryRecords) {
                String queryId = (String) requestQueryRecord.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID);
                Boolean required = (Boolean) requestQueryRecord.get(Jdbc.RestRequestQuery.REQUIRED);
                Map<String, Object> httpQuery = httpQueryDictionary.get(queryId);
                String name = (String) httpQuery.get(Jdbc.HttpQuery.NAME);
                String enumId = (String) httpQuery.get(Jdbc.HttpQuery.ENUM_ID);
                String type = (String) httpQuery.get(Jdbc.HttpQuery.TYPE);
                String subType = (String) httpQuery.get(Jdbc.HttpQuery.SUB_TYPE);

                List<String> strings = new ArrayList<>();
                String newType;
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        strings.add(requestQueryDictionary.get(name).get(0));
                    }
                    newType = type;
                } else {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        strings.addAll(requestQueryDictionary.get(name));
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
                    enumRecord = enumDictionary.get(enumId);
                    enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    enumItems = enumItemDictionary.get(enumId);
                }
                try {
                    for (String string : strings) {
                        Object value = null;
                        if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                            value = parseStringToBoolean(required, string);
                        } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                            value = parseStringToLong(required, string);
                        } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                            value = parseStringToDouble(required, string);
                        } else if (TypeEnum.String.getLiteral().equals(newType)) {
                            value = parseStringToString(required, string);
                        } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                            value = parseStringToTime(required, string);
                        } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                            value = parseStringToDate(required, string);
                        } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                            value = parseStringToDateTime(required, string);
                        } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            value = parseStringToEnum(required, enumType, enumItems, string);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    requestQueryErrors.put(name, e.getMessage());
                }
            }
            // endregion

            // region Validation Request Header
            Map<String, String> requestHeaderErrors = new HashMap<>();
            for (Map<String, Object> requestHeaderRecord : requestHeaderRecords) {
                String headerId = (String) requestHeaderRecord.get(Jdbc.RestRequestHeader.HTTP_HEADER_ID);
                Boolean required = (Boolean) requestHeaderRecord.get(Jdbc.RestRequestHeader.REQUIRED);
                Map<String, Object> httpHeader = headerDictionary.get(headerId);
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
                    enumRecord = enumDictionary.get(enumId);
                    enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    enumItems = enumItemDictionary.get(enumId);
                }
                try {
                    for (String string : strings) {
                        Object value = null;
                        if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                            value = parseStringToBoolean(required, string);
                        } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                            value = parseStringToLong(required, string);
                        } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                            value = parseStringToDouble(required, string);
                        } else if (TypeEnum.String.getLiteral().equals(newType)) {
                            value = parseStringToString(required, string);
                        } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                            value = parseStringToTime(required, string);
                        } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                            value = parseStringToDate(required, string);
                        } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                            value = parseStringDateFormat(HTTP_DATE_FORMAT, required, string);
                        } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            value = parseStringToEnum(required, enumType, enumItems, string);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    requestHeaderErrors.put(name, e.getMessage());
                }
            }
            // endregion

            // region Validation Request Body
            Map<String, Object> requestBodyErrors = new HashMap<>();
            if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
                String contentType = (String) restRecord.get(Jdbc.Rest.REQUEST_CONTENT_TYPE);
                if (req.getContentType() == null || "".equals(req.getContentType())) {
                    return null;
                }
                if (!contentType.equals(springRequestContentType)) {
                    return null;
                }
                if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    // region application/x-www-form-urlencoded
                    Boolean bodyRequired = (Boolean) restRecord.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if (requestBodyFormDictionary == null || requestBodyFormDictionary.isEmpty()) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBodyRecord.get(Jdbc.Json.JSON_ID));
                    for (Map<String, Object> jsonField : jsonFields) {
                        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
                        boolean required = (boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
                        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
                        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
                        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);

                        List<String> strings = new ArrayList<>();
                        String newType;
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.get(name).isEmpty()) {
                                strings.add(requestBodyFormDictionary.get(name).get(0));
                            }
                            newType = type;
                        } else {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.get(name).isEmpty()) {
                                strings.addAll(requestBodyFormDictionary.get(name));
                            }
                            newType = subType;
                        }
                        if (required) {
                            if (strings.isEmpty()) {
                                requestBodyErrors.put(name, "is required");
                            }
                        }
                        Map<String, Object> enumRecord = null;
                        String enumType = null;
                        List<String> enumItems = null;
                        if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            enumRecord = enumDictionary.get(enumId);
                            enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            enumItems = enumItemDictionary.get(enumId);
                        }

                        try {
                            for (String string : strings) {
                                Object value = null;
                                if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                                    value = parseStringToBoolean(required, string);
                                } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                                    value = parseStringToLong(required, string);
                                } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                                    value = parseStringToDouble(required, string);
                                } else if (TypeEnum.String.getLiteral().equals(newType)) {
                                    value = parseStringToString(required, string);
                                } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                                    value = parseStringToTime(required, string);
                                } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                                    value = parseStringToDate(required, string);
                                } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                                    value = parseStringToDateTime(required, string);
                                } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                                    value = parseStringToEnum(required, enumType, enumItems, string);
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            requestBodyErrors.put(name, e.getMessage());
                        }
                    }
                    // endregion
                } else if (contentType.equals(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    // region multipart/form-data
                    Boolean bodyRequired = (Boolean) restRecord.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if ((requestBodyFormDataDictionary == null || requestBodyFormDataDictionary.isEmpty()) && (requestBodyFormFileDictionary == null || requestBodyFormFileDictionary.isEmpty())) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBodyRecord.get(Jdbc.Json.JSON_ID));
                    for (Map<String, Object> jsonField : jsonFields) {
                        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
                        boolean required = (boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
                        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
                        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
                        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);

                        List<Object> objects = new ArrayList<>();
                        String newType;
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.get(name).isEmpty()) {
                                objects.add(requestBodyFormDataDictionary.get(name).get(0));
                            }
                            if (requestBodyFormFileDictionary.get(name) != null && !requestBodyFormFileDictionary.get(name).isEmpty()) {
                                objects.add(requestBodyFormFileDictionary.get(name).get(0));
                            }
                            newType = type;
                        } else {
                            if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.get(name).isEmpty()) {
                                objects.addAll(requestBodyFormDataDictionary.get(name));
                            }
                            if (requestBodyFormFileDictionary.get(name) != null && !requestBodyFormFileDictionary.get(name).isEmpty()) {
                                objects.addAll(requestBodyFormFileDictionary.get(name));
                            }
                            newType = subType;
                        }
                        Map<String, Object> enumRecord = null;
                        String enumType = null;
                        List<String> enumItems = null;
                        if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            enumRecord = enumDictionary.get(enumId);
                            enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            enumItems = enumItemDictionary.get(enumId);
                        }

                        if (required) {
                            if (objects.isEmpty()) {
                                requestBodyErrors.put(name, "is required");
                            }
                        }

                        try {
                            for (Object object : objects) {
                                Object value = null;
                                if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                                    value = parseStringToBoolean(required, (String) object);
                                } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                                    value = parseStringToLong(required, (String) object);
                                } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                                    value = parseStringToDouble(required, (String) object);
                                } else if (TypeEnum.String.getLiteral().equals(newType)) {
                                    value = parseStringToString(required, (String) object);
                                } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                                    value = parseStringToTime(required, (String) object);
                                } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                                    value = parseStringToDate(required, (String) object);
                                } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                                    value = parseStringToDateTime(required, (String) object);
                                } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                                    value = parseStringToEnum(required, enumType, enumItems, (String) object);
                                } else if (TypeEnum.File.getLiteral().equals(newType)) {
                                    value = parseMultipartFileToByteArray(required, (MultipartFile) object);
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            requestBodyErrors.put(name, e.getMessage());
                        }
                    }
                    // endregion
                } else if (contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
                    // region application/octet-stream
                    Boolean bodyRequired = (Boolean) restRecord.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if (requestBody == null || requestBody.length == 0) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    // endregion
                } else if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    // region application/json
                    String type = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_TYPE);
                    String subType = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_SUB_TYPE);
                    String enumId = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID);
                    Boolean bodyRequired = (Boolean) restRecord.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired && (json == null || "".equals(json))) {
                        requestBodyErrors.put("requestBody", "is required");
                    }
                    if (json != null && !"".equals(json)) {
                        if (TypeEnum.Boolean.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Boolean value = parseObjectToBoolean(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Long.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Long value = parseObjectToLong(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Double.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Double value = parseObjectToDouble(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.String.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    String value = parseObjectToString(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Time.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Date value = parseObjectToTime(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Date.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Date value = parseObjectToDate(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    Date value = parseObjectToDateTime(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                            // region nobody
                            Map<String, Object> enumRecord = enumDictionary.get(enumId);
                            String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            List<String> enumItems = enumItemDictionary.get(enumId);
                            try {
                                try {
                                    Object value = parseObjectToEnum(bodyRequired, enumType, enumItems, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Map.getLiteral().equals(type)) {
                            // region nobody
                            // TODO : SKH to be checked
                            String jsonId = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID);
                            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                            Map<String, Object> jsonObject = gson.fromJson(json, mapType);
                            if (jsonObject.isEmpty()) {
                                requestBodyErrors.put("requestBody", "is required");
                            } else {
                                for (Map<String, Object> jsonField : jsonFields) {
                                    validateJsonField(jdbcTemplate, requestBodyErrors, jsonObject, jsonField, enumItemDictionary, enumDictionary);
                                }
                            }
                            // endregion
                        } else if (TypeEnum.File.getLiteral().equals(type)) {
                            // region nobody
                            try {
                                try {
                                    byte[] value = parseObjectToByteArray(bodyRequired, gson.fromJson(json, Object.class));
                                } catch (IllegalArgumentException e) {
                                    requestBodyErrors.put("requestBody", e.getMessage());
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.List.getLiteral().equals(type)) {
                            // region nobody
                            if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Boolean[] value = parseObjectToBooleanArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Long[] value = parseObjectToLongArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Double[] value = parseObjectToDoubleArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.String.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    String[] value = parseObjectToStringArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Date[] value = parseObjectToDateArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Date[] value = parseObjectToTimeArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    Date[] value = parseObjectToDateTimeArray(bodyRequired, (List<Object>) gson.fromJson(json, Object.class));
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                                // region nobody
                                Map<String, Object> enumRecord = enumDictionary.get(enumId);
                                String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                                List<String> enumValues = enumItemDictionary.get(enumId);
                                Object value = parseObjectToEnumArray(bodyRequired, enumType, enumValues, (List<Object>) gson.fromJson(json, Object.class));
                                // endregion
                            } else if (TypeEnum.File.getLiteral().equals(subType)) {
                                // region nobody
                                try {
                                    List<Object> objects = gson.fromJson(json, this.listType);
                                    for (Object object : objects) {
                                        if (object == null) {
                                            requestBodyErrors.put("requestBody", "is required");
                                            break;
                                        } else {
                                            if (object instanceof List) {
                                                List<Object> value = (List<Object>) object;
                                                if (value.isEmpty()) {
                                                    requestBodyErrors.put("requestBody", "is required");
                                                    break;
                                                }
                                            } else {
                                                requestBodyErrors.put("requestBody", "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            } else if (TypeEnum.Map.getLiteral().equals(subType)) {
                                // region nobody
                                String jsonId = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID);
                                List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                                try {
                                    List<Object> objects = gson.fromJson(json, listType);
                                    for (Object object : objects) {
                                        if (object instanceof Map) {
                                            for (Map<String, Object> jsonField : jsonFields) {
                                                validateJsonField(jdbcTemplate, requestBodyErrors, (Map) object, jsonField, enumItemDictionary, enumDictionary);
                                            }
                                        } else {
                                            requestBodyErrors.put("requestBody", "is invalid");
                                            break;
                                        }
                                    }
                                } catch (JsonSyntaxException | ClassCastException e) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                                // endregion
                            }
                            // endregion
                        }
                    }
                    // endregion
                }
            }
            // endregion

            if (requestQueryErrors.isEmpty() || requestBodyErrors.isEmpty() || requestHeaderErrors.isEmpty()) {
                return null;
            }

            Map<String, Object> responseBodyErrors = new HashMap<>();
            String responseContentType = (String) restRecord.get(Jdbc.Rest.RESPONSE_CONTENT_TYPE);
            String responseBodyType = (String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_TYPE);
            String responseBodySubType = (String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_SUB_TYPE);
            boolean responseBodyRequired = (boolean) restRecord.get(Jdbc.Rest.RESPONSE_BODY_REQUIRED);
            Object response = http.http(null, null, null, null);
            if (MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(responseContentType)) {
                if (responseBodyRequired) {
                    if (response == null) {
                        responseBodyErrors.put("responseBody", "is required");
                    } else {
                        if (response instanceof List) {
                            if (((List) response).isEmpty()) {
                                responseBodyErrors.put("responseBody", "is required");
                            }
                        } else {
                            responseBodyErrors.put("responseBody", "is invalid");
                        }
                    }
                } else {
                    if (response == null) {

                    } else {
                        if (response instanceof List) {

                        } else {

                        }
                    }
                }
            } else if (MediaType.APPLICATION_JSON_VALUE.equals(responseContentType)) {
                if (responseBodyRequired) {
                    if (TypeEnum.Boolean.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Long.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Double.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.String.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Time.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Date.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.DateTime.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Map.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.Enum.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        // endregion
                    } else if (TypeEnum.List.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        if (TypeEnum.Boolean.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Long.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Double.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.String.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Time.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Date.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.DateTime.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Map.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.Enum.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        } else if (TypeEnum.List.getLiteral().equals(responseBodyType)) {
                            // region nobody
                            // endregion
                        }
                        // endregion
                    }
                } else {

                }
            }

            if (responseBodyErrors.isEmpty()) {
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(requestBodyErrors.size());
            System.out.println(gson.toJson(requestBodyErrors));

        } catch (
                Throwable e)

        {
            e.printStackTrace();
        }
        return null;
    }

    private void validateJsonField(JdbcTemplate jdbcTemplate, Map<String, Object> error, Map<String, Object> json, Map<String, Object> jsonField, Map<String, List<String>> enumItemDictionary, Map<String, Map<String, Object>> enumDictionary) {
        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);
        String jsonId = (String) jsonField.get(Jdbc.JsonField.MAP_JSON_ID);
        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
        Boolean required = (Boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
        if (required) {
            // region required
            if (TypeEnum.Boolean.getLiteral().equals(type)) {
                try {
                    Boolean value = (Boolean) json.get(name);
                    if (value == null) {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Long.getLiteral().equals(type)) {
                try {
                    Double doubleValue = (Double) json.get(name);
                    if (doubleValue == null) {
                        error.put(name, "is required");
                    } else {
                        String stringValue = String.valueOf(doubleValue);
                        if (stringValue.endsWith(".0")) {
                            stringValue = stringValue.substring(0, stringValue.length() - 2);
                            try {
                                Long value = Long.valueOf(stringValue);
                            } catch (NumberFormatException e) {
                                error.put(name, "is invalid");
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Double.getLiteral().equals(type)) {
                try {
                    Double value = (Double) json.get(name);
                    if (value == null) {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.String.getLiteral().equals(type)) {
                try {
                    String value = (String) json.get(name);
                    if (value == null || "".equals(value)) {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Time.getLiteral().equals(type)) {
                try {
                    String stringValue = (String) json.get(name);
                    if (stringValue == null || "".equals(stringValue)) {
                        error.put(name, "is required");
                    } else {
                        try {
                            Date value = DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(stringValue);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Date.getLiteral().equals(type)) {
                try {
                    String stringValue = (String) json.get(name);
                    if (stringValue == null || "".equals(stringValue)) {
                        error.put(name, "is required");
                    } else {
                        try {
                            Date value = DateFormatUtils.ISO_DATE_FORMAT.parse(stringValue);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                try {
                    String stringValue = (String) json.get(name);
                    if (stringValue == null || "".equals(stringValue)) {
                        error.put(name, "is required");
                    } else {
                        try {
                            Date value = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(stringValue);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                Object value = json.get(name);
                Map<String, Object> enumRecord = enumDictionary.get(enumId);
                String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                if (TypeEnum.Boolean.getLiteral().equals(enumType)) {
                    try {
                        Boolean enumValue = (Boolean) value;
                        if (enumValue == null) {
                            error.put(name, "is required");
                        } else {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Character.getLiteral().equals(enumType)
                        || TypeEnum.String.getLiteral().equals(enumType)
                        || TypeEnum.Time.getLiteral().equals(enumType)
                        || TypeEnum.Date.getLiteral().equals(enumType)
                        || TypeEnum.DateTime.getLiteral().equals(enumType)) {
                    try {
                        String enumValue = (String) value;
                        if (enumValue == null || "".equals(enumValue)) {
                            error.put(name, "is required");
                        } else {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(enumValue)) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Long.getLiteral().equals(enumType)) {
                    try {
                        Double doubleValue = (Double) value;
                        if (value == null) {
                            error.put(name, "is required");
                        } else {
                            try {
                                String stringValue = String.valueOf(doubleValue);
                                if (stringValue.endsWith(".0")) {
                                    stringValue = stringValue.substring(0, stringValue.length() - 2);
                                }
                                Long enumValue = Long.valueOf(stringValue);
                                List<String> enumItemValues = enumItemDictionary.get(enumId);
                                if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                    error.put(name, "is invalid");
                                }
                            } catch (NumberFormatException e) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Double.getLiteral().equals(enumType)) {
                    try {
                        Double enumValue = (Double) value;
                        if (enumValue == null) {
                            error.put(name, "is required");
                        } else {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                }
            } else if (TypeEnum.Map.getLiteral().equals(type)) {
                try {
                    Map<String, Object> fieldJson = (Map<String, Object>) json.get(name);
                    if (fieldJson != null) {
                        Map<String, Object> fieldError = new HashMap<>();
                        List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                        for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                            validateJsonField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
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
                try {
                    List<Object> value = (List<Object>) json.get(name);
                    if (value == null || value.isEmpty()) {
                        error.put(name, "is required");
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.List.getLiteral().equals(type)) {
                if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                    List<Boolean> values = (List<Boolean>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (Boolean value : values) {
                                if (value == null) {
                                    error.put(name, "is required");
                                    break;
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                    List<Double> values = (List<Double>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (Double value : values) {
                                if (value == null) {
                                    error.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        String longValue = String.valueOf(value);
                                        if (longValue.endsWith(".0")) {
                                            longValue = longValue.substring(0, longValue.length() - 2);
                                        }
                                        Long.valueOf(longValue);
                                    } catch (NumberFormatException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                    List<Double> values = (List<Double>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (Double value : values) {
                                if (value == null) {
                                    error.put(name, "is required");
                                    break;
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.String.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value == null || "".equals(value)) {
                                    error.put(name, "is required");
                                    break;
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value == null || "".equals(value)) {
                                    error.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value == null || "".equals(value)) {
                                    error.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value == null || "".equals(value)) {
                                    error.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                    List<Object> values = (List<Object>) json.get(name);
                    Map<String, Object> enumRecord = enumDictionary.get(enumId);
                    String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    if (values != null && !values.isEmpty()) {
                        for (Object value : values) {
                            if (TypeEnum.Boolean.getLiteral().equals(enumType)) {
                                try {
                                    Boolean enumValue = (Boolean) value;
                                    if (enumValue != null) {
                                        List<String> enumItemValues = enumItemDictionary.get(enumId);
                                        if (!enumItemValues.contains(String.valueOf(value))) {
                                            error.put(name, "is invalid");
                                            break;
                                        }
                                    } else {
                                        error.put(name, "is required");
                                        break;
                                    }
                                } catch (ClassCastException e) {
                                    error.put(name, "is invalid");
                                    break;
                                }
                            } else if (TypeEnum.Character.getLiteral().equals(enumType)
                                    || TypeEnum.String.getLiteral().equals(enumType)
                                    || TypeEnum.Time.getLiteral().equals(enumType)
                                    || TypeEnum.Date.getLiteral().equals(enumType)
                                    || TypeEnum.DateTime.getLiteral().equals(enumType)) {
                                try {
                                    String enumValue = (String) value;
                                    if (enumValue != null && !"".equals(enumValue)) {
                                        List<String> enumItemValues = enumItemDictionary.get(enumId);
                                        if (!enumItemValues.contains(enumValue)) {
                                            error.put(name, "is invalid");
                                            break;
                                        }
                                    } else {
                                        error.put(name, "is required");
                                        break;
                                    }
                                } catch (ClassCastException e) {
                                    error.put(name, "is invalid");
                                    break;
                                }
                            } else if (TypeEnum.Long.getLiteral().equals(enumType)) {
                                try {
                                    Double doubleValue = (Double) value;
                                    if (value != null) {
                                        try {
                                            String stringValue = String.valueOf(doubleValue);
                                            if (stringValue.endsWith(".0")) {
                                                stringValue = stringValue.substring(0, stringValue.length() - 2);
                                            }
                                            Long enumValue = Long.valueOf(stringValue);
                                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                                            if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                                error.put(name, "is invalid");
                                                break;
                                            }
                                        } catch (NumberFormatException e) {
                                            error.put(name, "is invalid");
                                            break;
                                        }
                                    } else {
                                        error.put(name, "is required");
                                        break;
                                    }
                                } catch (ClassCastException e) {
                                    error.put(name, "is invalid");
                                    break;
                                }
                            } else if (TypeEnum.Double.getLiteral().equals(enumType)) {
                                try {
                                    Double enumValue = (Double) value;
                                    if (enumValue != null) {
                                        List<String> enumItemValues = enumItemDictionary.get(enumId);
                                        if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                            error.put(name, "is invalid");
                                            break;
                                        }
                                    } else {
                                        error.put(name, "is required");
                                        break;
                                    }
                                } catch (ClassCastException e) {
                                    error.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    } else {
                        error.put(name, "is required");
                    }
                } else if (TypeEnum.File.getLiteral().equals(subType)) {
                    List<Object> values = (List<Object>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        for (Object value : values) {
                            if (value == null) {
                                error.put(name, "is required");
                                break;
                            } else {
                                if (value instanceof List) {
                                    if (((List) value).isEmpty()) {
                                        error.put(name, "is required");
                                        break;
                                    }
                                } else {
                                    error.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    } else {
                        error.put(name, "is required");
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
                                        validateJsonField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
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
            // endregion
        } else {
            // region not required
            if (TypeEnum.Time.getLiteral().equals(type)) {
                try {
                    String value = (String) json.get(name);
                    if (value != null) {
                        try {
                            DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Date.getLiteral().equals(type)) {
                try {
                    String value = (String) json.get(name);
                    if (value != null) {
                        try {
                            DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                try {
                    String value = (String) json.get(name);
                    if (value != null) {
                        try {
                            DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                        } catch (ParseException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                Object value = json.get(name);
                Map<String, Object> enumRecord = enumDictionary.get(enumId);
                String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                if (TypeEnum.Boolean.getLiteral().equals(enumType)) {
                    try {
                        Boolean enumValue = (Boolean) value;
                        if (enumValue != null) {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(String.valueOf(value))) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Character.getLiteral().equals(enumType)
                        || TypeEnum.String.getLiteral().equals(enumType)
                        || TypeEnum.Time.getLiteral().equals(enumType)
                        || TypeEnum.Date.getLiteral().equals(enumType)
                        || TypeEnum.DateTime.getLiteral().equals(enumType)) {
                    try {
                        String enumValue = (String) value;
                        if (enumValue != null && !"".equals(enumValue)) {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(enumValue)) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Long.getLiteral().equals(enumType)) {
                    try {
                        Double doubleValue = (Double) value;
                        if (value != null) {
                            try {
                                String stringValue = String.valueOf(doubleValue);
                                if (stringValue.endsWith(".0")) {
                                    stringValue = stringValue.substring(0, stringValue.length() - 2);
                                }
                                Long enumValue = Long.valueOf(stringValue);
                                List<String> enumItemValues = enumItemDictionary.get(enumId);
                                if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                    error.put(name, "is invalid");
                                }
                            } catch (NumberFormatException e) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                } else if (TypeEnum.Double.getLiteral().equals(enumType)) {
                    try {
                        Double enumValue = (Double) value;
                        if (enumValue != null) {
                            List<String> enumItemValues = enumItemDictionary.get(enumId);
                            if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                error.put(name, "is invalid");
                            }
                        }
                    } catch (ClassCastException e) {
                        error.put(name, "is invalid");
                    }
                }
            } else if (TypeEnum.Map.getLiteral().equals(type)) {
                try {
                    Map<String, Object> fieldJson = (Map<String, Object>) json.get(name);
                    if (fieldJson != null) {
                        Map<String, Object> fieldError = new HashMap<>();
                        List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonField.get(Jdbc.JsonField.MAP_JSON_ID));
                        for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                            validateJsonField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                        }
                        if (!fieldError.isEmpty()) {
                            error.put(name, fieldError);
                        }
                    }
                } catch (ClassCastException e) {
                    error.put(name, "is invalid");
                }
            } else if (TypeEnum.List.getLiteral().equals(type)) {
                if (TypeEnum.Time.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value != null) {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value != null) {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                    List<String> values = (List<String>) json.get(name);
                    if (values != null && !values.isEmpty()) {
                        try {
                            for (String value : values) {
                                if (value != null) {
                                    try {
                                        DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } catch (ClassCastException e) {
                            error.put(name, "is invalid");
                        }
                    }
                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                    List<Object> values = (List<Object>) json.get(name);
                    Map<String, Object> enumRecord = enumDictionary.get(enumId);
                    String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                    if (values != null && !values.isEmpty()) {
                        for (Object value : values) {
                            if (TypeEnum.Boolean.getLiteral().equals(enumType)) {
                                Boolean enumValue = (Boolean) value;
                                if (enumValue != null) {
                                    List<String> enumItemValues = enumItemDictionary.get(enumId);
                                    if (!enumItemValues.contains(String.valueOf(value))) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            } else if (TypeEnum.Character.getLiteral().equals(enumType)
                                    || TypeEnum.String.getLiteral().equals(enumType)
                                    || TypeEnum.Time.getLiteral().equals(enumType)
                                    || TypeEnum.Date.getLiteral().equals(enumType)
                                    || TypeEnum.DateTime.getLiteral().equals(enumType)) {
                                String enumValue = (String) value;
                                if (enumValue != null && !"".equals(enumValue)) {
                                    List<String> enumItemValues = enumItemDictionary.get(enumId);
                                    if (!enumItemValues.contains(enumValue)) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            } else if (TypeEnum.Long.getLiteral().equals(enumType)) {
                                Double doubleValue = (Double) value;
                                if (value != null) {
                                    try {
                                        String stringValue = String.valueOf(doubleValue);
                                        if (stringValue.endsWith(".0")) {
                                            stringValue = stringValue.substring(0, stringValue.length() - 2);
                                        }
                                        Long enumValue = Long.valueOf(stringValue);
                                        List<String> enumItemValues = enumItemDictionary.get(enumId);
                                        if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                            error.put(name, "is invalid");
                                            break;
                                        }
                                    } catch (NumberFormatException e) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            } else if (TypeEnum.Double.getLiteral().equals(enumType)) {
                                Double enumValue = (Double) value;
                                if (enumValue != null) {
                                    List<String> enumItemValues = enumItemDictionary.get(enumId);
                                    if (!enumItemValues.contains(String.valueOf(enumValue))) {
                                        error.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (TypeEnum.Map.getLiteral().equals(subType)) {
                    List<Map<String, Object>> fieldJsons = (List<Map<String, Object>>) json.get(name);
                    if (fieldJsons != null) {
                        for (Map<String, Object> fieldJson : fieldJsons) {
                            if (fieldJson != null) {
                                Map<String, Object> fieldError = new HashMap<>();
                                List<Map<String, Object>> fieldJsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", fieldJson.get(Jdbc.JsonField.MAP_JSON_ID));
                                for (Map<String, Object> fieldJsonField : fieldJsonFields) {
                                    validateJsonField(jdbcTemplate, fieldError, fieldJson, fieldJsonField, enumItemDictionary, enumDictionary);
                                }
                                if (!fieldError.isEmpty()) {
                                    error.put(name, fieldError);
                                }
                            }
                        }
                    }
                }
            }
            // endregion
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
    private void processJsonField(JdbcTemplate jdbcTemplate, List<String> jsonIdList, List<String> enumIdList, Map<String, Object> jsonField) {
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

    private ScriptEngine getScriptEngine(DSLContext context) {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new JavaFilter(context));
        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        JavascripUtils.eval(engine);
        return engine;
    }

    private Object parseBody(Object body) {
        if (body instanceof JSObject) {
            JSObject js = (JSObject) body;
            if (js.isStrictFunction() || js.isFunction()) {
                return null;
            } else if (js.isArray()) {
                return js.values();
            } else {
                Map<String, Object> result = new LinkedHashMap<>();
                for (String key : js.keySet()) {
                    result.put(key, js.getMember(key));
                }
                return result;
            }
        } else {
            return body;
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnThrowable(Throwable throwable) {
        LOGGER.info(throwable.getMessage());
        if (throwable instanceof BadCredentialsException) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.UNAUTHORIZED.value());
            response.setResult(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return ResponseEntity.ok(response);
        } else {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResult(throwable.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnResponse(boolean found, boolean error, Throwable throwable, Object responseBody) {
        if (!found) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        } else {
            if (error) {
                return returnThrowable(throwable);
            } else {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.setData(parseBody(responseBody));
                return ResponseEntity.ok(response);
            }
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnMethodNotAllowed() {
        JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
        response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setResult(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        return ResponseEntity.ok(response);
    }

    public interface Http {
        Object http(HttpServletRequest request, Map<String, Object> requestParameter, Map<String, Object> requestHeader, Object requestBody);
    }
}
