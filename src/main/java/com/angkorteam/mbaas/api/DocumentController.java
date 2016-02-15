package com.angkorteam.mbaas.api;

import com.angkorteam.mbaas.mariadb.JdbcFunction;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.FieldRecord;
import com.angkorteam.mbaas.model.entity.tables.records.TableRecord;
import com.angkorteam.mbaas.request.*;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
@Controller
@RequestMapping("/document")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

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
            method = RequestMethod.POST, path = "/query/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> query(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentQueryRequest requestBody
    ) {
        LOGGER.info("/document/query/{} appCode=>{} session=>{} body=>{}", collection, appCode, session, gson.toJson(requestBody));
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<Integer, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getFieldId(), fieldRecord);
        }

        List<String> fields = new LinkedList<>();
        for (Map.Entry<Integer, FieldRecord> entry : fieldRecords.entrySet()) {
            FieldRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    FieldRecord virtualRecord = fieldRecords.get(fieldRecord.getVirtualFieldId());
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName()) + " as " + fieldRecord.getName());
                } else {
                    fields.add("`" + entry.getKey() + "`");
                }
            }
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT " + StringUtils.join(fields, ", ") + " from " + collection + " limit 0,10");

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/query/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> queryById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentQueryRequestById requestBody
    ) {
        LOGGER.info("/document/query/{}/{} appCode=>{} session=>{} body=>{}", collection, id, appCode, session, gson.toJson(requestBody));
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<Integer, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getFieldId(), fieldRecord);
        }

        List<String> fields = new LinkedList<>();
        for (Map.Entry<Integer, FieldRecord> entry : fieldRecords.entrySet()) {
            FieldRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    FieldRecord virtualRecord = fieldRecords.get(fieldRecord.getVirtualFieldId());
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName()) + " as " + fieldRecord.getName());
                } else {
                    fields.add("`" + entry.getKey() + "`");
                }
            }
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT " + StringUtils.join(fields, ", ") + " from " + collection + " where " + collection + "_id = ?", id);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/create/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> create(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentCreateRequest requestBody
    ) {
        LOGGER.info("/document/create/{} appCode=>{} session=>{} body=>{}", collection, appCode, session, gson.toJson(requestBody));
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
        }

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (!fieldRecords.containsKey(entry.getKey())) {
                return null;
            }
        }

        Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
        for (FieldRecord blobRecord : context.select(fieldTable.fields()).from(fieldTable)
                .where(fieldTable.SQL_TYPE.eq("BLOB"))
                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.VIRTUAL.eq(false))
                .fetchInto(fieldTable)) {
            blobRecords.put(blobRecord.getFieldId(), blobRecord);
        }

        Map<String, Map<String, Serializable>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
            if (fieldRecord.getNullable()) {
                if (fieldRecord.getJavaType().equals(String.class.getName())) {
                    if (entry.getValue() == null) {
                        return null;
                    }
                } else {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        return null;
                    }
                }
            }
            if (fieldRecord.getVirtual()) {
                FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                if (!virtualColumns.containsKey(physicalRecord.getName())) {
                    virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                }
                virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
            } else {
                columnNames.add(entry.getKey());
                columnKeys.add(":" + entry.getKey());
                columnValues.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Map<String, Serializable>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add(JdbcFunction.columnCreate(entry.getValue()));
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        namedParameterJdbcTemplate.update("INSERT INTO " + collection + "(" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", columnValues);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/count/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> count(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentCountRequest requestBody
    ) {
        LOGGER.info("/document/count/{} appCode=>{} session=>{} body=>{}", collection, appCode, session, gson.toJson(requestBody));

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Integer count = jdbcTemplate.queryForObject("SELECT count(*) from `" + collection + "`", Integer.class);

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/modify/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentModifyRequest requestBody
    ) {
        LOGGER.info("/document/modify/{}/{} appCode=>{} session=>{} body=>{}", collection, id, appCode, session, gson.toJson(requestBody));

        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
            if (fieldRecord.getSqlType().equals("BLOB")) {
                blobRecords.put(fieldRecord.getFieldId(), fieldRecord);
            }
        }

        List<String> columns = new LinkedList<>();
        Map<String, Map<String, Serializable>> virtualColumns = new LinkedHashMap<>();
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
            if (fieldRecord == null) {
                continue;
            }
            if (fieldRecord.getVirtual()) {
                FieldRecord physicalRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                if (!virtualColumns.containsKey(physicalRecord.getName())) {
                    virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                }
                virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), (Serializable) entry.getValue());
            } else {
                columns.add(entry.getKey() + " = :" + entry.getKey());
                values.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Map<String, Serializable>> entry : virtualColumns.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                columns.add(entry.getKey() + " = " + JdbcFunction.columnAdd(entry.getKey(), entry.getValue()));
            }
        }

        values.put(collection + "_id", id);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update("UPDATE TABLE SET " + StringUtils.join(columns, ", ") + " WHERE " + collection + "_id = :" + collection + "_id", values);

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/delete/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> delete(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentDeleteRequest request
    ) {
        Application applicationTable = Tables.APPLICATION.as("applicationTable");
        User userTable = Tables.USER.as("userTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        UserPrivacy userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return null;
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields())
                .from(fieldTable)
                .where(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.NAME.eq(tableRecord.getName() + "_id"))
                .and(fieldTable.AUTO_INCREMENT.eq(true))
                .and(fieldTable.SYSTEM.eq(true))
                .fetchOneInto(fieldTable);

        if (fieldRecord == null) {
            return null;
        }

        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", id);

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.PUT, path = "/permission/grant/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody DocumenGrantPermissionUsername request
    ) {

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/permission/grant/{collection}/{id}/{action}/role/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody DocumenGrantPermissionRoleName request
    ) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.DELETE, path = "/permission/revoke/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.DELETE, path = "/permission/revoke/{collection}/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissiontRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

}
