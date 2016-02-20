package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.*;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Socheat KHAUV on 2/12/2016.
 */
@Controller
@RequestMapping(path = "/collection")
public class CollectionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);

    private static final Pattern PATTERN_NAMING = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_NAMING));

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionCreateRequest requestBody
    ) throws SQLException {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        PrimaryTable primaryTable = Tables.PRIMARY.as("primaryTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
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

        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);

        String primaryName = requestBody.getCollectionName() + "_id";
        List<String> systemAttributes = Arrays.asList(primaryName, configuration.getString(Constants.JDBC_COLUMN_DELETED), configuration.getString(Constants.JDBC_COLUMN_EXTRA), configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (!PATTERN_NAMING.matcher(attribute.getName()).matches()) {
                errorMessages.put(attribute.getName(), "bad name");
            } else {
                if (systemAttributes.contains(attribute.getName())) {
                    errorMessages.put(attribute.getName(), "overridden system field");
                }
                if (attribute.getJavaType() == null || "".equals(attribute.getJavaType())) {
                    errorMessages.put(attribute.getName(), "javaType is required");
                }
                if (!attribute.getJavaType().equals(Integer.class.getName()) && !attribute.getJavaType().equals(int.class.getName())
                        && !attribute.getJavaType().equals(Double.class.getName()) && !attribute.getJavaType().equals(double.class.getName())
                        && !attribute.getJavaType().equals(Float.class.getName()) && !attribute.getJavaType().equals(float.class.getName())
                        && !attribute.getJavaType().equals(Byte.class.getName()) && !attribute.getJavaType().equals(byte.class.getName())
                        && !attribute.getJavaType().equals(Short.class.getName()) && !attribute.getJavaType().equals(short.class.getName())
                        && !attribute.getJavaType().equals(Long.class.getName()) && !attribute.getJavaType().equals(long.class.getName())
                        && !attribute.getJavaType().equals(Boolean.class.getName()) && !attribute.getJavaType().equals(boolean.class.getName())
                        && !attribute.getJavaType().equals(Character.class.getName()) && !attribute.getJavaType().equals(char.class.getName())
                        && !attribute.getJavaType().equals(Date.class.getName()) && !attribute.getJavaType().equals(Time.class.getName()) && !attribute.getJavaType().equals(Timestamp.class.getName())
                        && !attribute.getJavaType().equals(String.class.getName())) {
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

        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_EXTRA)).append("` BLOB, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("` INT(11) NOT NULL DEFAULT 0, ");
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11), ");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` DECIMAL(15,4), ");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` BIT(1), ");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` DATETIME, ");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` VARCHAR(255), ");
            }
        }
        buffer.append("`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("`), ");
        buffer.append("PRIMARY KEY (`").append(primaryName).append("`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        collectionRecord = context.newRecord(collectionTable);
        collectionRecord.setCollectionId(UUID.randomUUID().toString());
        collectionRecord.setName(requestBody.getCollectionName());
        collectionRecord.setSystem(false);
        collectionRecord.setLocked(true);
        collectionRecord.setOwnerUserId(userRecord.getUserId());
        collectionRecord.store();

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(primaryName);
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(true);
            attributeRecord.setSystem(true);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(String.class.getName());
            attributeRecord.setSqlType("VARCHAR");
            attributeRecord.store();

            PrimaryRecord primaryRecord = context.newRecord(primaryTable);
            primaryRecord.setAttributeId(attributeRecord.getAttributeId());
            primaryRecord.setCollectionId(collectionRecord.getCollectionId());
            primaryRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_EXTRA));
            attributeRecord.setNullable(true);
            attributeRecord.setSystem(true);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Byte.class.getName() + "[]");
            attributeRecord.setSqlType("BLOB");
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Integer.class.getName());
            attributeRecord.setSqlType("INT");
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DELETED));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Boolean.class.getName());
            attributeRecord.setSqlType("BIT");
            attributeRecord.store();
        }
        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_OWNER_USER_ID));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(String.class.getName());
            attributeRecord.setSqlType("VARCHAR");
            attributeRecord.store();
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(attribute.getName());
            attributeRecord.setNullable(attribute.isNullable());
            attributeRecord.setSystem(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(attribute.getJavaType());
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11)");
                attributeRecord.setSqlType("INT");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                attributeRecord.setSqlType("DECIMAL");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                attributeRecord.setSqlType("BIT");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                attributeRecord.setSqlType("DATETIME");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                attributeRecord.setSqlType("VARCHAR");
            }
            attributeRecord.store();
        }

        collectionRecord.setLocked(false);
        collectionRecord.update();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionAttributeCreateRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), PermissionEnum.Modify.getLiteral())
                        ) {
                } else {
                    errorMessages.put("collectionName", "you are not allow to create its attribute");
                }
            }
        }

        AttributeRecord attributeRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else if (!PATTERN_NAMING.matcher(requestBody.getAttributeName()).matches()) {
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
            if (!requestBody.getJavaType().equals(Integer.class.getName()) && !requestBody.getJavaType().equals(int.class.getName())
                    && !requestBody.getJavaType().equals(Double.class.getName()) && !requestBody.getJavaType().equals(double.class.getName())
                    && !requestBody.getJavaType().equals(Float.class.getName()) && !requestBody.getJavaType().equals(float.class.getName())
                    && !requestBody.getJavaType().equals(Byte.class.getName()) && !requestBody.getJavaType().equals(byte.class.getName())
                    && !requestBody.getJavaType().equals(Short.class.getName()) && !requestBody.getJavaType().equals(short.class.getName())
                    && !requestBody.getJavaType().equals(Long.class.getName()) && !requestBody.getJavaType().equals(long.class.getName())
                    && !requestBody.getJavaType().equals(Boolean.class.getName()) && !requestBody.getJavaType().equals(boolean.class.getName())
                    && !requestBody.getJavaType().equals(Character.class.getName()) && !requestBody.getJavaType().equals(char.class.getName())
                    && !requestBody.getJavaType().equals(Date.class.getName()) && !requestBody.getJavaType().equals(Time.class.getName()) && !requestBody.getJavaType().equals(Timestamp.class.getName())
                    && !requestBody.getJavaType().equals(String.class.getName())) {
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

        attributeRecord = context.newRecord(attributeTable);
        attributeRecord.setAttributeId(UUID.randomUUID().toString());
        attributeRecord.setNullable(requestBody.isNullable());
        attributeRecord.setCollectionId(collectionRecord.getCollectionId());
        attributeRecord.setVirtual(true);
        attributeRecord.setSystem(false);
        attributeRecord.setName(requestBody.getAttributeName());
        attributeRecord.setAutoIncrement(false);
        attributeRecord.setExposed(true);
        attributeRecord.setVirtualAttributeId(virtualRecord.getAttributeId());
        attributeRecord.setJavaType(requestBody.getJavaType());
        if (requestBody.getJavaType().equals(Integer.class.getName()) || requestBody.getJavaType().equals(int.class.getName())
                || requestBody.getJavaType().equals(Byte.class.getName()) || requestBody.getJavaType().equals(byte.class.getName())
                || requestBody.getJavaType().equals(Short.class.getName()) || requestBody.getJavaType().equals(short.class.getName())
                || requestBody.getJavaType().equals(Long.class.getName()) || requestBody.getJavaType().equals(long.class.getName())
                ) {
            attributeRecord.setSqlType("INT");

        } else if (requestBody.getJavaType().equals(Double.class.getName()) || requestBody.getJavaType().equals(double.class.getName())
                || requestBody.getJavaType().equals(Float.class.getName()) || requestBody.getJavaType().equals(float.class.getName())) {
            attributeRecord.setSqlType("DECIMAL");
        } else if (requestBody.getJavaType().equals(Boolean.class.getName()) || requestBody.getJavaType().equals(boolean.class.getName())) {
            attributeRecord.setSqlType("BIT");
        } else if (requestBody.getJavaType().equals(Date.class.getName()) || requestBody.getJavaType().equals(Time.class.getName()) || requestBody.getJavaType().equals(Timestamp.class.getName())) {
            attributeRecord.setSqlType("DATETIME");
        } else if (requestBody.getJavaType().equals(Character.class.getName()) || requestBody.getJavaType().equals(char.class.getName())
                || requestBody.getJavaType().equals(String.class.getName())) {
            attributeRecord.setSqlType("VARCHAR");
        }
        attributeRecord.store();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionDeleteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), PermissionEnum.Delete.getLiteral())
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

        context.delete(attributeTables).where(attributeTables.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(primaryTable).where(primaryTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(indexTable).where(indexTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(collectionUserPrivacyTable).where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(collectionRolePrivacyTable).where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(documentUserPrivacyTable).where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(documentRolePrivacyTable).where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
        context.delete(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();

        jdbcTemplate.execute("DROP TABLE `" + requestBody.getCollectionName() + "`");

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionAttributeDeleteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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

        AttributeRecord attributeRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else if (!PATTERN_NAMING.matcher(requestBody.getAttributeName()).matches()) {
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
                        || permission.hasCollectionPermission(session, requestBody.getCollectionName(), PermissionEnum.Modify.getLiteral())
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

        if (attributeRecord.getVirtual()) {
            AttributeRecord virtualRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeRecord.getVirtualAttributeId())).fetchOneInto(attributeTable);
            jdbcTemplate.execute("UPDATE `" + requestBody.getCollectionName() + "`" + " SET " + virtualRecord.getName() + " = " + JdbcFunction.columnDelete(virtualRecord.getName(), requestBody.getAttributeName()));
        } else {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        attributeRecord.delete();

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                if (action != PermissionEnum.Delete.getLiteral()
                        && action != PermissionEnum.Read.getLiteral()
                        && action != PermissionEnum.Create.getLiteral()
                        && action != PermissionEnum.Modify.getLiteral()) {
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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                if (action != PermissionEnum.Delete.getLiteral()
                        && action != PermissionEnum.Read.getLiteral()
                        && action != PermissionEnum.Create.getLiteral()
                        && action != PermissionEnum.Modify.getLiteral()) {
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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                if (action != PermissionEnum.Delete.getLiteral()
                        && action != PermissionEnum.Read.getLiteral()
                        && action != PermissionEnum.Create.getLiteral()
                        && action != PermissionEnum.Modify.getLiteral()) {
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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
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
                if (action != PermissionEnum.Delete.getLiteral()
                        && action != PermissionEnum.Read.getLiteral()
                        && action != PermissionEnum.Create.getLiteral()
                        && action != PermissionEnum.Modify.getLiteral()) {
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
