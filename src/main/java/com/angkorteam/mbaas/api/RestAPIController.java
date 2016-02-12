package com.angkorteam.mbaas.api;

import com.angkorteam.baasbox.sdk.java.json.ChangePasswordJson;
import com.angkorteam.baasbox.sdk.java.request.SendPushNotificationRequest;
import com.angkorteam.mbaas.Constants;
import com.angkorteam.mbaas.enums.ColumnEnum;
import com.angkorteam.mbaas.enums.ResultEnum;
import com.angkorteam.mbaas.enums.ScopeEnum;
import com.angkorteam.mbaas.mariadb.JdbcFunction;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.Table;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.response.Response;
import com.angkorteam.mbaas.service.RequestHeader;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.flywaydb.core.internal.dbsupport.*;
import org.jasypt.encryption.StringEncryptor;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterBatchUpdateUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> login(
            HttpServletRequest request,
            @RequestBody LoginRequest requestBody
    ) {
        LOGGER.info("/login {}", gson.toJson(requestBody));
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");

        List<Condition> where = new ArrayList<>();
        where.add(userTable.LOGIN.eq(requestBody.getUsername()));
        where.add(userTable.PASSWORD.eq(requestBody.getPassword()));

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(where).fetchOneInto(userTable);
        if (userRecord != null) {
            responseBody.setHttpCode(HttpStatus.OK.value());
            responseBody.setResult(ResultEnum.OK.getLiteral());

            Map<String, Object> loginResponse = new HashMap<>();
            String tokenId = UUID.randomUUID().toString();
            Date dateCreated = new Date();


            TokenRecord tokenRecord = context.newRecord(tokenTable);
            tokenRecord.setTokenId(tokenId);
            tokenRecord.setDateCreated(new Timestamp(dateCreated.getTime()));
            tokenRecord.setUserId(userRecord.getUserId());
            tokenRecord.setDeleted(false);
            tokenRecord.store();

            loginResponse.put("token", tokenId);
            loginResponse.put("dateCreated", dateCreated);
            loginResponse.put("login", userRecord.getLogin());
            responseBody.setData(loginResponse);
        } else {
            responseBody.setHttpCode(HttpStatus.BAD_REQUEST.value());
            responseBody.setResult(ResultEnum.ERROR.getLiteral());
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signup(
            HttpServletRequest request,
            @RequestBody SignupRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        // field duplication check
        List<String> fields = new LinkedList<>();
        boolean error = false;
        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (error) {
            return null;
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(Tables.USER.getName())).fetchOneInto(tableTable);

        int fieldCount = context.selectCount().from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).and(fieldTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            return null;
        }

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        String appCode = requestBody.getAppCode();
        String login = requestBody.getUsername();
        String password = requestBody.getPassword();

        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setDeleted(false);
        userRecord.setAccountNonExpired(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setDisabled(false);
        userRecord.setLogin(login);
        userRecord.setPassword(password);
        userRecord.store();

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        if (tableRecord != null) {
            for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
                fieldRecords.put(fieldRecord.getName(), fieldRecord);
            }
        }
        Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
        for (FieldRecord blobRecord : context.select(fieldTable.fields()).from(fieldTable)
                .where(fieldTable.SQL_TYPE.eq("BLOB"))
                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.VIRTUAL.eq(false))
                .fetchInto(fieldTable)) {
            blobRecords.put(blobRecord.getFieldId(), blobRecord);
        }

        Map<Integer, String> visiblity = new LinkedHashMap<>();
        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByFriend.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByRegisteredUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByTheUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<Integer, String> entry : visiblity.entrySet()) {
            UserPrivacyRecord userPrivacyRecord = context.newRecord(userPrivacyTable);
            userPrivacyRecord.setFieldId(entry.getKey());
            userPrivacyRecord.setScope(entry.getValue());
            userPrivacyRecord.setUserId(userRecord.getUserId());
            userPrivacyRecord.store();
        }

        if (!virtualColumns.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : virtualColumns.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnNames.add(entry.getKey() + " = " + "COLUMN_CREATE(" + StringUtils.join(entry.getValue(), ",") + ")");
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("update " + Tables.USER.getName() + " set " + StringUtils.join(columnNames, ", ") + " where " + Tables.USER.USER_ID.getName() + " = " + userRecord.getUserId(), columnValues);
        }

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(HttpStatus.OK.value());

        Map<String, Object> signupResponse = new HashMap<>();
        String tokenId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        TokenRecord tokenRecord = context.newRecord(tokenTable);
        tokenRecord.setTokenId(tokenId);
        tokenRecord.setDateCreated(new Timestamp(dateCreated.getTime()));
        tokenRecord.setUserId(userRecord.getUserId());
        tokenRecord.setDeleted(false);
        tokenRecord.store();

        signupResponse.put("token", tokenId);
        signupResponse.put("dateCreated", dateCreated);
        signupResponse.put("login", userRecord.getLogin());
        responseBody.setData(signupResponse);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        Integer userId = tokenRecord.getUserId();
        context.delete(tokenTable).where(tokenTable.USER_ID.eq(userId)).execute();

        responseBody.setHttpCode(200);
        responseBody.setResult(ResultEnum.OK.getLiteral());

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        context.delete(tokenTable).where(tokenTable.TOKEN_ID.eq(token)).execute();

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(200);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/suspend",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        User userTable = Tables.USER.as("userTable");

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = null;

        if (tokenRecord != null) {
            context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        }

        if (userRecord != null) {
            userRecord.setAccountNonLocked(false);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/suspend/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord != null) {
            userRecord.setAccountNonLocked(false);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/activate/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activateUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        User userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord != null) {
            userRecord.setAccountNonLocked(true);
            userRecord.update();
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loggedUserProfile(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        User userTable = Tables.USER.as("userTable");

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = null;

        if (tokenRecord != null) {
            context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        }

        if (userRecord != null) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("login", userRecord.getLogin());
            responseBody.setData(data);
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfile(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UpdateUserProfileRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        // field duplication check
        List<String> fields = new LinkedList<>();
        boolean error = false;
        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    error = true;
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (error) {
            return null;
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(Tables.USER.getName())).fetchOneInto(tableTable);

        int fieldCount = context.selectCount().from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).and(fieldTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            return null;
        }

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        if (tableRecord != null) {
            for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
                fieldRecords.put(fieldRecord.getName(), fieldRecord);
            }
        }
        Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
        for (FieldRecord blobRecord : context.select(fieldTable.fields()).from(fieldTable)
                .where(fieldTable.SQL_TYPE.eq("BLOB"))
                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.VIRTUAL.eq(false))
                .fetchInto(fieldTable)) {
            blobRecords.put(blobRecord.getFieldId(), blobRecord);
        }

        Map<Integer, String> visiblity = new LinkedHashMap<>();
        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByFriend.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByRegisteredUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByTheUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);

        if (!virtualColumns.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : virtualColumns.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnNames.add(entry.getKey() + " = " + "COLUMN_CREATE(" + StringUtils.join(entry.getValue(), ",") + ")");
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("update " + Tables.USER.getName() + " set " + StringUtils.join(columnNames, ", ") + " where " + Tables.USER.USER_ID.getName() + " = " + userRecord.getUserId(), columnValues);
        }

        for (Map.Entry<Integer, String> entry : visiblity.entrySet()) {
            Integer userId = userRecord.getUserId();
            Integer fieldId = entry.getKey();
            String scope = entry.getValue();
            UserPrivacyRecord userPrivacyRecord = context.select(userPrivacyTable.fields()).from(userPrivacyTable).where(userPrivacyTable.USER_ID.eq(userId)).and(userPrivacyTable.FIELD_ID.eq(fieldId)).fetchOneInto(userPrivacyTable);
            if (userPrivacyRecord != null) {
                userPrivacyRecord.setScope(scope);
                userPrivacyRecord.update();
            } else {
                userPrivacyRecord = context.newRecord(userPrivacyTable);
                userPrivacyRecord.setFieldId(entry.getKey());
                userPrivacyRecord.setUserId(userRecord.getUserId());
                userPrivacyRecord.setScope(entry.getValue());
                userPrivacyRecord.store();
            }
        }

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(HttpStatus.OK.value());

        Map<String, Object> signupResponse = new HashMap<>();
        String tokenId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        signupResponse.put("token", tokenId);
        signupResponse.put("dateCreated", dateCreated);
        signupResponse.put("login", userRecord.getLogin());
        responseBody.setData(signupResponse);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfile(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUsers(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchUsersRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        List<UserRecord> userRecords = context.select(userTable.fields()).from(userTable).fetchInto(userTable);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changePassword(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangePasswordJson requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        userRecord.setPassword(requestBody.getNewPassword());
        userRecord.update();

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changeUsername(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangeUsernameRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        Response responseBody = new Response();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String appVersion = configuration.getString(Constants.APP_VERSION);

        responseBody.setVersion(appVersion);
        RequestHeader.serve(responseBody, request);
        responseBody.setMethod(request.getMethod());

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        userRecord.setLogin(requestBody.getUsername());
        userRecord.update();
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/user/{username}/password/reset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> passwordReset(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);
        DateTime now = new DateTime();
        userRecord.setToken(UUID.randomUUID().toString());
        userRecord.setTokenExpiredDate(new Timestamp(now.plusMinutes(10).toDate().getTime()));
        // TODO : send mail link
        userRecord.update();

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/social",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> social(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLogin(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLoginRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialUnlink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUser(
            HttpServletRequest request,
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/following/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowing(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/followers/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("os") String os,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/disable/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> disablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/push/message",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> sendPushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody SendPushNotificationRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        String name = StringUtils.lowerCase(collection);

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord != null) {
            return null;
        }

        String primaryName = name + "_id";

        buffer.append("CREATE TABLE `" + name + "` (");
        buffer.append("`" + primaryName + "` INT(11) AUTO_INCREMENT, ");
        buffer.append("extra BLOB, ");
        buffer.append("PRIMARY KEY (`" + primaryName + "`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        tableRecord = context.newRecord(tableTable);
        tableRecord.setName(name);
        tableRecord.setSystem(false);
        tableRecord.store();

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(primaryName);
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(true);
            fieldRecord.setVirtual(false);
            fieldRecord.setExposed(true);
            fieldRecord.setJavaType(Integer.class.getName());
            fieldRecord.setSqlType("INT");
            fieldRecord.store();

            PrimaryRecord primaryRecord = context.newRecord(primaryTable);
            primaryRecord.setFieldId(fieldRecord.getFieldId());
            primaryRecord.setTableId(tableRecord.getTableId());
            primaryRecord.store();
        }

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName("extra");
            fieldRecord.setNullable(true);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Byte.class.getName() + "[]");
            fieldRecord.setSqlType("BLOB");
            fieldRecord.store();
        }

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        String name = StringUtils.lowerCase(collection);

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord == null || tableRecord.getSystem()) {
            return null;
        }

        context.delete(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(primaryTable).where(primaryTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(tableTable).where(tableTable.TABLE_ID.eq(tableRecord.getTableId())).execute();

        jdbcTemplate.execute("DROP TABLE `" + name + "`");

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody CreateDocumentRequest request
    ) {
        String name = StringUtils.lowerCase(collection);

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
        }

        for (Map.Entry<String, Object> entry : request.getDocument().entrySet()) {
            if (!fieldRecords.containsKey(entry.getKey())) {
                return null;
            }
        }

        Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
        for (FieldRecord blobRecord : context.select(fieldTable.fields()).from(fieldTable)
                .where(fieldTable.SQL_TYPE.eq("BLOB"))
                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.VIRTUAL.eq(false))
                .fetchInto(fieldTable)) {
            blobRecords.put(blobRecord.getFieldId(), blobRecord);
        }

        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : request.getDocument().entrySet()) {
            FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
            if (fieldRecord.getVirtual()) {
                FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                if (!virtualColumns.containsKey(physicalRecord.getName())) {
                    virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
                }
                virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
                virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
            } else {
                columnNames.add(entry.getKey());
                columnKeys.add(":" + entry.getKey());
                columnValues.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, List<String>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add("COLUMN_CREATE (" + StringUtils.join(entry.getValue(), ", ") + ")");
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        namedParameterJdbcTemplate.update("INSERT INTO " + name + "(" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", columnValues);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        String name = StringUtils.lowerCase(collection);

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
        }

        List<String> fields = new LinkedList<>();
        for (Map.Entry<String, FieldRecord> entry : fieldRecords.entrySet()) {
            FieldRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    fields.add(JdbcFunction.columnGet());
                } else {
                    fields.add("`" + entry.getKey() + "`");
                }
            }
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * from " + name + " where " + name + "_id = ?", id);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentByQuery(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody RetrieveDocumentByQueryRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}/count",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> countDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody ModifyDocumentRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/role/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/link/{sourceId}/{label}/{destinationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("sourceId") String sourceId,
            @PathVariable("label") String label,
            @PathVariable("destinationId") String destinationId,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> uploadFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UploadFileRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFileDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFilesDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveFilesDetailRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/asset",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> createAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CreateAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> deleteAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/asset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/dump.json",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchCurrentSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/{section}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchSectionSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/configuration/{section}/{key}/{value}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> updateValueSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @PathVariable("key") String key,
            @PathVariable("value") String value,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> listGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> readSpecificGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> enableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> disableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }
}
