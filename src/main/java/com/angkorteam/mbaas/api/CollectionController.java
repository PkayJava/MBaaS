package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.FieldRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PrimaryRecord;
import com.angkorteam.mbaas.model.entity.tables.records.TableRecord;
import com.angkorteam.mbaas.request.Request;
import com.angkorteam.mbaas.response.Response;
import com.google.gson.Gson;
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
            method = RequestMethod.POST, path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> create(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        String name = StringUtils.lowerCase(collection);

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

        buffer.append("CREATE TABLE `" + name + "` (");
        buffer.append("`" + primaryName + "` INT(11) AUTO_INCREMENT, ");
        buffer.append("extra BLOB, ");
        buffer.append("PRIMARY KEY (`" + primaryName + "`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        tableRecord = context.newRecord(tableTable);
        tableRecord.setName(name);
        tableRecord.setSystem(false);
        tableRecord.store();

        {
            FieldRecord fieldRecord = context.newRecord(fieldTable);
            fieldRecord.setTableId(tableRecord.getTableId());
            fieldRecord.setName(primaryName);
            fieldRecord.setNullable(false);
            fieldRecord.setAutoIncrement(true);
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
            fieldRecord.setName("extra");
            fieldRecord.setNullable(true);
            fieldRecord.setAutoIncrement(false);
            fieldRecord.setVirtual(false);
            fieldRecord.setExposed(false);
            fieldRecord.setJavaType(Byte.class.getName() + "[]");
            fieldRecord.setSqlType("BLOB");
            fieldRecord.store();
        }

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> delete(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        String name = StringUtils.lowerCase(collection);

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
