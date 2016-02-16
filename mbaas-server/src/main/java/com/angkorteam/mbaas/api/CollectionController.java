package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.Constants;
import com.angkorteam.mbaas.enums.PermissionEnum;
import com.angkorteam.mbaas.factory.PermissionFactoryBean;
import com.angkorteam.mbaas.mariadb.JdbcFunction;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.request.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.request.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.request.CollectionCreateRequest;
import com.angkorteam.mbaas.request.CollectionDeleteRequest;
import com.angkorteam.mbaas.response.Response;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PermissionFactoryBean.Permission permission;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteAttribute(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionAttributeDeleteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        if (!permission.hasCollectionAccess(session, requestBody.getCollection(), PermissionEnum.Modify.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        String collection = StringUtils.lowerCase(requestBody.getCollection());
        String name = StringUtils.lowerCase(requestBody.getName());

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(name)).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (fieldRecord == null || fieldRecord.getSystem()) {
            return ResponseEntity.ok(null);
        }

        if (fieldRecord.getVirtual()) {
            FieldRecord virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.FIELD_ID.eq(fieldRecord.getVirtualFieldId())).fetchOneInto(fieldTable);
            jdbcTemplate.execute("UPDATE `" + collection + "`" + " SET " + virtualRecord.getName() + " = " + JdbcFunction.columnDelete(virtualRecord.getName(), name));
        } else {
            jdbcTemplate.execute("ALTER TABLE `" + collection + "` DROP COLUMN `" + name + "`");
        }
        fieldRecord.delete();

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createAttribute(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionAttributeCreateRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        if (!permission.hasCollectionAccess(session, requestBody.getCollection(), PermissionEnum.Modify.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String collection = StringUtils.lowerCase(requestBody.getCollection());
        String name = StringUtils.lowerCase(requestBody.getName());

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(name)).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (fieldRecord != null) {
            return ResponseEntity.ok(null);
        }

        FieldRecord virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(configuration.getString(Constants.JDBC_COLUMN_EXTRA))).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (virtualRecord == null) {
            return ResponseEntity.ok(null);
        }

        fieldRecord = context.newRecord(fieldTable);
        fieldRecord.setNullable(requestBody.isNullable());
        fieldRecord.setTableId(tableRecord.getTableId());
        fieldRecord.setVirtual(true);
        fieldRecord.setSystem(false);
        fieldRecord.setName(name);
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

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> create(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionCreateRequest requestBody
    ) throws SQLException {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        StringBuffer buffer = new StringBuffer();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        User userTable = Tables.USER.as("userTable");

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getName())).fetchOneInto(tableTable);
        if (tableRecord != null) {
            return ResponseEntity.ok(null);
        }

        String primaryName = requestBody.getName() + "_id";

        List<String> systemFields = Arrays.asList(primaryName, configuration.getString(Constants.JDBC_COLUMN_DELETED), configuration.getString(Constants.JDBC_COLUMN_EXTRA), configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (systemFields.contains(attribute.getName())) {
                return ResponseEntity.ok(null);
            }
            if (attribute.getJavaType() == null) {
                return ResponseEntity.ok(null);
            }
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    || attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())
                    || attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())
                    ) {
            } else {
                return ResponseEntity.ok(null);
            }
        }

        buffer.append("CREATE TABLE `" + requestBody.getName() + "` (");
        buffer.append("`" + primaryName + "` INT(11) AUTO_INCREMENT, ");
        buffer.append("`" + configuration.getString(Constants.JDBC_COLUMN_EXTRA) + "` BLOB, ");
        buffer.append("`" + configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC) + "` INT(11) NOT NULL DEFAULT 0, ");
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`" + attribute.getName() + "` INT(11), ");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                buffer.append("`" + attribute.getName() + "` DECIMAL(15,4), ");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                buffer.append("`" + attribute.getName() + "` BIT(1), ");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                buffer.append("`" + attribute.getName() + "` DATETIME, ");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                buffer.append("`" + attribute.getName() + "` VARCHAR(255), ");
            }
        }
        buffer.append("`" + configuration.getString(Constants.JDBC_OWNER_USER_ID) + "` INT(11) NOT NULL, ");
        buffer.append("`" + configuration.getString(Constants.JDBC_COLUMN_DELETED) + "` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("INDEX(`" + configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC) + "`), ");
        buffer.append("INDEX(`" + configuration.getString(Constants.JDBC_COLUMN_DELETED) + "`), ");
        buffer.append("INDEX(`" + configuration.getString(Constants.JDBC_OWNER_USER_ID) + "`), ");
        buffer.append("PRIMARY KEY (`" + primaryName + "`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        tableRecord = context.newRecord(tableTable);
        tableRecord.setName(requestBody.getName());
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
                buffer.append("`" + attribute.getName() + "` INT(11)");
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

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> delete(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody CollectionDeleteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        if (!permission.hasCollectionAccess(session, requestBody.getName(), PermissionEnum.Delete.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getName())).fetchOneInto(tableTable);
        if (tableRecord == null || tableRecord.getSystem()) {
            return ResponseEntity.ok(null);
        }

        context.delete(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(primaryTable).where(primaryTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(tableTable).where(tableTable.TABLE_ID.eq(tableRecord.getTableId())).execute();

        jdbcTemplate.execute("DROP TABLE `" + requestBody.getName() + "`");

        return ResponseEntity.ok(null);
    }
}
