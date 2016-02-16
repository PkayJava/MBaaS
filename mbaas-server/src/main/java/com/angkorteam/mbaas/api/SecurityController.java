package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ResultEnum;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.Request;
import com.angkorteam.mbaas.plain.request.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping("/security")
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

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
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signup(
            HttpServletRequest request,
            @RequestBody SecuritySignUpRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        Role roleTable = Tables.ROLE.as("roleTable");
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
            return ResponseEntity.ok(null);
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(Tables.USER.getName())).fetchOneInto(tableTable);

        int fieldCount = context.selectCount().from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).and(fieldTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            return ResponseEntity.ok(null);
        }

        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);

        Response responseBody = new Response();

        String login = requestBody.getUsername();
        String password = requestBody.getPassword();

        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setDeleted(false);
        userRecord.setRoleId(roleRecord.getRoleId());
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
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
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
        Map<String, Map<String, Serializable>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
                visiblity.put(fieldRecord.getFieldId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
                if (fieldRecord.getVirtual()) {
                    FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
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
            for (Map.Entry<String, Map<String, Serializable>> entry : virtualColumns.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnNames.add(entry.getKey() + " = " + JdbcFunction.columnCreate(entry.getValue()));
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
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> login(
            HttpServletRequest request,
            @RequestBody SecurityLoginRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Response responseBody = new Response();

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
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Response responseBody = new Response();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=> body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Response responseBody = new Response();

        Token tokenTable = Tables.TOKEN.as("tokenTable");
        context.delete(tokenTable).where(tokenTable.TOKEN_ID.eq(token)).execute();

        responseBody.setResult(ResultEnum.OK.getLiteral());
        responseBody.setHttpCode(200);

        return ResponseEntity.ok(responseBody);
    }

}
