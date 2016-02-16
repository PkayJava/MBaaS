package com.angkorteam.mbaas.server.api;

import com.angkorteam.baasbox.sdk.java.json.ChangePasswordJson;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ResultEnum;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.request.ChangeUsernameRequest;
import com.angkorteam.mbaas.plain.request.FetchUsersRequest;
import com.angkorteam.mbaas.plain.request.Request;
import com.angkorteam.mbaas.plain.request.UpdateUserProfileRequest;
import com.angkorteam.mbaas.plain.response.Response;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

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
            method = RequestMethod.PUT, path = "/me/suspend",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody Request requestBody
    ) {
        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody Request requestBody
    ) {
        UnknownResponse responseBody = new UnknownResponse();

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
//            responseBody.setData(data);
        }

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfile(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
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
            return ResponseEntity.ok(null);
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(Tables.USER.getName())).fetchOneInto(tableTable);

        int fieldCount = context.selectCount().from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).and(fieldTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            return ResponseEntity.ok(null);
        }

        UnknownResponse responseBody = new UnknownResponse();

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

        Map<String, Object> signupUnknownResponse = new HashMap<>();
        String tokenId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

//        signupResponse.put("token", tokenId);
//        signupResponse.put("dateCreated", dateCreated);
//        signupResponse.put("login", userRecord.getLogin());
//        responseBody.setData(signupResponse);

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfile(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody FetchUsersRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody ChangePasswordJson requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody ChangeUsernameRequest requestBody
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
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
}
