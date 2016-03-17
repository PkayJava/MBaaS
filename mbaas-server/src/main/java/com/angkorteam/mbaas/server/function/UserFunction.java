package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.jooq.enums.UserStatusEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by socheat on 3/16/16.
 */
public class UserFunction {

    public static String createUser(DSLContext context, JdbcTemplate jdbcTemplate, HttpServletRequest request, SecuritySignUpRequest requestBody) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        UserTable userTable = Tables.USER.as("userTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);

        String login = requestBody.getUsername();
        String password = requestBody.getPassword();

        String userId = UUID.randomUUID().toString();

        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setUserId(userId);
        userRecord.setDeleted(false);
        userRecord.setRoleId(roleRecord.getRoleId());
        userRecord.setAccountNonExpired(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setStatus(UserStatusEnum.Active.getLiteral());
        userRecord.setLogin(login);
        userRecord.setPassword(password);
        userRecord.store();

        // select all attribute put into map and type enum into map
        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
        Map<String, TypeEnum> typeEnums = new LinkedHashMap<>();
        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
            typeEnums.put(attributeRecord.getName(), TypeEnum.valueOf(attributeRecord.getJavaType()));
        }

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.putAll(requestBody.getVisibleByAnonymousUsers());
        fields.putAll(requestBody.getVisibleByFriends());
        fields.putAll(requestBody.getVisibleByRegisteredUsers());
        fields.putAll(requestBody.getVisibleByTheUser());

        boolean dirtyAttribute = false;

        // Create The Attribute Which Are Not Yet Have.
        if (!fields.isEmpty()) {
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (attributeRecords.get(entry.getKey()) == null) {
                    CollectionAttributeCreateRequest req = new CollectionAttributeCreateRequest();
                    req.setAttributeName(entry.getKey());
                    req.setJavaType(TypeEnum.parse(entry.getValue()).getLiteral());
                    req.setNullable(true);
                    if (requestBody.getVisibleByAnonymousUsers().containsKey(entry.getKey())) {
                        UserAttributeFunction.createAttribute(context, req, userRecord.getUserId(), ScopeEnum.VisibleByAnonymousUser);
                        dirtyAttribute = true;
                    }
                    if (requestBody.getVisibleByFriends().containsKey(entry.getKey())) {
                        UserAttributeFunction.createAttribute(context, req, userRecord.getUserId(), ScopeEnum.VisibleByFriend);
                        dirtyAttribute = true;
                    }
                    if (requestBody.getVisibleByRegisteredUsers().containsKey(entry.getKey())) {
                        UserAttributeFunction.createAttribute(context, req, userRecord.getUserId(), ScopeEnum.VisibleByRegisteredUser);
                        dirtyAttribute = true;
                    }
                    if (requestBody.getVisibleByTheUser().containsKey(entry.getKey())) {
                        UserAttributeFunction.createAttribute(context, req, userRecord.getUserId(), ScopeEnum.VisibleByTheUser);
                        dirtyAttribute = true;
                    }
                }
            }
        }

        if (dirtyAttribute) {
            // select all attribute put into map and type enum into map
            attributeRecords = new LinkedHashMap<>();
            typeEnums = new LinkedHashMap<>();
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
                attributeRecords.put(attributeRecord.getName(), attributeRecord);
                typeEnums.put(attributeRecord.getName(), TypeEnum.valueOf(attributeRecord.getJavaType()));
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

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();
        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();

        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
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

        if (!virtualColumns.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnNames.add(entry.getKey() + " = " + MariaDBFunction.columnCreate(entry.getValue(), typeEnums));
                }
            }
            columnValues.put(Tables.USER.USER_ID.getName(), userRecord.getUserId());
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("update " + Tables.USER.getName() + " set " + StringUtils.join(columnNames, ", ") + " where " + Tables.USER.USER_ID.getName() + " = :" + Tables.USER.USER_ID.getName(), columnValues);
        }

        MobileTable mobileTable = Tables.MOBILE.as("desktopTable");

        String mobileId = UUID.randomUUID().toString();
        Date dateCreated = new Date();

        MobileRecord mobileRecord = context.newRecord(mobileTable);
        mobileRecord.setMobileId(mobileId);
        mobileRecord.setDateCreated(dateCreated);
        mobileRecord.setUserId(userRecord.getUserId());
        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        mobileRecord.setClientIp(request.getRemoteAddr());
        mobileRecord.store();

        return userId;
    }

}
