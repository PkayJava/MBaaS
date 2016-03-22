package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryParameterTable;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.records.QueryParameterRecord;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.plain.enums.QueryInputParamTypeEnum;
import com.angkorteam.mbaas.plain.enums.QueryPermissionEnum;
import com.angkorteam.mbaas.plain.enums.QueryReturnTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.request.query.QueryExecuteRequest;
import com.angkorteam.mbaas.plain.response.query.QueryExecuteResponse;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by socheat on 2/22/16.
 */
@Controller
@RequestMapping(path = "/query")
public class QueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @Autowired
    private PermissionFactoryBean.Permission permission;

    @RequestMapping(
            path = "/execute/{query}",
            method = {RequestMethod.POST, RequestMethod.PUT},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<QueryExecuteResponse> executeJson(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("query") String query,
            @RequestBody(required = false) QueryExecuteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);

        if (queryRecord == null || queryRecord.getScript() == null || "".equals(queryRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(queryRecord.getSecurity())) {
            QueryExecuteResponse response = new QueryExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        }
        if (permission.isQueryOwner(session, queryRecord.getName()) || permission.hasQueryPermission(session, query, QueryPermissionEnum.Execute.getLiteral())) {
        } else {
            QueryExecuteResponse response = new QueryExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        }

        if (requestBody == null) {
            requestBody = new QueryExecuteRequest();
        }

        if (requestBody.getParameters() == null) {
            requestBody.setParameters(new LinkedHashMap<>());
        }

        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> errorMessages = queryValidation(query, requestBody, params);

        if (!errorMessages.isEmpty()) {
            QueryExecuteResponse response = new QueryExecuteResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(query(query, params));
    }

    @RequestMapping(
            path = "/execute/{query}",
            method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<QueryExecuteResponse> execute(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("query") String query,
            @RequestBody(required = false) QueryExecuteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);

        if (queryRecord == null || queryRecord.getScript() == null || "".equals(queryRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(queryRecord.getSecurity())) {
            QueryExecuteResponse response = new QueryExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        }

        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");

        Map<String, QueryParameterRecord> queryParameterRecords = new LinkedHashMap<>();
        for (QueryParameterRecord queryParameterRecord : context.select(queryParameterTable.fields()).from(queryParameterTable).where(queryParameterTable.QUERY_ID.eq(queryRecord.getQueryId())).fetchInto(queryParameterTable)) {
            queryParameterRecords.put(queryParameterRecord.getName(), queryParameterRecord);
        }

        if (requestBody == null) {
            requestBody = new QueryExecuteRequest();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    if (queryParameterRecords.containsKey(name)) {
                        QueryParameterRecord queryParameterRecord = queryParameterRecords.get(name);
                        Object value = parse(queryParameterRecord.getType(), values[0]);
                        if (value != null) {
                            requestBody.getParameters().put(name, value);
                        }
                    }
                } else {
                    if (queryParameterRecords.containsKey(name)) {
                        QueryParameterRecord queryParameterRecord = queryParameterRecords.get(name);
                        parseSubType(name, requestBody.getParameters(), queryParameterRecord.getSubType(), values);
                    }
                }
            }
        }

        if (requestBody.getParameters() == null) {
            requestBody.setParameters(new LinkedHashMap<>());
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    if (queryParameterRecords.containsKey(name)) {
                        QueryParameterRecord queryParameterRecord = queryParameterRecords.get(name);
                        Object value = parse(queryParameterRecord.getType(), values[0]);
                        if (value != null) {
                            requestBody.getParameters().put(name, value);
                        }
                    }
                } else {
                    if (queryParameterRecords.containsKey(name)) {
                        QueryParameterRecord queryParameterRecord = queryParameterRecords.get(name);
                        parseSubType(name, requestBody.getParameters(), queryParameterRecord.getSubType(), values);
                    }
                }
            }
        }

        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> errorMessages = queryValidation(query, requestBody, params);

        if (!errorMessages.isEmpty()) {
            QueryExecuteResponse response = new QueryExecuteResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(query(query, params));
    }

    protected void parseSubType(String name, Map<String, Object> params, String subType, String[] values) {
        if (QueryInputParamTypeEnum.Boolean.getLiteral().equals(subType)) {
            List<Boolean> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Boolean.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Byte.getLiteral().equals(subType)) {
            List<Byte> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Byte.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Short.getLiteral().equals(subType)) {
            List<Short> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Short.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Integer.getLiteral().equals(subType)) {
            List<Integer> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Integer.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Long.getLiteral().equals(subType)) {
            List<Long> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Long.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Float.getLiteral().equals(subType)) {
            List<Float> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Float.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Double.getLiteral().equals(subType)) {
            List<Double> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(Double.valueOf(value));
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Character.getLiteral().equals(subType)) {
            List<Character> objects = new ArrayList<>(values.length);
            for (String value : values) {
                if (value != null && value.length() == 1) {
                    objects.add(value.charAt(1));
                }
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.String.getLiteral().equals(subType)) {
            List<String> objects = new ArrayList<>(values.length);
            for (String value : values) {
                objects.add(value);
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Time.getLiteral().equals(subType)) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            List<String> objects = new ArrayList<>(values.length);
            for (String value : values) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                try {
                    objects.add(dateFormat.format(dateFormat.parse(value)));
                } catch (ParseException e) {
                    dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                    try {
                        objects.add(dateFormat.format(dateFormat.parse(value)));
                    } catch (ParseException e1) {
                    }
                }
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.Date.getLiteral().equals(subType)) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            List<String> objects = new ArrayList<>(values.length);
            for (String value : values) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                try {
                    objects.add(dateFormat.format(dateFormat.parse(value)));
                } catch (ParseException e) {
                    dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                    try {
                        objects.add(dateFormat.format(dateFormat.parse(value)));
                    } catch (ParseException e1) {
                    }
                }
            }
            params.put(name, objects);
        } else if (QueryInputParamTypeEnum.DateTime.getLiteral().equals(subType)) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            List<String> objects = new ArrayList<>(values.length);
            for (String value : values) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                try {
                    objects.add(dateFormat.format(dateFormat.parse(value)));
                } catch (ParseException e1) {
                }
            }
            params.put(name, objects);
        }
    }


    protected Object parse(String type, String value) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (type.equals(QueryInputParamTypeEnum.Boolean.getLiteral())) {
            return Boolean.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Byte.getLiteral())) {
            return Byte.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Short.getLiteral())) {
            return Short.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Integer.getLiteral())) {
            return Integer.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Long.getLiteral())) {
            return Long.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Float.getLiteral())) {
            return Float.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Double.getLiteral())) {
            return Double.valueOf(value);
        } else if (type.equals(QueryInputParamTypeEnum.Character.getLiteral())) {
            if (value.length() == 1) {
                return value.charAt(0);
            }
        } else if (type.equals(QueryInputParamTypeEnum.String.getLiteral())) {
            return value;
        } else if (type.equals(QueryInputParamTypeEnum.Time.getLiteral())) {
            DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
            try {
                return dateFormat.format(dateFormat.parse(value));
            } catch (ParseException e) {
                dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                try {
                    return dateFormat.format(dateFormat.parse(value));
                } catch (ParseException e1) {
                }
            }
        } else if (type.equals(QueryInputParamTypeEnum.Date.getLiteral())) {
            DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
            try {
                return dateFormat.parse(value);
            } catch (ParseException e) {
                dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                try {
                    return dateFormat.format(dateFormat.parse(value));
                } catch (ParseException e1) {
                }
            }
        } else if (type.equals(QueryInputParamTypeEnum.DateTime.getLiteral())) {
            DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
            try {
                return dateFormat.format(dateFormat.parse(value));
            } catch (ParseException e) {
            }
        }
        return null;
    }

    protected Map<String, String> queryValidation(String query, QueryExecuteRequest requestBody, Map<String, Object> params) {
        Map<String, String> errorMessages = new LinkedHashMap<>();

        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);
        List<QueryParameterRecord> queryParameterRecords = context.select(queryParameterTable.fields()).from(queryParameterTable).where(queryParameterTable.QUERY_ID.eq(queryRecord.getQueryId())).fetchInto(queryParameterTable);

        for (QueryParameterRecord queryParameterRecord : queryParameterRecords) {
            if (!requestBody.getParameters().containsKey(queryParameterRecord.getName())) {
                errorMessages.put(queryParameterRecord.getName(), "is required");
            } else {
                if (QueryInputParamTypeEnum.Boolean.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Boolean) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not boolean");
                    }
                } else if (QueryInputParamTypeEnum.Byte.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Byte) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not byte");
                    }
                } else if (QueryInputParamTypeEnum.Short.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Short) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not short");
                    }
                } else if (QueryInputParamTypeEnum.Integer.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Integer) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not integer");
                    }
                } else if (QueryInputParamTypeEnum.Long.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Long) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not long");
                    }
                } else if (QueryInputParamTypeEnum.Float.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Float) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not float");
                    }
                } else if (QueryInputParamTypeEnum.Double.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Double) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not double");
                    }
                } else if (QueryInputParamTypeEnum.Character.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof Character) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not character");
                    }
                } else if (QueryInputParamTypeEnum.String.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof String) {
                        params.put(queryParameterRecord.getName(), requestBody.getParameters().get(queryParameterRecord.getName()));
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not string");
                    }
                } else if (QueryInputParamTypeEnum.Time.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) requestBody.getParameters().get(queryParameterRecord.getName()));
                            params.put(queryParameterRecord.getName(), value);
                        } catch (ParseException e) {
                            dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                            try {
                                value = dateFormat.parse((String) requestBody.getParameters().get(queryParameterRecord.getName()));
                                params.put(queryParameterRecord.getName(), value);
                            } catch (ParseException e1) {
                                errorMessages.put(queryParameterRecord.getName(), "is not time");
                            }
                        }
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not time");
                    }
                } else if (QueryInputParamTypeEnum.Date.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) requestBody.getParameters().get(queryParameterRecord.getName()));
                            params.put(queryParameterRecord.getName(), value);
                        } catch (ParseException e) {
                            dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                            try {
                                value = dateFormat.parse((String) requestBody.getParameters().get(queryParameterRecord.getName()));
                                params.put(queryParameterRecord.getName(), value);
                            } catch (ParseException e1) {
                                errorMessages.put(queryParameterRecord.getName(), "is not date");
                            }
                        }
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not date");
                    }
                } else if (QueryInputParamTypeEnum.DateTime.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) requestBody.getParameters().get(queryParameterRecord.getName()));
                            params.put(queryParameterRecord.getName(), value);
                        } catch (ParseException e) {
                            errorMessages.put(queryParameterRecord.getName(), "is not datetime");
                        }
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not datetime");
                    }
                } else if (QueryInputParamTypeEnum.List.getLiteral().equals(queryParameterRecord.getType())) {
                    if (requestBody.getParameters().get(queryParameterRecord.getName()) instanceof List) {
                        List<Object> values = (List<Object>) requestBody.getParameters().get(queryParameterRecord.getName());
                        if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Boolean.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Boolean) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of boolean");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Byte.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Byte) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of byte");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Short.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Short) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of short");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Integer.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Integer) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of integer");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Long.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Long) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of long");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Float.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Float) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of float");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Double.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Double) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of double");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Character.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof Character) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of character");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.String.getLiteral())) {
                            for (Object value : values) {
                                if (value instanceof String) {
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of string");
                                }
                            }
                            params.put(queryParameterRecord.getName(), values);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Time.getLiteral())) {
                            List<Date> dates = new ArrayList<>();
                            for (Object value : values) {
                                if (value instanceof String) {
                                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                                    Date date = null;
                                    try {
                                        date = dateFormat.parse((String) value);
                                        dates.add(date);
                                    } catch (ParseException e) {
                                        dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                                        try {
                                            date = dateFormat.parse((String) value);
                                            dates.add(date);
                                        } catch (ParseException e1) {
                                            errorMessages.put(queryParameterRecord.getName(), "is not list of time");
                                        }
                                    }
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of time");
                                }
                            }
                            params.put(queryParameterRecord.getName(), dates);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.Date.getLiteral())) {
                            List<Date> dates = new ArrayList<>();
                            for (Object value : values) {
                                if (value instanceof String) {
                                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                                    Date date = null;
                                    try {
                                        date = dateFormat.parse((String) value);
                                        dates.add(date);
                                    } catch (ParseException e) {
                                        dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                                        try {
                                            date = dateFormat.parse((String) value);
                                            dates.add(date);
                                        } catch (ParseException e1) {
                                            errorMessages.put(queryParameterRecord.getName(), "is not list of date");
                                        }
                                    }
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of date");
                                }
                            }
                            params.put(queryParameterRecord.getName(), dates);
                        } else if (queryParameterRecord.getSubType().equals(QueryInputParamTypeEnum.DateTime.getLiteral())) {
                            List<Date> dates = new ArrayList<>();
                            for (Object value : values) {
                                if (value instanceof String) {
                                    DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                                    Date date = null;
                                    try {
                                        date = dateFormat.parse((String) value);
                                        dates.add(date);
                                    } catch (ParseException e) {
                                        errorMessages.put(queryParameterRecord.getName(), "is not list of datetime");
                                    }
                                } else {
                                    errorMessages.put(queryParameterRecord.getName(), "is not list of datetime");
                                }
                            }
                            params.put(queryParameterRecord.getName(), dates);
                        }
                    } else {
                        errorMessages.put(queryParameterRecord.getName(), "is not list");
                    }
                }
            }
        }

        return errorMessages;
    }

    protected QueryExecuteResponse query(String query, Map<String, Object> params) {
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        QueryExecuteResponse response = new QueryExecuteResponse();
        try {
            if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Boolean.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Byte.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Short.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Integer.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Long.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Float.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Double.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Character.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, String.class));
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    response.setData(dateFormat.format(value));
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    response.setData(dateFormat.format(value));
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    response.setData(dateFormat.format(value));
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                response.setData(namedParameterJdbcTemplate.queryForMap(queryRecord.getScript(), params));

            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.List.getLiteral())) {
                if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Boolean.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Byte.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Short.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Integer.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Long.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Float.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Double.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Character.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, String.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class));
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                    response.setData(namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params));
                }
            }
        } catch (EmptyResultDataAccessException e) {
        } catch (IncorrectResultSetColumnCountException | IncorrectResultSizeDataAccessException | BadSqlGrammarException e) {
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResult(e.getMessage());
        }
        return response;
    }
}
