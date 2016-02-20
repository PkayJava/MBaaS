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

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        UserTable userTable = Tables.USER.as("userTable");

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
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

        UserTable userTable = Tables.USER.as("userTable");
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

        UserTable userTable = Tables.USER.as("userTable");
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

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        UserTable userTable = Tables.USER.as("userTable");

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
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
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

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

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);

        int fieldCount = context.selectCount().from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).and(attributeTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            return ResponseEntity.ok(null);
        }

        UnknownResponse responseBody = new UnknownResponse();

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
        if (collectionRecord != null) {
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
                attributeRecords.put(attributeRecord.getName(), attributeRecord);
            }
        }
        Map<String, AttributeRecord> blobRecords = new LinkedHashMap<>();
        for (AttributeRecord blobRecord : context.select(attributeTable.fields()).from(attributeTable)
                .where(attributeTable.SQL_TYPE.eq("BLOB"))
                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(attributeTable.VIRTUAL.eq(false))
                .fetchInto(attributeTable)) {
            blobRecords.put(blobRecord.getAttributeId(), blobRecord);
        }

        Map<String, String> visiblity = new LinkedHashMap<>();
        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visiblity.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
                if (attributeRecord.getVirtual()) {
                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
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
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visiblity.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByFriend.getLiteral());
                if (attributeRecord.getVirtual()) {
                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
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
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visiblity.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByRegisteredUser.getLiteral());
                if (attributeRecord.getVirtual()) {
                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
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
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visiblity.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByTheUser.getLiteral());
                if (attributeRecord.getVirtual()) {
                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
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

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
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

        for (Map.Entry<String, String> entry : visiblity.entrySet()) {
            String userId = userRecord.getUserId();
            String fieldId = entry.getKey();
            String scope = entry.getValue();
            UserPrivacyRecord userPrivacyRecord = context.select(userPrivacyTable.fields()).from(userPrivacyTable).where(userPrivacyTable.USER_ID.eq(userId)).and(userPrivacyTable.ATTRIBUTE_ID.eq(fieldId)).fetchOneInto(userPrivacyTable);
            if (userPrivacyRecord != null) {
                userPrivacyRecord.setScope(scope);
                userPrivacyRecord.update();
            } else {
                userPrivacyRecord = context.newRecord(userPrivacyTable);
                userPrivacyRecord.setUserPrivacyId(UUID.randomUUID().toString());
                userPrivacyRecord.setAttributeId(entry.getKey());
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
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

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
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

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
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
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
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
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
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);
        DateTime now = new DateTime();
        userRecord.setPasswordResetToken(UUID.randomUUID().toString());
        userRecord.setPasswordResetTokenExpiredDate(now.plusMinutes(10).toDate());
        // TODO : send mail link
        userRecord.update();

        return ResponseEntity.ok(null);
    }
}
