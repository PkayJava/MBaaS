package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.Constants;
import com.angkorteam.mbaas.mariadb.JdbcFunction;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.FieldRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PrimaryRecord;
import com.angkorteam.mbaas.model.entity.tables.records.TableRecord;
import com.angkorteam.mbaas.request.*;
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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/12/2016.
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
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteAttribute(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CollectionAttributeDeleteRequest request
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        String collection = StringUtils.lowerCase(request.getCollection());
        String name = StringUtils.lowerCase(request.getName());

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(name)).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (fieldRecord == null || fieldRecord.getSystem()) {
            return null;
        }

        if (fieldRecord.getVirtual()) {
            FieldRecord virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.FIELD_ID.eq(fieldRecord.getVirtualFieldId())).fetchOneInto(fieldTable);
            jdbcTemplate.execute("UPDATE `" + collection + "`" + " SET " + virtualRecord.getName() + " = " + JdbcFunction.columnDelete(virtualRecord.getName(), name));
        } else {
            jdbcTemplate.execute("ALTER TABLE `" + collection + "` DROP COLUMN `" + name + "`");
        }
        fieldRecord.delete();

        return null;
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/attribute/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createAttribute(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CollectionAttributeCreateRequest request
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String collection = StringUtils.lowerCase(request.getCollection());
        String name = StringUtils.lowerCase(request.getName());

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(name)).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (fieldRecord != null) {
            return null;
        }

        FieldRecord virtualRecord = context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.NAME.eq(configuration.getString(Constants.JDBC_COLUMN_EXTRA))).and(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchOneInto(fieldTable);
        if (virtualRecord == null) {
            return null;
        }

        fieldRecord = context.newRecord(fieldTable);
        fieldRecord.setNullable(true);
        fieldRecord.setTableId(tableRecord.getTableId());
        fieldRecord.setVirtual(true);
        fieldRecord.setSystem(false);
        fieldRecord.setName(name);
        fieldRecord.setAutoIncrement(false);
        fieldRecord.setExposed(true);
        fieldRecord.setVirtualFieldId(virtualRecord.getFieldId());
        fieldRecord.setJavaType(request.getJavaType());
        if (request.getJavaType().equals(Integer.class.getName()) || request.getJavaType().equals(int.class.getName())
                || request.getJavaType().equals(Byte.class.getName()) || request.getJavaType().equals(byte.class.getName())
                || request.getJavaType().equals(Short.class.getName()) || request.getJavaType().equals(short.class.getName())
                || request.getJavaType().equals(Long.class.getName()) || request.getJavaType().equals(long.class.getName())
                ) {
            fieldRecord.setSqlType("INT");

        } else if (request.getJavaType().equals(Double.class.getName()) || request.getJavaType().equals(double.class.getName())
                || request.getJavaType().equals(Float.class.getName()) || request.getJavaType().equals(float.class.getName())) {
            fieldRecord.setSqlType("DECIMAL");
        } else if (request.getJavaType().equals(Boolean.class.getName()) || request.getJavaType().equals(boolean.class.getName())) {
            fieldRecord.setSqlType("BIT");
        } else if (request.getJavaType().equals(Date.class.getName()) || request.getJavaType().equals(Time.class.getName()) || request.getJavaType().equals(Timestamp.class.getName())) {
            fieldRecord.setSqlType("DATETIME");
        } else if (request.getJavaType().equals(Character.class.getName()) || request.getJavaType().equals(char.class.getName())
                || request.getJavaType().equals(String.class.getName())) {
            fieldRecord.setSqlType("VARCHAR");
        }
        fieldRecord.store();

        return null;
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> create(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CollectionCreateRequest request
    ) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        String name = StringUtils.lowerCase(request.getName());

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord != null) {
            return null;
        }

        String primaryName = name + "_id";

        List<String> systemFields = Arrays.asList(primaryName, configuration.getString(Constants.JDBC_COLUMN_DELETED), configuration.getString(Constants.JDBC_COLUMN_EXTRA), configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
        for (CollectionCreateRequest.Attribute attribute : request.getAttributes()) {
            if (systemFields.contains(attribute.getName())) {
                return null;
            }
            if (attribute.getJavaType() == null) {
                return null;
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
                return null;
            }
        }

        buffer.append("CREATE TABLE `" + name + "` (");
        buffer.append("`" + primaryName + "` INT(11) AUTO_INCREMENT, ");
        buffer.append("`extra` BLOB, ");
        buffer.append("`optimistic` INT(11) NOT NULL DEFAULT 0, ");
        for (CollectionCreateRequest.Attribute attribute : request.getAttributes()) {
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
        buffer.append("`delete` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("PRIMARY KEY (`" + primaryName + "`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        tableRecord = context.newRecord(tableTable);
        tableRecord.setName(name);
        tableRecord.setSystem(false);
        tableRecord.setLocked(true);
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

        for (CollectionCreateRequest.Attribute attribute : request.getAttributes()) {
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
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CollectionDeleteRequest requestBody
    ) {
        String name = StringUtils.lowerCase(requestBody.getName());

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(name)).fetchOneInto(tableTable);
        if (tableRecord == null || tableRecord.getSystem()) {
            return null;
        }

        context.delete(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(primaryTable).where(primaryTable.TABLE_ID.eq(tableRecord.getTableId())).execute();
        context.delete(tableTable).where(tableTable.TABLE_ID.eq(tableRecord.getTableId())).execute();

        jdbcTemplate.execute("DROP TABLE `" + name + "`");

        return ResponseEntity.ok(null);
    }
}
