package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.plain.request.CollectionCreateRequest;
import com.angkorteam.mbaas.plain.request.CollectionDeleteRequest;
import com.angkorteam.mbaas.plain.response.*;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
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

/**
 * Created by Socheat KHAUV on 2/12/2016.
 */
@Controller
@RequestMapping("/collection")
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

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = null;

        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(tableTable);
            if (tableRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        FieldRecord fieldRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else {
            if (tableRecord != null) {
                fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(requestBody.getAttributeName())).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
                if (fieldRecord == null) {
                    errorMessages.put("attributeName", "is not found");
                }
            }
        }

        if (tableRecord != null) {
            if (!permission.hasCollectionAccess(session, requestBody.getCollectionName(), PermissionEnum.Modify.getLiteral())) {
                errorMessages.put("collectionName", "you are not allow to delete its attribute");
            }
        }

        if (!errorMessages.isEmpty()) {
            CollectionAttributeDeleteResponse response = new CollectionAttributeDeleteResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        if (fieldRecord.getVirtual()) {
            FieldRecord virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.FIELD_ID.eq(fieldRecord.getVirtualFieldId())).fetchOneInto(fieldTable);
            jdbcTemplate.execute("UPDATE `" + requestBody.getCollectionName() + "`" + " SET " + virtualRecord.getName() + " = " + JdbcFunction.columnDelete(virtualRecord.getName(), requestBody.getAttributeName()));
        } else {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        fieldRecord.delete();

        CollectionAttributeDeleteResponse response = new CollectionAttributeDeleteResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());
        response.getData().setAttributeName(requestBody.getAttributeName());

        return ResponseEntity.ok(response);
    }

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

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(tableTable);
            if (tableRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        if (tableRecord != null) {
            if (!permission.hasCollectionAccess(session, requestBody.getCollectionName(), PermissionEnum.Modify.getLiteral())) {
                errorMessages.put("collectionName", "you are not allow to create its attribute");
            }
        }

        FieldRecord fieldRecord = null;
        if (requestBody.getAttributeName() == null || "".equals(requestBody.getAttributeName())) {
            errorMessages.put("attributeName", "is required");
        } else {
            if (tableRecord != null) {
                fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(requestBody.getAttributeName())).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
                if (fieldRecord != null) {
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

        FieldRecord virtualRecord = null;
        if (tableRecord != null) {
            virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(configuration.getString(Constants.JDBC_COLUMN_EXTRA))).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
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

        fieldRecord = context.newRecord(fieldTable);
        fieldRecord.setNullable(requestBody.isNullable());
        fieldRecord.setTableId(tableRecord.getTableId());
        fieldRecord.setVirtual(true);
        fieldRecord.setSystem(false);
        fieldRecord.setName(requestBody.getAttributeName());
        fieldRecord.setAutoIncrement(false);
        fieldRecord.setExposed(true);
        fieldRecord.setVirtualFieldId(virtualRecord.getFieldId());
        fieldRecord.setJavaType(requestBody.getJavaType());
        if (requestBody.getJavaType().equals(Integer.class.getName()) || requestBody.getJavaType().equals(int.class.getName())
                || requestBody.getJavaType().equals(Byte.class.getName()) || requestBody.getJavaType().equals(byte.class.getName())
                || requestBody.getJavaType().equals(Short.class.getName()) || requestBody.getJavaType().equals(short.class.getName())
                || requestBody.getJavaType().equals(Long.class.getName()) || requestBody.getJavaType().equals(long.class.getName())
                ) {
            fieldRecord.setSqlType("INT");

        } else if (requestBody.getJavaType().equals(Double.class.getName()) || requestBody.getJavaType().equals(double.class.getName())
                || requestBody.getJavaType().equals(Float.class.getName()) || requestBody.getJavaType().equals(float.class.getName())) {
            fieldRecord.setSqlType("DECIMAL");
        } else if (requestBody.getJavaType().equals(Boolean.class.getName()) || requestBody.getJavaType().equals(boolean.class.getName())) {
            fieldRecord.setSqlType("BIT");
        } else if (requestBody.getJavaType().equals(Date.class.getName()) || requestBody.getJavaType().equals(Time.class.getName()) || requestBody.getJavaType().equals(Timestamp.class.getName())) {
            fieldRecord.setSqlType("DATETIME");
        } else if (requestBody.getJavaType().equals(Character.class.getName()) || requestBody.getJavaType().equals(char.class.getName())
                || requestBody.getJavaType().equals(String.class.getName())) {
            fieldRecord.setSqlType("VARCHAR");
        }
        fieldRecord.store();

        CollectionAttributeCreateResponse response = new CollectionAttributeCreateResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());
        response.getData().setAttributeName(requestBody.getAttributeName());

        return ResponseEntity.ok(response);
    }

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

        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        User userTable = Tables.USER.as("userTable");

        TableRecord tableRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(tableTable);
            if (tableRecord != null) {
                errorMessages.put("collectionName", "is existed");
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);

        String primaryName = requestBody.getCollectionName() + "_id";
        List<String> systemFields = Arrays.asList(primaryName, configuration.getString(Constants.JDBC_COLUMN_DELETED), configuration.getString(Constants.JDBC_COLUMN_EXTRA), configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (systemFields.contains(attribute.getName())) {
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

        if (!errorMessages.isEmpty()) {
            CollectionCreateResponse response = new CollectionCreateResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` INT(11) AUTO_INCREMENT, ");
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
        buffer.append("`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("` INT(11) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("`), ");
        buffer.append("PRIMARY KEY (`").append(primaryName).append("`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        tableRecord = context.newRecord(tableTable);
        tableRecord.setName(requestBody.getCollectionName());
        tableRecord.setSystem(false);
        tableRecord.setLocked(true);
        tableRecord.setOwnerUserId(userRecord.getUserId());
        tableRecord.store();

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(primaryName);
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(true);
            fieldRecord.setSystem(true);
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
            fieldRecord.setName(configuration.getString(Constants.JDBC_COLUMN_EXTRA));
            fieldRecord.setNullable(true);
            fieldRecord.setSystem(true);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Byte.class.getName() + "[]");
            fieldRecord.setSqlType("BLOB");
            fieldRecord.store();
        }

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setSystem(true);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Integer.class.getName());
            fieldRecord.setSqlType("INT");
            fieldRecord.store();
        }

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DELETED));
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setSystem(true);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Boolean.class.getName());
            fieldRecord.setSqlType("BIT");
            fieldRecord.store();
        }
        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(configuration.getString(Constants.JDBC_OWNER_USER_ID));
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setSystem(true);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Integer.class.getName());
            fieldRecord.setSqlType("INT");
            fieldRecord.store();
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(attribute.getName());
            fieldRecord.setNullable(true);
            fieldRecord.setSystem(false);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setExposed(true);
            fieldRecord.setJavaType(attribute.getJavaType());
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11)");
                fieldRecord.setSqlType("INT");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                fieldRecord.setSqlType("DECIMAL");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                fieldRecord.setSqlType("BIT");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                fieldRecord.setSqlType("DATETIME");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                fieldRecord.setSqlType("VARCHAR");
            }
            fieldRecord.store();
        }

        tableRecord.setLocked(false);
        tableRecord.update();

        CollectionCreateResponse response = new CollectionCreateResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());

        return ResponseEntity.ok(response);
    }

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

        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(tableTable);
            if (tableRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        if (tableRecord != null) {
            if (tableRecord.getSystem()) {
                errorMessages.put("collectionName", "you are not allow to delete system collection");
            } else {
                if (!permission.hasCollectionAccess(session, requestBody.getCollectionName(), PermissionEnum.Modify.getLiteral())) {
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

        context.delete(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(primaryTable).where(primaryTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(tableTable).where(tableTable.TABLE_ID.eq(tableRecord.getTableId())).execute();

        jdbcTemplate.execute("DROP TABLE `" + requestBody.getCollectionName() + "`");

        CollectionDeleteResponse response = new CollectionDeleteResponse();
        response.getData().setCollectionName(requestBody.getCollectionName());

        return ResponseEntity.ok(response);
    }
}
