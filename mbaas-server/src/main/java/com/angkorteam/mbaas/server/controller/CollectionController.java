package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.CollectionPermissionEnum;
import com.angkorteam.mbaas.plain.request.collection.*;
import com.angkorteam.mbaas.plain.response.collection.*;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Socheat KHAUV on 2/12/2016.
 */
@Controller
@RequestMapping(path = "/collection")
public class CollectionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PermissionFactoryBean.Permission permission;

    @Autowired
    private Gson gson;

    //region /collection/create

    @RequestMapping(
            method = RequestMethod.POST, path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionCreateResponse> create(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionCreateRequest requestBody
    ) throws SQLException {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        UserTable userTable = Tables.USER.as("userTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord != null) {
                errorMessages.put("collectionName", "is existed");
            }
        }

        if (permission.isAdministratorUser(session)
                || permission.isBackOfficeUser(session)) {
        } else {
            errorMessages.put("collectionName", "you are not allow to create new collection");
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getUserId())).fetchOneInto(userTable);

        String primaryName = requestBody.getCollectionName() + "_id";
        List<String> systemAttributes = Arrays.asList(primaryName, configuration.getString(Constants.JDBC_COLUMN_DELETED), configuration.getString(Constants.JDBC_COLUMN_EXTRA), configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));

        Pattern patternAttributeName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_ATTRIBUTE_NAME));

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (!patternAttributeName.matcher(attribute.getName()).matches()) {
                errorMessages.put(attribute.getName(), "bad name");
            } else {
                if (systemAttributes.contains(attribute.getName())) {
                    errorMessages.put(attribute.getName(), "overridden system field");
                }
                if (attribute.getJavaType() == null || "".equals(attribute.getJavaType())) {
                    errorMessages.put(attribute.getName(), "javaType is required");
                }
                if (!attribute.getJavaType().equals(AttributeTypeEnum.Boolean.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Byte.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Short.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Integer.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Long.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Float.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Double.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Character.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.String.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Time.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.Date.getLiteral())
                        && !attribute.getJavaType().equals(AttributeTypeEnum.DateTime.getLiteral())) {
                    errorMessages.put(attribute.getName(), "javaType is not support");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionCreateResponse response = new CollectionCreateResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        CollectionFunction.createCollection(context, jdbcTemplate, userRecord.getUserId(), requestBody);

        CollectionCreateResponse response = new CollectionCreateResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/attribute/create

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionAttributeCreateResponse> createAttribute(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionAttributeCreateRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        if (collectionRecord != null) {
            if (collectionRecord.getLocked() || collectionRecord.getSystem()) {
                errorMessages.put("collectionName", "you are not allow to create its attribute");
            } else {
                if (permission.isAdministratorUser(session)
                        || permission.isBackOfficeUser(session)
                        || permission.isCollectionOwner(session, requestBody.getCollectionName())
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), CollectionPermissionEnum.Attribute.getLiteral())
                        ) {
                } else {
                    errorMessages.put("collectionName", "you are not allow to create its attribute");
                }
            }
        }

        Pattern patternAttributeName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_ATTRIBUTE_NAME));

        AttributeRecord attributeRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else if (!patternAttributeName.matcher(requestBody.getAttributeName()).matches()) {
            errorMessages.put(requestBody.getAttributeName(), "bad name");
        } else {
            if (collectionRecord != null) {
                attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);
                if (attributeRecord != null) {
                    errorMessages.put("attributeName", "is existed");
                }
            }
        }

        if (requestBody.getJavaType() == null || "".equals(requestBody.getJavaType())) {
            errorMessages.put("javaType", "is required");
        } else {
            if (!requestBody.getJavaType().equals(AttributeTypeEnum.Integer.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Double.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Float.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Byte.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Short.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Long.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Boolean.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Character.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Date.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.Time.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.DateTime.getLiteral())
                    && !requestBody.getJavaType().equals(AttributeTypeEnum.String.getLiteral())) {
                errorMessages.put("javaType", "is not allow");
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        AttributeRecord virtualRecord = null;
        if (collectionRecord != null) {
            virtualRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(configuration.getString(Constants.JDBC_COLUMN_EXTRA))).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);
            if (virtualRecord == null) {
                errorMessages.put("collectionName", "does not support dynamic column");
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionAttributeCreateResponse response = new CollectionAttributeCreateResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        AttributeFunction.createAttribute(context, requestBody);

        CollectionAttributeCreateResponse response = new CollectionAttributeCreateResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());
        response.getData().setAttributeName(requestBody.getAttributeName());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/delete

    @RequestMapping(
            method = RequestMethod.POST, path = "/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionDeleteResponse> delete(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionDeleteRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        PrimaryTable primaryTable = Tables.PRIMARY.as("primaryTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("CollectionROlePrivacyTable");
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");
        IndexTable indexTable = Tables.INDEX.as("indexTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        if (collectionRecord != null) {
            if (collectionRecord.getLocked() || collectionRecord.getSystem()) {
                errorMessages.put("collectionName", "you are not allow to create its attribute");
            } else {
                if (permission.isAdministratorUser(session)
                        || permission.isBackOfficeUser(session)
                        || permission.isCollectionOwner(session, requestBody.getCollectionName())
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), CollectionPermissionEnum.Drop.getLiteral())
                        ) {
                } else {
                    errorMessages.put("collectionName", "you are not allow to delete it");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionDeleteResponse response = new CollectionDeleteResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        CollectionFunction.deleteCollection(context, jdbcTemplate, requestBody);

        CollectionDeleteResponse response = new CollectionDeleteResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/attribute/delete

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionAttributeDeleteResponse> deleteAttribute(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionAttributeDeleteRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = null;

        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        Pattern patternAttributeName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_ATTRIBUTE_NAME));

        AttributeRecord attributeRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else if (!patternAttributeName.matcher(requestBody.getAttributeName()).matches()) {
            errorMessages.put(requestBody.getAttributeName(), "bad name");
        } else {
            if (collectionRecord != null) {
                attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);
                if (attributeRecord == null) {
                    errorMessages.put("attributeName", "is not found");
                } else {
                    if (attributeRecord.getSystem()) {
                        errorMessages.put("attributeName", "you are not allow to delete this attribute");
                    }
                }
            }
        }

        if (collectionRecord != null) {
            if (collectionRecord.getLocked() || collectionRecord.getSystem()) {
                errorMessages.put("collectionName", "you are not allow to create its attribute");
            } else {
                if (permission.isAdministratorUser(session)
                        || permission.isBackOfficeUser(session)
                        || permission.isCollectionOwner(session, requestBody.getCollectionName())
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), CollectionPermissionEnum.Attribute.getLiteral())
                        ) {
                } else {
                    errorMessages.put("collectionName", "you are not allow to delete its attribute");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionAttributeDeleteResponse response = new CollectionAttributeDeleteResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        AttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);

        CollectionAttributeDeleteResponse response = new CollectionAttributeDeleteResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());
        response.getData().setAttributeName(requestBody.getAttributeName());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/permission/grant/username

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionPermissionUsernameResponse> grantPermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        UserTable userTable = Tables.USER.as("userTable");
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("collectionUserPrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody)) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        UserRecord userRecord = null;
        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("username", "is required");
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("username", "is not found");
            }
        }

        if (requestBody.getActions() == null || requestBody.getActions().isEmpty()) {
            errorMessages.put("actions", "is required");
        } else {
            for (Integer action : requestBody.getActions()) {
                if (action != CollectionPermissionEnum.Attribute.getLiteral()
                        && action != CollectionPermissionEnum.Read.getLiteral()
                        && action != CollectionPermissionEnum.Drop.getLiteral()
                        && action != CollectionPermissionEnum.Insert.getLiteral()) {
                    errorMessages.put("actions", "is bad");
                    break;
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionPermissionUsernameResponse response = new CollectionPermissionUsernameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        context.delete(collectionUserPrivacyTable)
                .where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(collectionUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                .execute();

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }

        CollectionUserPrivacyRecord collectionUserPrivacyRecord = context.newRecord(collectionUserPrivacyTable);
        collectionUserPrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
        collectionUserPrivacyRecord.setUserId(userRecord.getUserId());
        collectionUserPrivacyRecord.setPermisson(permission);
        collectionUserPrivacyRecord.store();

        CollectionPermissionUsernameResponse response = new CollectionPermissionUsernameResponse();
        response.getData().setPermission(permission);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/permission/grant/rolename

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/rolename",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionPermissionRoleNameResponse> grantPermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody)) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        RoleRecord roleRecord = null;
        if (requestBody.getRoleName() == null || "".equals(requestBody.getRoleName())) {
            errorMessages.put("roleName", "is required");
        } else {
            roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(requestBody.getRoleName())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                errorMessages.put("roleName", "is not found");
            }
        }

        if (requestBody.getActions() == null || requestBody.getActions().isEmpty()) {
            errorMessages.put("actions", "is required");
        } else {
            for (Integer action : requestBody.getActions()) {
                if (action != CollectionPermissionEnum.Attribute.getLiteral()
                        && action != CollectionPermissionEnum.Read.getLiteral()
                        && action != CollectionPermissionEnum.Drop.getLiteral()
                        && action != CollectionPermissionEnum.Insert.getLiteral()) {
                    errorMessages.put("actions", "is bad");
                    break;
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionPermissionRoleNameResponse response = new CollectionPermissionRoleNameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        context.delete(collectionRolePrivacyTable)
                .where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(collectionRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                .execute();

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }

        CollectionRolePrivacyRecord collectionRolePrivacyRecord = context.newRecord(collectionRolePrivacyTable);
        collectionRolePrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
        collectionRolePrivacyRecord.setRoleId(roleRecord.getRoleId());
        collectionRolePrivacyRecord.setPermisson(permission);
        collectionRolePrivacyRecord.store();

        CollectionPermissionRoleNameResponse response = new CollectionPermissionRoleNameResponse();
        response.getData().setPermission(permission);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/permission/revoke/username

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionPermissionUsernameResponse> revokePermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        UserTable userTable = Tables.USER.as("userTable");
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("collectionUserPrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody)) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        UserRecord userRecord = null;
        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("username", "is required");
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("username", "is not found");
            }
        }

        if (requestBody.getActions() == null || requestBody.getActions().isEmpty()) {
            errorMessages.put("actions", "is required");
        } else {
            for (Integer action : requestBody.getActions()) {
                if (action != CollectionPermissionEnum.Attribute.getLiteral()
                        && action != CollectionPermissionEnum.Read.getLiteral()
                        && action != CollectionPermissionEnum.Drop.getLiteral()
                        && action != CollectionPermissionEnum.Insert.getLiteral()) {
                    errorMessages.put("actions", "is bad");
                    break;
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionPermissionUsernameResponse response = new CollectionPermissionUsernameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        CollectionUserPrivacyRecord collectionUserPrivacyRecord = context.select(collectionUserPrivacyTable.fields())
                .from(collectionUserPrivacyTable)
                .where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(collectionUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                .fetchOneInto(collectionUserPrivacyTable);

        int revokePermission = 0;
        for (Integer action : requestBody.getActions()) {
            revokePermission = revokePermission | action;
        }

        if (collectionUserPrivacyRecord != null) {
            int permission = collectionUserPrivacyRecord.getPermisson();
            if ((permission & revokePermission) == revokePermission) {
                collectionUserPrivacyRecord.setPermisson(permission - revokePermission);
            }
            collectionUserPrivacyRecord.update();
        } else {
            collectionUserPrivacyRecord = context.newRecord(collectionUserPrivacyTable);
            collectionUserPrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
            collectionUserPrivacyRecord.setUserId(userRecord.getUserId());
            collectionUserPrivacyRecord.setPermisson(0);
            collectionUserPrivacyRecord.store();
        }


        CollectionPermissionUsernameResponse response = new CollectionPermissionUsernameResponse();
        response.getData().setPermission(collectionUserPrivacyRecord.getPermisson());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /collection/permission/revoke/rolename

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/rolename",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionPermissionRoleNameResponse> revokePermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "client_id", required = false) String clientId,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody CollectionPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} client_id=>{} session=>{} body=>{}", request.getRequestURL(), clientId, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody)) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        RoleRecord roleRecord = null;
        if (requestBody.getRoleName() == null || "".equals(requestBody.getRoleName())) {
            errorMessages.put("roleName", "is required");
        } else {
            roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(requestBody.getRoleName())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                errorMessages.put("roleName", "is not found");
            }
        }

        if (requestBody.getActions() == null || requestBody.getActions().isEmpty()) {
            errorMessages.put("actions", "is required");
        } else {
            for (Integer action : requestBody.getActions()) {
                if (action != CollectionPermissionEnum.Attribute.getLiteral()
                        && action != CollectionPermissionEnum.Read.getLiteral()
                        && action != CollectionPermissionEnum.Insert.getLiteral()
                        && action != CollectionPermissionEnum.Drop.getLiteral()) {
                    errorMessages.put("actions", "is bad");
                    break;
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionPermissionRoleNameResponse response = new CollectionPermissionRoleNameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        CollectionRolePrivacyRecord collectionRolePrivacyRecord = context.select(collectionRolePrivacyTable.fields())
                .from(collectionRolePrivacyTable)
                .where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(collectionRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                .fetchOneInto(collectionRolePrivacyTable);

        int revokePermission = 0;
        for (Integer action : requestBody.getActions()) {
            revokePermission = revokePermission | action;
        }

        if (collectionRolePrivacyRecord != null) {
            int permission = collectionRolePrivacyRecord.getPermisson();
            if ((permission & revokePermission) == revokePermission) {
                collectionRolePrivacyRecord.setPermisson(permission - revokePermission);
            }
            collectionRolePrivacyRecord.update();
        } else {
            collectionRolePrivacyRecord = context.newRecord(collectionRolePrivacyTable);
            collectionRolePrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
            collectionRolePrivacyRecord.setRoleId(roleRecord.getRoleId());
            collectionRolePrivacyRecord.setPermisson(0);
            collectionRolePrivacyRecord.store();
        }

        CollectionPermissionRoleNameResponse response = new CollectionPermissionRoleNameResponse();
        response.getData().setPermission(collectionRolePrivacyRecord.getPermisson());

        return ResponseEntity.ok(response);
    }

    //endregion

}
