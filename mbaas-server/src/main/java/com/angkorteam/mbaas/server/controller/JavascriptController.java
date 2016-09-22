package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.response.JavascriptResponse;
import com.angkorteam.mbaas.plain.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.server.Jdbc;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.script.ScriptEngine;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    @RequestMapping(path = "/**")
    public ResponseEntity<JavascriptResponse> execute(
            HttpServletRequest req,
            Identity identity
    ) throws IOException, ServletException {

        JavascriptResponse responseEntity = new JavascriptResponse();

        try {

            byte[] requestBody = JavascriptControllerUtils.getRequestBodyByteArray(req);
            String springRequestContentType = JavascriptControllerUtils.getRequestContentType(req);
            boolean stage = ServletRequestUtils.getBooleanParameter(req, "stage", false);
            String pathInfo = JavascriptControllerUtils.getPathInfo(req);

            HostnameRecord hostnameRecord = JavascriptControllerUtils.getHostnameRecord(this.servletContext, req);
            ApplicationRecord applicationRecord = JavascriptControllerUtils.getApplicationRecord(this.servletContext, hostnameRecord);

            JdbcTemplate jdbcTemplate = JavascriptControllerUtils.getJdbcTemplate(this.servletContext, applicationRecord);
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);

            Map<String, Object> restObject = JavascriptControllerUtils.getRestObject(jdbcTemplate, pathInfo, req);

            Object newRequestBody = null;

            ScriptEngine scriptEngine = JavascriptControllerUtils.getScriptEngine(this.servletContext);
            Http http = JavascriptControllerUtils.getHttp(scriptEngine, restObject, stage);

            List<Map<String, Object>> requestQueryRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_QUERY + " WHERE " + Jdbc.RestRequestQuery.REST_ID + " = ?", restObject.get(Jdbc.Rest.REST_ID));
            List<Map<String, Object>> requestHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_REQUEST_HEADER + " WHERE " + Jdbc.RestRequestHeader.REST_ID + " = ?", restObject.get(Jdbc.Rest.REST_ID));
            List<Map<String, Object>> responseHeaderRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.REST_RESPONSE_HEADER + " WHERE " + Jdbc.RestResponseHeader.REST_ID + " = ?", restObject.get(Jdbc.Rest.REST_ID));

            Map<String, Map<String, Object>> headerMetaData = new HashMap<>();
            Map<String, Map<String, Object>> queryParameterMetaData = new HashMap<>();
            Map<String, Map<String, Object>> enumMetaData = new HashMap<>();
            Map<String, List<String>> enumItemMetaData = new HashMap<>();

            // MetaData Fetch
            JavascriptControllerUtils.fetchMetaData(named,
                    jdbcTemplate,
                    restObject,
                    requestQueryRecords,
                    requestHeaderRecords,
                    responseHeaderRecords,
                    headerMetaData,
                    queryParameterMetaData,
                    enumMetaData,
                    enumItemMetaData);

            // Rest Object Extraction
            Map<String, Object> requestBodyObject = new HashMap<>();
            Map<String, Object> responseBodyObject = new HashMap<>();
            JavascriptControllerUtils.restObjectExtraction(jdbcTemplate, restObject, requestBodyObject, responseBodyObject);

            // Request Data Extraction
            Map<String, List<String>> requestQueryParameterDictionary = new HashMap<>();
            Map<String, List<String>> requestHeaderDictionary = new HashMap<>();
            Map<String, List<String>> requestBodyApplicationFormUrlencodedDictionary = new HashMap<>();
            Map<String, List<String>> requestBodyMultipartFormDataStringItemDictionary = new HashMap<>();
            Map<String, List<MultipartFile>> requestBodyMultipartFormDataFileItemDictionary = new HashMap<>();
            Object requestBodyApplicationJsonDictionary = JavascriptControllerUtils.queryRequestDictionary(req,
                    this.gson,
                    requestBody,
                    springRequestContentType,
                    requestQueryParameterDictionary,
                    requestHeaderDictionary,
                    requestBodyApplicationFormUrlencodedDictionary,
                    requestBodyMultipartFormDataStringItemDictionary, requestBodyMultipartFormDataFileItemDictionary);

            // Validation Request Query Parameter
            Map<String, Object> newQueryParameter = new HashMap<>();
            Map<String, String> requestQueryErrors = new HashMap<>();
            JavascriptControllerUtils.validationRequestQueryParameter(requestQueryRecords,
                    queryParameterMetaData, enumMetaData, enumItemMetaData,
                    requestQueryParameterDictionary,
                    newQueryParameter,
                    requestQueryErrors);

            // Validation Request Header
            Map<String, Object> newRequestHeader = new HashMap<>();
            Map<String, String> requestHeaderErrors = new HashMap<>();
            JavascriptControllerUtils.validationRequestHeader(requestHeaderRecords,
                    headerMetaData,
                    enumMetaData,
                    enumItemMetaData,
                    requestHeaderDictionary,
                    newRequestHeader,
                    requestHeaderErrors);

            String method = (String) restObject.get(Jdbc.Rest.METHOD);

            // region Validation Request Body
            Map<String, Object> requestBodyErrors = new HashMap<>();
            if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.POST.name())) {
                String contentType = (String) restObject.get(Jdbc.Rest.REQUEST_CONTENT_TYPE);
                if (req.getContentType() == null || "".equals(req.getContentType())) {
                    return null;
                }
                if (!contentType.equals(springRequestContentType)) {
                    return null;
                }
                if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    // region application/x-www-form-urlencoded
                    Map<String, Object> iRequestBody = new TreeMap<>();
                    Boolean bodyRequired = (Boolean) restObject.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if (requestBodyApplicationFormUrlencodedDictionary == null || requestBodyApplicationFormUrlencodedDictionary.isEmpty()) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBodyObject.get(Jdbc.Json.JSON_ID));
                    for (Map<String, Object> jsonField : jsonFields) {
                        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
                        boolean required = (boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
                        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
                        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
                        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);

                        List<String> strings = new ArrayList<>();
                        String newType;
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyApplicationFormUrlencodedDictionary.get(name) != null && !requestBodyApplicationFormUrlencodedDictionary.get(name).isEmpty()) {
                                strings.add(requestBodyApplicationFormUrlencodedDictionary.get(name).get(0));
                            }
                            newType = type;
                        } else {
                            if (requestBodyApplicationFormUrlencodedDictionary.get(name) != null && !requestBodyApplicationFormUrlencodedDictionary.get(name).isEmpty()) {
                                strings.addAll(requestBodyApplicationFormUrlencodedDictionary.get(name));
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
                            enumRecord = enumMetaData.get(enumId);
                            enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            enumItems = enumItemMetaData.get(enumId);
                        }

                        try {
                            List<Object> values = new ArrayList<>();
                            for (String string : strings) {
                                Object value = null;
                                if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToBoolean(required, string);
                                } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToLong(required, string);
                                } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDouble(required, string);
                                } else if (TypeEnum.String.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToString(required, string);
                                } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToTime(required, string);
                                } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDate(required, string);
                                } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDateTime(required, string);
                                } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToEnum(required, enumType, enumItems, string);
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
                                iRequestBody.put(name, values.isEmpty() ? null : values.get(0));
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
                                iRequestBody.put(name, newValues);
                            }
                        } catch (IllegalArgumentException e) {
                            requestBodyErrors.put(name, e.getMessage());
                        }
                    }
                    newRequestBody = iRequestBody;
                    // endregion
                } else if (contentType.equals(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    // region multipart/form-data
                    Map<String, Object> iRequestBody = new HashMap<>();
                    Boolean bodyRequired = (Boolean) restObject.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if ((requestBodyMultipartFormDataStringItemDictionary == null || requestBodyMultipartFormDataStringItemDictionary.isEmpty()) && (requestBodyMultipartFormDataFileItemDictionary == null || requestBodyMultipartFormDataFileItemDictionary.isEmpty())) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", requestBodyObject.get(Jdbc.Json.JSON_ID));
                    for (Map<String, Object> jsonField : jsonFields) {
                        String name = (String) jsonField.get(Jdbc.JsonField.NAME);
                        boolean required = (boolean) jsonField.get(Jdbc.JsonField.REQUIRED);
                        String type = (String) jsonField.get(Jdbc.JsonField.TYPE);
                        String subType = (String) jsonField.get(Jdbc.JsonField.SUB_TYPE);
                        String enumId = (String) jsonField.get(Jdbc.JsonField.ENUM_ID);

                        List<Object> objects = new ArrayList<>();
                        String newType;
                        if (!TypeEnum.List.getLiteral().equals(type)) {
                            if (requestBodyMultipartFormDataStringItemDictionary.get(name) != null && !requestBodyMultipartFormDataStringItemDictionary.get(name).isEmpty()) {
                                objects.add(requestBodyMultipartFormDataStringItemDictionary.get(name).get(0));
                            }
                            if (requestBodyMultipartFormDataFileItemDictionary.get(name) != null && !requestBodyMultipartFormDataFileItemDictionary.get(name).isEmpty()) {
                                objects.add(requestBodyMultipartFormDataFileItemDictionary.get(name).get(0));
                            }
                            newType = type;
                        } else {
                            if (requestBodyMultipartFormDataStringItemDictionary.get(name) != null && !requestBodyMultipartFormDataStringItemDictionary.get(name).isEmpty()) {
                                objects.addAll(requestBodyMultipartFormDataStringItemDictionary.get(name));
                            }
                            if (requestBodyMultipartFormDataFileItemDictionary.get(name) != null && !requestBodyMultipartFormDataFileItemDictionary.get(name).isEmpty()) {
                                objects.addAll(requestBodyMultipartFormDataFileItemDictionary.get(name));
                            }
                            newType = subType;
                        }
                        Map<String, Object> enumRecord = null;
                        String enumType = null;
                        List<String> enumItems = null;
                        if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            enumRecord = enumMetaData.get(enumId);
                            enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            enumItems = enumItemMetaData.get(enumId);
                        }

                        if (required) {
                            if (objects.isEmpty()) {
                                requestBodyErrors.put(name, "is required");
                            }
                        }

                        try {
                            List<Object> values = new ArrayList<>();
                            for (Object object : objects) {
                                Object value = null;
                                if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToBoolean(required, (String) object);
                                } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToLong(required, (String) object);
                                } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDouble(required, (String) object);
                                } else if (TypeEnum.String.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToString(required, (String) object);
                                } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToTime(required, (String) object);
                                } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDate(required, (String) object);
                                } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToDateTime(required, (String) object);
                                } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseStringToEnum(required, enumType, enumItems, (String) object);
                                } else if (TypeEnum.File.getLiteral().equals(newType)) {
                                    value = JavascriptControllerUtils.parseMultipartFileToByteArray(required, (MultipartFile) object);
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
                                    || TypeEnum.File.getLiteral().equals(type)
                                    || TypeEnum.Enum.getLiteral().equals(type)) {
                                iRequestBody.put(name, values.isEmpty() ? null : values.get(0));
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
                                } else if (TypeEnum.File.getLiteral().equals(subType)) {
                                    newValues = Array.newInstance(byte[].class, values.size());
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
                                iRequestBody.put(name, newValues);
                            }
                        } catch (IllegalArgumentException e) {
                            requestBodyErrors.put(name, e.getMessage());
                        }
                    }
                    newRequestBody = iRequestBody;
                    // endregion
                } else if (contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
                    // region application/octet-stream
                    Boolean bodyRequired = (Boolean) restObject.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired) {
                        if (requestBody == null || requestBody.length == 0) {
                            requestBodyErrors.put("requestBody", "is required");
                        }
                    }
                    newRequestBody = requestBody;
                    // endregion
                } else if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    // region application/json
                    // TODO : need to be checked
                    String type = (String) restObject.get(Jdbc.Rest.REQUEST_BODY_TYPE);
                    String subType = (String) restObject.get(Jdbc.Rest.REQUEST_BODY_SUB_TYPE);
                    String enumId = (String) restObject.get(Jdbc.Rest.REQUEST_BODY_ENUM_ID);
                    Boolean bodyRequired = (Boolean) restObject.get(Jdbc.Rest.REQUEST_BODY_REQUIRED);
                    if (bodyRequired && requestBodyApplicationJsonDictionary == null) {
                        requestBodyErrors.put("requestBody", "is required");
                    }

                    if (TypeEnum.Boolean.getLiteral().equals(type)) {
                        // region nobody
                        try {
                            try {
                                Boolean value = JavascriptControllerUtils.parseObjectToBoolean(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                Long value = JavascriptControllerUtils.parseObjectToLong(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                Double value = JavascriptControllerUtils.parseObjectToDouble(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                String value = JavascriptControllerUtils.parseObjectToString(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                Date value = JavascriptControllerUtils.parseObjectToTime(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                Date value = JavascriptControllerUtils.parseObjectToDate(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
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
                                Date value = JavascriptControllerUtils.parseObjectToDateTime(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
                            } catch (IllegalArgumentException e) {
                                requestBodyErrors.put("requestBody", e.getMessage());
                            }
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                        // endregion
                    } else if (TypeEnum.Enum.getLiteral().equals(type)) {
                        // region nobody
                        Map<String, Object> enumRecord = enumMetaData.get(enumId);
                        String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                        List<String> enumItems = enumItemMetaData.get(enumId);
                        try {
                            try {
                                Object value = JavascriptControllerUtils.parseObjectToEnum(bodyRequired, enumType, enumItems, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
                            } catch (IllegalArgumentException e) {
                                requestBodyErrors.put("requestBody", e.getMessage());
                            }
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                        // endregion
                    } else if (TypeEnum.Map.getLiteral().equals(type)) {
                        // region nobody
                        String jsonId = (String) restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID);
                        List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                        Map<String, Object> iRequestBody = new HashMap<>();
                        Map<String, Object> jsonObject = (Map<String, Object>) requestBodyApplicationJsonDictionary;
                        if (jsonObject.isEmpty()) {
                            requestBodyErrors.put("requestBody", "is required");
                        } else {
                            for (Map<String, Object> jsonField : jsonFields) {
                                JavascriptControllerUtils.validateJsonField(jdbcTemplate, iRequestBody, requestBodyErrors, jsonObject, jsonField, enumItemMetaData, enumMetaData);
                            }
                        }
                        newRequestBody = iRequestBody;
                        // endregion
                    } else if (TypeEnum.File.getLiteral().equals(type)) {
                        // region nobody
                        try {
                            try {
                                byte[] value = JavascriptControllerUtils.parseObjectToByteArray(bodyRequired, requestBodyApplicationJsonDictionary);
                                newRequestBody = value;
                            } catch (IllegalArgumentException e) {
                                requestBodyErrors.put("requestBody", e.getMessage());
                            }
                        } catch (JsonSyntaxException e) {
                            requestBodyErrors.put("requestBody", "is invalid");
                        }
                        // endregion
                    } else if (TypeEnum.List.getLiteral().equals(type)) {
                        // region nobody
                        List<Object> objects = (List<Object>) requestBodyApplicationJsonDictionary;
                        if (TypeEnum.Boolean.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Boolean[] value = JavascriptControllerUtils.parseObjectToBooleanArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Long.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Long[] value = JavascriptControllerUtils.parseObjectToLongArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Double.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Double[] value = JavascriptControllerUtils.parseObjectToDoubleArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.String.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                String[] value = JavascriptControllerUtils.parseObjectToStringArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Date.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Date[] value = JavascriptControllerUtils.parseObjectToDateArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Time.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Date[] value = JavascriptControllerUtils.parseObjectToTimeArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.DateTime.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Date[] value = JavascriptControllerUtils.parseObjectToDateTimeArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Enum.getLiteral().equals(subType)) {
                            // region nobody
                            Map<String, Object> enumRecord = enumMetaData.get(enumId);
                            String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            List<String> enumValues = enumItemMetaData.get(enumId);
                            Object value = JavascriptControllerUtils.parseObjectToEnumArray(bodyRequired, enumType, enumValues, objects);
                            newRequestBody = value;                                // endregion
                        } else if (TypeEnum.File.getLiteral().equals(subType)) {
                            // region nobody
                            try {
                                Object value = JavascriptControllerUtils.parseObjectToByteArrayArray(bodyRequired, objects);
                                newRequestBody = value;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Map.getLiteral().equals(subType)) {
                            // region nobody
                            String jsonId = (String) restObject.get(Jdbc.Rest.REQUEST_BODY_MAP_JSON_ID);
                            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                            try {
                                List<Map<String, Object>> iRequestBody = new ArrayList<>();
                                for (Object object : objects) {
                                    if (object instanceof Map) {
                                        Map<String, Object> i = new HashMap<>();
                                        for (Map<String, Object> jsonField : jsonFields) {
                                            JavascriptControllerUtils.validateJsonField(jdbcTemplate, i, requestBodyErrors, (Map) object, jsonField, enumItemMetaData, enumMetaData);
                                        }
                                        iRequestBody.add(i);
                                    } else {
                                        requestBodyErrors.put("requestBody", "is invalid");
                                        break;
                                    }
                                }
                                newRequestBody = iRequestBody;
                            } catch (JsonSyntaxException | ClassCastException e) {
                                requestBodyErrors.put("requestBody", "is invalid");
                            }
                            // endregion
                        }
                        // endregion
                    }
                    // endregion
                }
            }
            // endregion

            if (!requestQueryErrors.isEmpty() || !requestBodyErrors.isEmpty() || !requestHeaderErrors.isEmpty()) {
                if (requestQueryErrors != null && !requestQueryErrors.isEmpty()) {
                    responseEntity.setRequestQueryParameterErrors(requestQueryErrors);
                }
                if (requestBodyErrors != null && !requestBodyErrors.isEmpty()) {
                    responseEntity.setRequestBodyErrors(requestBodyErrors);
                }
                if (requestHeaderErrors != null && !requestHeaderErrors.isEmpty()) {
                    responseEntity.setRequestHeaderErrors(requestHeaderErrors);
                }
                return ResponseEntity.ok(responseEntity);
            }

            Map<String, Object> responseBodyErrors = new HashMap<>();
            String responseContentType = (String) restObject.get(Jdbc.Rest.RESPONSE_CONTENT_TYPE);
            String responseBodyType = (String) restObject.get(Jdbc.Rest.RESPONSE_BODY_TYPE);
            String responseBodySubType = (String) restObject.get(Jdbc.Rest.RESPONSE_BODY_SUB_TYPE);
            boolean responseBodyRequired = (boolean) restObject.get(Jdbc.Rest.RESPONSE_BODY_REQUIRED);

            CollectionFactory collectionFactory = new CollectionFactory();
            Map<String, Object> responseHeaderDictionary = new HashMap<>();
            Object response = http.http(collectionFactory, req, responseHeaderDictionary, newQueryParameter, newRequestHeader, newRequestBody);

            HttpHeaders newResponseHeader = new HttpHeaders();
            // region Validation Response Header
            Map<String, String> responseHeaderErrors = new HashMap<>();
            for (Map<String, Object> responseHeaderRecord : responseHeaderRecords) {
                String headerId = (String) responseHeaderRecord.get(Jdbc.RestResponseHeader.HTTP_HEADER_ID);
                Boolean required = (Boolean) responseHeaderRecord.get(Jdbc.RestResponseHeader.REQUIRED);
                Map<String, Object> httpHeader = headerMetaData.get(headerId);
                String name = (String) httpHeader.get(Jdbc.HttpHeader.NAME);
                String enumId = (String) httpHeader.get(Jdbc.HttpHeader.ENUM_ID);
                String type = (String) httpHeader.get(Jdbc.HttpHeader.TYPE);
                String subType = (String) httpHeader.get(Jdbc.HttpHeader.SUB_TYPE);

                String newType;
                List<Object> objects = new ArrayList<>();
                if (!TypeEnum.List.getLiteral().equals(type)) {
                    if (responseHeaderDictionary.get(name) != null) {
                        if (responseHeaderDictionary.get(name) instanceof String) {
                            if (!"".equals(responseHeaderDictionary.get(name))) {
                                objects.add(responseHeaderDictionary.get(name));
                            }
                        } else {
                            objects.add(responseHeaderDictionary.get(name));
                        }
                    }
                    newType = type;
                } else {
                    if (responseHeaderDictionary.get(name) != null) {
                        if (responseHeaderDictionary.get(name) instanceof List) {
                            for (Object object : (List<Object>) responseHeaderDictionary.get(name)) {
                                if (object != null) {
                                    if (object instanceof String) {
                                        if (!"".equals(object)) {
                                            objects.add(object);
                                        }
                                    } else {
                                        objects.add(object);
                                    }
                                }
                            }
                        }
                    }
                    newType = subType;
                }
                if (required) {
                    if (objects.isEmpty()) {
                        responseHeaderErrors.put(name, "is required");
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
                    List<String> values = new ArrayList<>();
                    for (Object object : objects) {
                        String value = null;
                        if (TypeEnum.Boolean.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseBooleanToString(required, object);
                        } else if (TypeEnum.Long.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseLongToString(required, object);
                        } else if (TypeEnum.Double.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseDoubleToString(required, object);
                        } else if (TypeEnum.String.getLiteral().equals(newType)) {
                            try {
                                value = JavascriptControllerUtils.parseStringToString(required, (String) object);
                            } catch (ClassCastException e) {
                                throw new IllegalArgumentException("is invalid");
                            }
                        } else if (TypeEnum.Time.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseDateTimeToString(required, DateFormatUtils.ISO_TIME_NO_T_FORMAT, object);
                        } else if (TypeEnum.Date.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseDateTimeToString(required, DateFormatUtils.ISO_DATE_FORMAT, object);
                        } else if (TypeEnum.DateTime.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseDateTimeToString(required, HTTP_DATE_FORMAT, object);
                        } else if (TypeEnum.Enum.getLiteral().equals(newType)) {
                            value = JavascriptControllerUtils.parseEnumToString(required, HTTP_DATE_FORMAT, enumType, enumItems, object);
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
                        newResponseHeader.add(name, values.get(0));
                    } else if (TypeEnum.List.getLiteral().equals(type)) {
                        newResponseHeader.put(name, values);
                    }

                } catch (IllegalArgumentException e) {
                    responseHeaderErrors.put(name, e.getMessage());
                }
            }
            // endregion

            if (MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(responseContentType)) {
                if (responseBodyRequired) {
                    if (response == null) {
                        responseBodyErrors.put("responseBody", "is required");
                    } else {
                        if (response instanceof byte[]) {
                            if (((byte[]) response).length == 0) {
                                responseBodyErrors.put("responseBody", "is required");
                            }
                        } else {
                            responseBodyErrors.put("responseBody", "is invalid");
                        }
                    }
                } else {
                    if (response != null) {
                        if (response instanceof byte[]) {
                        } else {
                            responseBodyErrors.put("responseBody", "is invalid");
                        }
                    }
                }
            } else if (MediaType.APPLICATION_JSON_VALUE.equals(responseContentType)) {
                try {
                    if (TypeEnum.Boolean.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        JavascriptControllerUtils.validateBoolean(responseBodyRequired, response);
                        // endregion
                    } else if (TypeEnum.Long.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        JavascriptControllerUtils.validateLong(responseBodyRequired, response);
                        // endregion
                    } else if (TypeEnum.Double.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        JavascriptControllerUtils.validateDouble(responseBodyRequired, response);
                        // endregion
                    } else if (TypeEnum.String.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        JavascriptControllerUtils.validateString(responseBodyRequired, response);
                        // endregion
                    } else if (TypeEnum.Time.getLiteral().equals(responseBodyType)
                            || TypeEnum.Date.getLiteral().equals(responseBodyType)
                            || TypeEnum.DateTime.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        JavascriptControllerUtils.validateDate(responseBodyRequired, response);
                        // endregion
                    } else if (TypeEnum.Map.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        String jsonId = (String) restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID);
                        List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                        Map<String, Object> jsonObject = (Map<String, Object>) response;
                        if (jsonObject.isEmpty()) {
                            responseBodyErrors.put("responseBody", "is required");
                        } else {
                            for (Map<String, Object> jsonField : jsonFields) {
                                JavascriptControllerUtils.validateMapField(jdbcTemplate, responseBodyErrors, jsonObject, jsonField, enumItemMetaData, enumMetaData);
                            }
                        }
                        // endregion
                    } else if (TypeEnum.Enum.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        String enumId = (String) responseBodyObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID);
                        Map<String, Object> enumRecord = enumMetaData.get(enumId);
                        String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                        List<String> enumItems = enumItemMetaData.get(enumId);
                        JavascriptControllerUtils.validateEnum(responseBodyRequired, enumType, enumItems, response);
                        // endregion
                    } else if (TypeEnum.List.getLiteral().equals(responseBodyType)) {
                        // region nobody
                        if (TypeEnum.Boolean.getLiteral().equals(responseBodySubType)) {
                            JavascriptControllerUtils.validateBooleanArray(responseBodyRequired, (List<Object>) response);
                            // endregion
                        } else if (TypeEnum.Long.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            JavascriptControllerUtils.validateLongArray(responseBodyRequired, (List<Object>) response);
                            // endregion
                        } else if (TypeEnum.Double.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            JavascriptControllerUtils.validateDoubleArray(responseBodyRequired, (List<Object>) response);
                            // endregion
                        } else if (TypeEnum.String.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            JavascriptControllerUtils.validateStringArray(responseBodyRequired, (List<Object>) response);
                            // endregion
                        } else if (TypeEnum.Time.getLiteral().equals(responseBodySubType)
                                || TypeEnum.Date.getLiteral().equals(responseBodySubType)
                                || TypeEnum.DateTime.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            JavascriptControllerUtils.validateDateArray(responseBodyRequired, (List<Object>) response);
                            // endregion
                        } else if (TypeEnum.Map.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            String jsonId = (String) restObject.get(Jdbc.Rest.RESPONSE_BODY_MAP_JSON_ID);
                            List<Map<String, Object>> jsonFields = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ?", jsonId);
                            try {
                                List<Object> objects = (List<Object>) response;
                                for (Object object : objects) {
                                    if (object instanceof Map) {
                                        for (Map<String, Object> jsonField : jsonFields) {
                                            JavascriptControllerUtils.validateMapField(jdbcTemplate, new HashMap<>(), requestBodyErrors, (Map) object, jsonField, enumItemMetaData, enumMetaData);
                                        }
                                    } else {
                                        responseBodyErrors.put("responseBody", "is invalid");
                                        break;
                                    }
                                }
                            } catch (JsonSyntaxException | ClassCastException e) {
                                responseBodyErrors.put("responseBody", "is invalid");
                            }
                            // endregion
                        } else if (TypeEnum.Enum.getLiteral().equals(responseBodySubType)) {
                            // region nobody
                            String enumId = (String) responseBodyObject.get(Jdbc.Rest.RESPONSE_BODY_ENUM_ID);
                            Map<String, Object> enumRecord = enumMetaData.get(enumId);
                            String enumType = (String) enumRecord.get(Jdbc.Enum.TYPE);
                            List<String> enumItems = enumItemMetaData.get(enumId);
                            JavascriptControllerUtils.validateEnumArray(responseBodyRequired, enumType, enumItems, (List<Object>) response);
                            // endregion
                        }
                        // endregion
                    }
                } catch (IllegalArgumentException e) {
                    responseBodyErrors.put("responseBody", e.getMessage());
                }
            }

            if (!responseBodyErrors.isEmpty() || !responseHeaderErrors.isEmpty()) {
                if (!responseBodyErrors.isEmpty()) {
                    responseEntity.setResponseBodyErrors(responseBodyErrors);
                }
                responseEntity.setResponseBodyErrors(responseBodyErrors);
                if (!responseHeaderErrors.isEmpty()) {
                    responseEntity.setResponseHeaderErrors(responseHeaderErrors);
                }
                return ResponseEntity.ok(responseEntity);
            }
            responseEntity.setData(response);
            return new ResponseEntity<>(responseEntity, newResponseHeader, HttpStatus.OK);
        } catch (Throwable e) {
            responseEntity.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseEntity.setResultMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            List<String> stackTraces = new ArrayList<>();
            for (StackTraceElement element : e.getStackTrace()) {
                stackTraces.add(element.getClassName() + "." + element.getMethodName() + " at " + element.getLineNumber() + " in " + element.getFileName());
            }
            responseEntity.setStackTrace(stackTraces);
            if (debugName != null && !"".equals(debugName)) {
                responseEntity.setDebugMessage(e.getMessage() + " name " + debugName);
            } else {
                responseEntity.setDebugMessage(e.getMessage());
            }
            return ResponseEntity.ok(responseEntity);
        }
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
            response.setResultCode(HttpStatus.UNAUTHORIZED.value());
            response.setResultMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return ResponseEntity.ok(response);
        } else {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResultMessage(throwable.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnResponse(boolean found, boolean error, Throwable throwable, Object responseBody) {
        if (!found) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setResultCode(HttpStatus.METHOD_NOT_ALLOWED.value());
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
        response.setResultCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setResultMessage(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        return ResponseEntity.ok(response);
    }

    public interface Http {
        Object http(CollectionFactory collectionFactory, HttpServletRequest request, Map<String, Object> responseHeader, Map<String, Object> queryParameter, Map<String, Object> requestHeader, Object requestBody);
    }

    public static class CollectionFactory implements Serializable {

        public List<Object> createList() {
            return new ArrayList<>();
        }

        public Map<String, Object> createMap() {
            return new HashMap<>();
        }

    }

}
