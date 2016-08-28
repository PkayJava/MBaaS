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
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.script.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by socheat on 2/27/16.
 */
@Controller
@RequestMapping(path = "/javascript")
public class JavascriptController {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(com.angkorteam.mbaas.server.MBaaS.class);

    @RequestMapping(path = "/**")
    public ResponseEntity<JavaScriptExecuteResponse> execute(
            HttpServletRequest req,
            Identity identity
    ) throws IOException, ServletException {
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
        Map<String, Object> restRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.REST + " WHERE " + Jdbc.Rest.PATH + " = ? AND " + Jdbc.Rest.METHOD + " = ?", pathInfo, StringUtils.upperCase(req.getMethod()));
        if (restRecord == null) {
            return null;
        }

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
        String method = (String) restRecord.get(Jdbc.Rest.METHOD);
        if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
            String contentType = (String) restRecord.get(Jdbc.Rest.REQUEST_CONTENT_TYPE);
            if (!contentType.equalsIgnoreCase(req.getContentType())) {
                return null;
            }
        }

        List<String> headerIds = new ArrayList<>();
        List<String> enumIds = new ArrayList<>();
        List<String> queryIds = new ArrayList<>();
        List<String> jsonIds = new ArrayList<>();
        List<Map<String, Object>> requestHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_HEADER + " WHERE " + Jdbc.RestRequestHeader.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
        for (Map<String, Object> header : requestHeaderRecords) {
            headerIds.add((String) header.get(Jdbc.RestRequestHeader.HTTP_HEADER_ID));
        }
        List<Map<String, Object>> responseHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_RESPONSE_HEADER + " WHERE " + Jdbc.RestResponseHeader.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
        for (Map<String, Object> header : responseHeaderRecords) {
            headerIds.add((String) header.get(Jdbc.RestResponseHeader.HTTP_HEADER_ID));
        }
        List<Map<String, Object>> headerRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.HTTP_HEADER_ID + " in (?)", headerIds.toArray());
        Map<String, Map<String, Object>> headerDictionary = new HashMap<>();
        for (Map<String, Object> headerRecord : headerRecords) {
            headerDictionary.put((String) headerRecord.get(Jdbc.HttpHeader.HTTP_HEADER_ID), headerRecord);
        }
        for (Map<String, Object> header : headerRecords) {
            if (header.get(Jdbc.HttpHeader.ENUM_ID) != null && !"".equals(header.get(Jdbc.HttpHeader.ENUM_ID))) {
                if (!enumIds.contains((String) header.get(Jdbc.HttpHeader.ENUM_ID))) {
                    enumIds.add((String) header.get(Jdbc.HttpHeader.ENUM_ID));
                }
            }
        }
        List<Map<String, Object>> requestQueryRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_QUERY + " WHERE " + Jdbc.RestRequestQuery.REST_ID + " = ?", restRecord.get(Jdbc.Rest.REST_ID));
        for (Map<String, Object> query : requestQueryRecords) {
            if (!queryIds.contains((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID))) {
                queryIds.add((String) query.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID));
            }
        }
        List<Map<String, Object>> queryRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " in (?)", queryIds.toArray());
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

        if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
            if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                enumIds.add((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
            }
        }

        Map<String, Object> requestBody = null;
        if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
            if (restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID))) {
                    enumIds.add((String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID));
                }
            }
            if (restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID))) {
                requestBody = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID));
            }
        }
        Map<String, Object> responseBody = null;
        if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID))) {
            responseBody = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", restRecord.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID));
        }
        if (restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID) != null && !"".equals(restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
            if (!enumIds.contains((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID))) {
                enumIds.add((String) restRecord.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID));
            }
        }

        if (requestBody != null) {
            if (!jsonIds.contains((String) requestBody.get(Jdbc.Json.JSON_ID))) {
                jsonIds.add((String) requestBody.get(Jdbc.Json.JSON_ID));
            }
            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBody.get(Jdbc.Json.JSON_ID));
            if (jsonFields != null && !jsonFields.isEmpty()) {
                for (Map<String, Object> jsonField : jsonFields) {
                    processJsonField(jdbcTemplate, jsonIds, enumIds, jsonField);
                }
            }
        }

        if (responseBody != null) {
            if (!jsonIds.contains((String) responseBody.get(Jdbc.Json.JSON_ID))) {
                jsonIds.add((String) responseBody.get(Jdbc.Json.JSON_ID));
            }
            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", responseBody.get(Jdbc.Json.JSON_ID));
            if (jsonFields != null && !jsonFields.isEmpty()) {
                for (Map<String, Object> jsonField : jsonFields) {
                    processJsonField(jdbcTemplate, jsonIds, enumIds, jsonField);
                }
            }
        }

        List<Map<String, Object>> enumRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " in (?)", enumIds.toArray());
        Map<String, Map<String, Object>> enumDictionary = new HashMap<>();
        for (Map<String, Object> enumRecord : enumRecords) {
            enumDictionary.put((String) enumRecord.get(Jdbc.Enum.ENUM_ID), enumRecord);
        }
        List<Map<String, Object>> enumItemRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ID + " in (?)", enumIds.toArray());
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

        // Request Query Parameter Validation
        Map<String, String> requestQueryErrors = new HashMap<>();
        for (Map<String, Object> requestQueryRecord : requestQueryRecords) {
            String queryId = (String) requestQueryRecord.get(Jdbc.RestRequestQuery.HTTP_QUERY_ID);
            Boolean required = (Boolean) requestQueryRecord.get(Jdbc.RestRequestQuery.REQUIRED);
            Map<String, Object> httpQuery = httpQueryDictionary.get(queryId);
            String name = (String) httpQuery.get(Jdbc.HttpQuery.NAME);
            String enumId = (String) httpQuery.get(Jdbc.HttpQuery.ENUM_ID);
            String type = (String) httpQuery.get(Jdbc.HttpQuery.TYPE);
            String subType = (String) httpQuery.get(Jdbc.HttpQuery.SUB_TYPE);
            if (required) {
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                if (!"true".equals(value) || !"false".equals(value)) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                try {
                                    Long.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                try {
                                    Double.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                try {
                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestQueryErrors.put(name, "is required");
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            List<String> enumItems = enumItemDictionary.get(enumId);
                            if (!enumItems.contains(value)) {
                                requestQueryErrors.put(name, "is invalid");
                            }
                        }
                    } else {
                        requestQueryErrors.put(name, "is required");
                    }
                } else {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    if (!"true".equals(value) || !"false".equals(value)) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        Long.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        Double.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                }
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestQueryErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                List<String> enumItems = enumItemDictionary.get(enumId);
                                if (!enumItems.contains(value)) {
                                    requestQueryErrors.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    } else {
                        requestQueryErrors.put(name, "is required");
                    }
                }
            } else {
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                if (!"true".equals(value) || !"false".equals(value)) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    Long.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    Double.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestQueryErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                            String value = requestQueryDictionary.get(name).get(0);
                            List<String> enumItems = enumItemDictionary.get(enumId);
                            if (!enumItems.contains(value)) {
                                requestQueryErrors.put(name, "is invalid");
                            }
                        }
                    }
                } else {
                    if (requestQueryDictionary.get(name) != null && !requestQueryDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    if (!"true".equals(value) || !"false".equals(value)) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        Long.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        Double.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                }
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestQueryErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                            for (String value : requestQueryDictionary.get(name)) {
                                List<String> enumItems = enumItemDictionary.get(enumId);
                                if (!enumItems.contains(value)) {
                                    requestQueryErrors.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

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
        Object http(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }
}
