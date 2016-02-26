package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.request.ChangeUsernameRequest;
import com.angkorteam.mbaas.plain.request.me.*;
import com.angkorteam.mbaas.plain.response.Response;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.plain.response.me.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.*;

/**
 * Created by socheat on 2/25/16.
 */
@Controller
@RequestMapping("/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

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
            method = RequestMethod.POST, path = "/suspend",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MeSuspendResponse> suspend(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MeSuspendRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        UserTable userTable = Tables.USER.as("userTable");

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        UserRecord userRecord = null;

        if (tokenRecord != null) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        }

        if (userRecord != null) {
            userRecord.setAccountNonLocked(false);
            userRecord.update();
        }

        MeSuspendResponse response = new MeSuspendResponse();
        response.setData(userRecord.getUserId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/retrieve",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MeRetrieveResponse> loggedUserProfile(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MeRetrieveRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        UserTable userTable = Tables.USER.as("userTable");

        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);

        UserRecord userRecord = null;
        if (sessionRecord != null) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
        }

        if (userRecord == null) {
            errorMessages.put("username", "is not found");
        }

        if (!errorMessages.isEmpty()) {
            MeRetrieveResponse response = new MeRetrieveResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        MeRetrieveResponse response = new MeRetrieveResponse();
        response.setData(userRecord.getUserId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/modify",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MeModifyResponse> updateUserProfile(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MeModifyRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        // field duplication check
        List<String> fields = new LinkedList<>();
        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "field is duplicated");
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "field is duplicated");
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "field is duplicated");
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "field is duplicated");
                } else {
                    fields.add(entry.getKey());
                }
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
        }

        for (String field : fields) {
            if (!attributeRecords.containsKey(field)) {
                errorMessages.put(field, "field is not found");
            }
        }

        if (!errorMessages.isEmpty()) {
            MeModifyResponse response = new MeModifyResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        Map<String, AttributeRecord> blobRecords = new LinkedHashMap<>();
        for (AttributeRecord blobRecord : context.select(attributeTable.fields()).from(attributeTable)
                .where(attributeTable.SQL_TYPE.eq("BLOB"))
                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(attributeTable.VIRTUAL.eq(false))
                .fetchInto(attributeTable)) {
            blobRecords.put(blobRecord.getAttributeId(), blobRecord);
        }

        Map<String, String> visibility = new LinkedHashMap<>();
        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
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
                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByFriend.getLiteral());
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
                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByRegisteredUser.getLiteral());
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
                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByTheUser.getLiteral());
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

        for (Map.Entry<String, String> entry : visibility.entrySet()) {
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

        MeModifyResponse response = new MeModifyResponse();

        response.setData(userRecord.getUserId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/password",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MePasswordResponse> changePassword(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MePasswordRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        userRecord.setPassword(requestBody.getNewPassword());
        userRecord.update();

        MePasswordResponse response = new MePasswordResponse();
        response.setData(userRecord.getUserId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MeUsernameResponse> changeUsername(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MeUsernameRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");

        SessionRecord tokenRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        userRecord.setLogin(requestBody.getUsername());
        userRecord.update();

        MeUsernameResponse response = new MeUsernameResponse();
        return ResponseEntity.ok(response);
    }
}
