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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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

import javax.script.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @RequestMapping(path = "/**")
    public ResponseEntity<JavaScriptExecuteResponse> execute(
            HttpServletRequest req,
            Identity identity
    ) throws IOException, ServletException {
        byte[] requestBody = null;

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
            enumRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " in (:" + Jdbc.Enum.ENUM_ID + ")", where);
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
            enumItemRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ID + " in (:" + Jdbc.EnumItem.ENUM_ID + ")", where);
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
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(req.getContentType())) {
            String bodyString = "";
            if (requestBody != null && requestBody.length > 0) {
                bodyString = IOUtils.toString(requestBody, "UTF-8");
            }
            if (bodyString != null && !"".equals(bodyString)) {
                String[] params = StringUtils.split(bodyString, '&');
                for (String param : params) {
                    String tmp[] = StringUtils.split(param, '=');
                    String name = tmp[0];
                    String value = tmp[1];
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
        if (MediaType.APPLICATION_JSON_VALUE.equals(req.getContentType()) && !"".equals(json)) {
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
            if (required) {
                // region required
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
                // endregion
            } else {
                // region not required
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
                // endregion
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
            if (required) {
                // region required
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                if (!"true".equals(value) || !"false".equals(value)) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                try {
                                    Long.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                try {
                                    Double.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                try {
                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                                requestHeaderErrors.put(name, "is required");
                            } else {
                                try {
                                    HTTP_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            List<String> enumItems = enumItemDictionary.get(enumId);
                            if (!enumItems.contains(value)) {
                                requestHeaderErrors.put(name, "is invalid");
                            }
                        }
                    } else {
                        requestHeaderErrors.put(name, "is required");
                    }
                } else {
                    if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    if (!"true".equals(value) || !"false".equals(value)) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        Long.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        Double.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                }
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                    requestHeaderErrors.put(name, "is required");
                                    break;
                                } else {
                                    try {
                                        HTTP_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                List<String> enumItems = enumItemDictionary.get(enumId);
                                if (!enumItems.contains(value)) {
                                    requestHeaderErrors.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    } else {
                        requestHeaderErrors.put(name, "is required");
                    }
                }
                // endregion
            } else {
                // region not required
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                if (!"true".equals(value) || !"false".equals(value)) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    Long.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    Double.valueOf(value);
                                } catch (NumberFormatException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            if (value == null || "".equals(value)) {
                            } else {
                                try {
                                    HTTP_DATE_FORMAT.parse(value);
                                } catch (ParseException e) {
                                    requestHeaderErrors.put(name, "is invalid");
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                            String value = requestHeaderDictionary.get(name).get(0);
                            List<String> enumItems = enumItemDictionary.get(enumId);
                            if (!enumItems.contains(value)) {
                                requestHeaderErrors.put(name, "is invalid");
                            }
                        }
                    }
                } else {
                    if (requestHeaderDictionary.get(name) != null && !requestHeaderDictionary.get(name).isEmpty()) {
                        if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    if (!"true".equals(value) || !"false".equals(value)) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        Long.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        Double.valueOf(value);
                                    } catch (NumberFormatException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.String.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                }
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                if (value == null || "".equals(value)) {
                                } else {
                                    try {
                                        HTTP_DATE_FORMAT.parse(value);
                                    } catch (ParseException e) {
                                        requestHeaderErrors.put(name, "is invalid");
                                        break;
                                    }
                                }
                            }
                        } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                            for (String value : requestHeaderDictionary.get(name)) {
                                List<String> enumItems = enumItemDictionary.get(enumId);
                                if (!enumItems.contains(value)) {
                                    requestHeaderErrors.put(name, "is invalid");
                                    break;
                                }
                            }
                        }
                    }
                }
                // endregion
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
            if (!contentType.equals(req.getContentType())) {
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
                    if (required) {
                        // region required
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.isEmpty()) {
                                if (TypeEnum.Boolean.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        if (!"true".equals(value) || !"false".equals(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Long.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        try {
                                            Long.valueOf(value);
                                        } catch (NumberFormatException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Double.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        try {
                                            Double.valueOf(value);
                                        } catch (NumberFormatException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.String.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    }
                                } else if (TypeEnum.Time.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Date.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                        requestBodyErrors.put(name, "is required");
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    List<String> enumItems = enumItemDictionary.get(enumId);
                                    if (!enumItems.contains(value)) {
                                        requestBodyErrors.put(name, "is invalid");
                                    }
                                }
                            } else {
                                requestBodyErrors.put(name, "is required");
                            }
                        } else {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.get(name).isEmpty()) {
                                if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            if (!"true".equals(value) || !"false".equals(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            try {
                                                Long.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            try {
                                                Double.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.String.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        }
                                    }
                                } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        List<String> enumItems = enumItemDictionary.get(enumId);
                                        if (!enumItems.contains(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                            break;
                                        }
                                    }
                                }
                            } else {
                                requestBodyErrors.put(name, "is required");
                            }
                        }
                        // endregion
                    } else {
                        // region not required
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.get(name).isEmpty()) {
                                if (TypeEnum.Boolean.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        if (!"true".equals(value) || !"false".equals(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Long.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        try {
                                            Long.valueOf(value);
                                        } catch (NumberFormatException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Double.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        try {
                                            Double.valueOf(value);
                                        } catch (NumberFormatException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.String.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    }
                                } else if (TypeEnum.Time.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Date.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    if (value == null || "".equals(value)) {
                                    } else {
                                        try {
                                            DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                        } catch (ParseException e) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                                    String value = requestBodyFormDictionary.get(name).get(0);
                                    List<String> enumItems = enumItemDictionary.get(enumId);
                                    if (!enumItems.contains(value)) {
                                        requestBodyErrors.put(name, "is invalid");
                                    }
                                }
                            }
                        } else {
                            if (requestBodyFormDictionary.get(name) != null && !requestBodyFormDictionary.get(name).isEmpty()) {
                                if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            if (!"true".equals(value) || !"false".equals(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                Long.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                Double.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.String.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        }
                                    }
                                } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                                    for (String value : requestBodyFormDictionary.get(name)) {
                                        List<String> enumItems = enumItemDictionary.get(enumId);
                                        if (!enumItems.contains(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // endregion
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
                    if (required) {
                        // region required
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (TypeEnum.Boolean.getLiteral().equals(type)
                                    || TypeEnum.Long.getLiteral().equals(type)
                                    || TypeEnum.Double.getLiteral().equals(type)
                                    || TypeEnum.String.getLiteral().equals(type)
                                    || TypeEnum.Time.getLiteral().equals(type)
                                    || TypeEnum.Date.getLiteral().equals(type)
                                    || TypeEnum.DateTime.getLiteral().equals(type)
                                    || TypeEnum.Enum.getLiteral().equals(type)) {
                                if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.isEmpty()) {
                                    if (TypeEnum.Boolean.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            if (!"true".equals(value) || !"false".equals(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Long.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            try {
                                                Long.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Double.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            try {
                                                Double.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.String.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        }
                                    } else if (TypeEnum.Time.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Date.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                            requestBodyErrors.put(name, "is required");
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        List<String> enumItems = enumItemDictionary.get(enumId);
                                        if (!enumItems.contains(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                } else {
                                    requestBodyErrors.put(name, "is required");
                                }
                            } else if (TypeEnum.File.getLiteral().equals(type)) {
                                if (requestBodyFormFileDictionary.get(name) != null && !requestBodyFormFileDictionary.isEmpty()) {
                                    MultipartFile value = requestBodyFormFileDictionary.get(name).get(0);
                                    if (value == null || value.isEmpty()) {
                                        requestBodyErrors.put(name, "is required");
                                    }
                                } else {
                                    requestBodyErrors.put(name, "is required");
                                }
                            }
                        } else {
                            if (TypeEnum.Boolean.getLiteral().equals(subType)
                                    || TypeEnum.Long.getLiteral().equals(subType)
                                    || TypeEnum.Double.getLiteral().equals(subType)
                                    || TypeEnum.String.getLiteral().equals(subType)
                                    || TypeEnum.Time.getLiteral().equals(subType)
                                    || TypeEnum.Date.getLiteral().equals(subType)
                                    || TypeEnum.DateTime.getLiteral().equals(subType)
                                    || TypeEnum.Enum.getLiteral().equals(subType)) {
                                if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.get(name).isEmpty()) {
                                    if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                if (!"true".equals(value) || !"false".equals(value)) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                try {
                                                    Long.valueOf(value);
                                                } catch (NumberFormatException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                try {
                                                    Double.valueOf(value);
                                                } catch (NumberFormatException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.String.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            }
                                        }
                                    } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                                requestBodyErrors.put(name, "is required");
                                                break;
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            List<String> enumItems = enumItemDictionary.get(enumId);
                                            if (!enumItems.contains(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    requestBodyErrors.put(name, "is required");
                                }
                            } else if (TypeEnum.File.getLiteral().equals(subType)) {
                                if (requestBodyFormFileDictionary.get(name) != null && !requestBodyFormFileDictionary.isEmpty()) {
                                    for (MultipartFile value : requestBodyFormFileDictionary.get(name)) {
                                        if (value == null || value.isEmpty()) {
                                            requestBodyErrors.put(name, "is required");
                                            break;
                                        }
                                    }
                                } else {
                                    requestBodyErrors.put(name, "is required");
                                }
                            }
                        }
                        // endregion
                    } else {
                        // region not required
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (TypeEnum.Boolean.getLiteral().equals(type)
                                    || TypeEnum.Long.getLiteral().equals(type)
                                    || TypeEnum.Double.getLiteral().equals(type)
                                    || TypeEnum.String.getLiteral().equals(type)
                                    || TypeEnum.Time.getLiteral().equals(type)
                                    || TypeEnum.Date.getLiteral().equals(type)
                                    || TypeEnum.DateTime.getLiteral().equals(type)
                                    || TypeEnum.Enum.getLiteral().equals(type)) {
                                if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.get(name).isEmpty()) {
                                    if (TypeEnum.Boolean.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            if (!"true".equals(value) || !"false".equals(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Long.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                Long.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Double.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                Double.valueOf(value);
                                            } catch (NumberFormatException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.String.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        }
                                    } else if (TypeEnum.Time.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Date.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        if (value == null || "".equals(value)) {
                                        } else {
                                            try {
                                                DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                            } catch (ParseException e) {
                                                requestBodyErrors.put(name, "is invalid");
                                            }
                                        }
                                    } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                                        String value = requestBodyFormDataDictionary.get(name).get(0);
                                        List<String> enumItems = enumItemDictionary.get(enumId);
                                        if (!enumItems.contains(value)) {
                                            requestBodyErrors.put(name, "is invalid");
                                        }
                                    }
                                }
                            } else if (TypeEnum.File.getLiteral().equals(type)) {
                                if (requestBodyFormFileDictionary.get(name) != null && !requestBodyFormFileDictionary.get(name).isEmpty()) {
                                    MultipartFile value = requestBodyFormFileDictionary.get(name).get(0);
                                }
                            }
                        } else {
                            if (TypeEnum.Boolean.getLiteral().equals(subType)
                                    || TypeEnum.Long.getLiteral().equals(subType)
                                    || TypeEnum.Double.getLiteral().equals(subType)
                                    || TypeEnum.String.getLiteral().equals(subType)
                                    || TypeEnum.Time.getLiteral().equals(subType)
                                    || TypeEnum.Date.getLiteral().equals(subType)
                                    || TypeEnum.DateTime.getLiteral().equals(subType)
                                    || TypeEnum.Enum.getLiteral().equals(subType)) {
                                if (requestBodyFormDataDictionary.get(name) != null && !requestBodyFormDataDictionary.get(name).isEmpty()) {
                                    if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                if (!"true".equals(value) || !"false".equals(value)) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                try {
                                                    Long.valueOf(value);
                                                } catch (NumberFormatException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                try {
                                                    Double.valueOf(value);
                                                } catch (NumberFormatException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.String.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            }
                                        }
                                    } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            if (value == null || "".equals(value)) {
                                            } else {
                                                try {
                                                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                                                } catch (ParseException e) {
                                                    requestBodyErrors.put(name, "is invalid");
                                                    break;
                                                }
                                            }
                                        }
                                    } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                                        for (String value : requestBodyFormDataDictionary.get(name)) {
                                            List<String> enumItems = enumItemDictionary.get(enumId);
                                            if (!enumItems.contains(value)) {
                                                requestBodyErrors.put(name, "is invalid");
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (TypeEnum.File.getLiteral().equals(subType)) {
                                if (requestBodyFormFileDictionary != null && !requestBodyFormFileDictionary.isEmpty()) {
                                    for (MultipartFile value : requestBodyFormFileDictionary.get(name)) {
                                    }
                                }
                            }
                        }
                        // endregion
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
                String type = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_TYPE);
                String enumId = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID);
                Boolean bodyRequired = (Boolean) restRecord.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                if (bodyRequired && (json == null || "".equals(json))) {
                    requestBodyErrors.put("requestBody", "is required");
                }
                if (json != null && !"".equals(json)) {
                    if (TypeEnum.Boolean.getLiteral().equals(type)) {
                        try {
                            gson.fromJson(json, Boolean.class);
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.Long.getLiteral().equals(type)) {
                        try {
                            gson.fromJson(json, Long.class);
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.Double.getLiteral().equals(type)) {
                        try {
                            gson.fromJson(json, Double.class);
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.String.getLiteral().equals(type)) {
                        try {
                            gson.fromJson(json, String.class);
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.Date.getLiteral().equals(type)) {
                        try {
                            String value = gson.fromJson(json, String.class);
                            DateFormatUtils.ISO_DATE_FORMAT.parse(value);
                        } catch (JsonSyntaxException | ParseException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.Time.getLiteral().equals(type)) {
                        try {
                            String value = gson.fromJson(json, String.class);
                            DateFormatUtils.ISO_TIME_NO_T_FORMAT.parse(value);
                        } catch (JsonSyntaxException | ParseException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                        try {
                            String value = gson.fromJson(json, String.class);
                            DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(value);
                        } catch (JsonSyntaxException | ParseException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                    } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                        Map<String, Object> enumRecord = enumDictionary.get(enumId);
                        String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                        List<String> enumValues = enumItemDictionary.get(enumId);
                        if (TypeEnum.Boolean.getLiteral().equals(enumType)) {
                            try {
                                String value = String.valueOf(gson.fromJson(json, Boolean.class));
                                if (!enumValues.contains(value)) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                        } else if (TypeEnum.Long.getLiteral().equals(enumType)) {
                            try {
                                String value = String.valueOf(gson.fromJson(json, Long.class));
                                if (!enumValues.contains(value)) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                        } else if (TypeEnum.Double.getLiteral().equals(enumType)) {
                            try {
                                String value = String.valueOf(gson.fromJson(json, Double.class));
                                if (!enumValues.contains(value)) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                        } else if (TypeEnum.Character.getLiteral().equals(enumType)
                                || TypeEnum.String.getLiteral().equals(enumType)
                                || TypeEnum.Time.getLiteral().equals(enumType)
                                || TypeEnum.Date.getLiteral().equals(enumType)
                                || TypeEnum.DateTime.getLiteral().equals(enumType)) {
                            try {
                                String value = gson.fromJson(json, String.class);
                                if (!enumValues.contains(value)) {
                                    requestBodyErrors.put("requestBody", "is invalid");
                                }
                            } catch (JsonSyntaxException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                        }
                    } else if (TypeEnum.Map.getLiteral().equals(type)) {
                        String jsonId = (String) restRecord.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID);
                        List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                        Map<String, Object> jsonObject = gson.fromJson(json, mapType);
                        for (Map<String, Object> jsonField : jsonFields) {
                            validateJsonField(requestBodyErrors, jdbcTemplate, jsonObject, jsonField);
                        }
                        // find map type
                    } else if (TypeEnum.List.getLiteral().equals(type)) {
                        // find subtype and repeat subtype checking again
                    }
                }
            }
        }
        // endregion

        return null;
    }

    private void validateJsonField(Map<String, Object> errors, JdbcTemplate jdbcTemplate, Map<String, Object> parentJson, Map<String, Object> jsonField) {
        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);
        String jsonId = (String) jsonField.get(Jdbc.JsonField.MAP_JSON_ID);
        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
        Boolean required = (Boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
        if (required) {
            if (TypeEnum.Boolean.getLiteral().equals(type)) {
                Boolean value = (Boolean) parentJson.get(name);
                if (value == null) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Long.getLiteral().equals(type)) {
                Long value = (Long) parentJson.get(name);
                if (value == null) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Double.getLiteral().equals(type)) {
                Double value = (Double) parentJson.get(name);
                if (value == null) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Time.getLiteral().equals(type)) {
                String value = (String) parentJson.get(name);
                if (value == null || "".equals(value)) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Date.getLiteral().equals(type)) {
                String value = (String) parentJson.get(name);
                if (value == null || "".equals(value)) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.DateTime.getLiteral().equals(type)) {
                String value = (String) parentJson.get(name);
                if (value == null || "".equals(value)) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                String value = (String) parentJson.get(name);
                if (value == null || "".equals(value)) {
                    errors.put(name, "is required");
                }
            } else if (TypeEnum.Map.getLiteral().equals(type)) {
                Map<String, Object> childJson = (Map<String, Object>) parentJson.get(name);
            } else if (TypeEnum.List.getLiteral().equals(type)) {
                List<Object> values = (List<Object>) parentJson.get(name);
                for (Object value : values) {
                    
                }
            }
        } else {

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
        Object http(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }
}
