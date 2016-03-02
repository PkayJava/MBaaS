package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.jooq.enums.UserStatusEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.Request;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutSessionResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping(path = "/security")
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    //region /security/signup

    @RequestMapping(
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecuritySignUpResponse> signup(
            HttpServletRequest request,
            @RequestBody SecuritySignUpRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("username", "is required");
        } else {
            int count = context.selectCount().from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(Integer.class);
            if (count > 0) {
                errorMessages.put("username", "is not available");
            }
        }

        if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
            errorMessages.put("password", "is required");
        }

        // field duplication check
        List<String> fields = new LinkedList<>();
        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
                if (fields.contains(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                    break;
                } else {
                    fields.add(entry.getKey());
                }
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);

        int fieldCount = context.selectCount().from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).and(attributeTable.NAME.in(fields)).fetchOneInto(Integer.class);
        if (fields.size() > fieldCount) {
            errorMessages.put("attribute", "some attributes are not allow");
        }

        if (!errorMessages.isEmpty()) {
            SecuritySignUpResponse response = new SecuritySignUpResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);

        SecuritySignUpResponse responseBody = new SecuritySignUpResponse();

        String login = requestBody.getUsername();
        String password = requestBody.getPassword();

        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setUserId(UUID.randomUUID().toString());
        userRecord.setDeleted(false);
        userRecord.setRoleId(roleRecord.getRoleId());
        userRecord.setAccountNonExpired(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setStatus(UserStatusEnum.Active.getLiteral());
        userRecord.setLogin(login);
        userRecord.setPassword(password);
        userRecord.store();

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
        }

        Map<String, AttributeRecord> blobRecords = new LinkedHashMap<>();
        for (AttributeRecord blobRecord : context.select(attributeTable.fields()).from(attributeTable)
                .where(attributeTable.SQL_TYPE.eq("BLOB"))
                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(attributeTable.VIRTUAL.eq(false))
                .fetchInto(attributeTable)) {
            blobRecords.put(blobRecord.getAttributeId(), blobRecord);
        }

        Map<String, String> visibility = new LinkedHashMap<>();
        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
                if (attributeRecord.getVirtual()) {
                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
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
                        virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                    }
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
                } else {
                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, String> entry : visibility.entrySet()) {
            UserPrivacyRecord userPrivacyRecord = context.newRecord(userPrivacyTable);
            userPrivacyRecord.setUserPrivacyId(UUID.randomUUID().toString());
            userPrivacyRecord.setAttributeId(entry.getKey());
            userPrivacyRecord.setScope(entry.getValue());
            userPrivacyRecord.setUserId(userRecord.getUserId());
            userPrivacyRecord.store();
        }

        if (!virtualColumns.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnNames.add(entry.getKey() + " = " + JdbcFunction.columnCreate(entry.getValue()));
                }
            }
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("update " + Tables.USER.getName() + " set " + StringUtils.join(columnNames, ", ") + " where " + Tables.USER.USER_ID.getName() + " = " + userRecord.getUserId(), columnValues);
        }

        String tokenId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        SessionRecord sessionRecord = context.newRecord(sessionTable);
        sessionRecord.setSessionId(tokenId);
        sessionRecord.setDateCreated(dateCreated);
        sessionRecord.setUserId(userRecord.getUserId());
        sessionRecord.setDeleted(false);
        sessionRecord.store();

        responseBody.getData().setSession(tokenId);
        responseBody.getData().setDateCreated(dateCreated);
        responseBody.getData().setLogin(userRecord.getLogin());

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/login

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLoginResponse> login(
            HttpServletRequest request,
            @RequestBody SecurityLoginRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");

        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("username", "is required");
        }
        if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
            errorMessages.put("password", "is required");
        }

        List<Condition> where = new ArrayList<>();
        where.add(userTable.LOGIN.eq(requestBody.getUsername()));
        where.add(userTable.PASSWORD.eq(requestBody.getPassword()));

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(where).fetchOneInto(userTable);
        if (userRecord == null) {
            errorMessages.put("login", "bad credential");
        }

        if (!errorMessages.isEmpty()) {
            SecurityLoginResponse response = new SecurityLoginResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        SecurityLoginResponse responseBody = new SecurityLoginResponse();

        String sessionId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        SessionRecord sessionRecord = context.newRecord(sessionTable);
        sessionRecord.setSessionId(sessionId);
        sessionRecord.setDateCreated(dateCreated);
        sessionRecord.setUserId(userRecord.getUserId());
        sessionRecord.setDeleted(false);
        sessionRecord.store();

        responseBody.getData().setSession(sessionId);
        responseBody.getData().setDateCreated(dateCreated);
        responseBody.getData().setLogin(userRecord.getLogin());

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutResponse> logout(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody SecurityLogoutRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        SecurityLogoutResponse responseBody = new SecurityLogoutResponse();

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        String userId = sessionRecord.getUserId();

        context.delete(sessionTable).where(sessionTable.USER_ID.eq(userId)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout/{session}

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{session}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutSessionResponse> logoutSession(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @PathVariable("session") String session,
            @RequestBody Request requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=> body=>{}", request.getRequestURL(), appCode, request.getHeader("X-MBAAS-SESSION"), gson.toJson(requestBody));

        SecurityLogoutSessionResponse responseBody = new SecurityLogoutSessionResponse();

        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        context.delete(sessionTable).where(sessionTable.SESSION_ID.eq(session)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

}
