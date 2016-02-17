package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.response.DocumentCreateResponse;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.Response;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PermissionFactoryBean.Permission permission;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/query/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> query(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentQueryRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Read.getLiteral())) {
            return ResponseEntity.ok(null);
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
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName(), fieldRecord.getJavaType()));
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
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentQueryRequestById requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Read.getLiteral())) {
            return ResponseEntity.ok(null);
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
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName(), fieldRecord.getJavaType()));
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
    public ResponseEntity<DocumentCreateResponse> create(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentCreateRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        User userTable = Tables.USER.as("userTable");
        Token tokenTable = Tables.TOKEN.as("tokenTable");
        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TokenRecord tokenRecord = context.select(tokenTable.fields()).from(tokenTable).where(tokenTable.TOKEN_ID.eq(session)).fetchOneInto(tokenTable);
        if (tokenRecord == null) {
            return ResponseEntity.ok(null);
        }

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(tokenRecord.getUserId())).fetchOneInto(userTable);
        if (userRecord == null) {
            return ResponseEntity.ok(null);
        }

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Create.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
        for (FieldRecord fieldRecord : context.select(fieldTable.fields()).from(fieldTable).where(fieldTable.TABLE_ID.eq(tableRecord.getTableId())).fetchInto(fieldTable)) {
            fieldRecords.put(fieldRecord.getName(), fieldRecord);
        }

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (!fieldRecords.containsKey(entry.getKey())) {
                return ResponseEntity.ok(null);
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

        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            FieldRecord fieldRecord = fieldRecords.get(entry.getKey());
            if (fieldRecord.getNullable()) {
                if (fieldRecord.getJavaType().equals(String.class.getName())) {
                    if (entry.getValue() == null) {
                        return ResponseEntity.ok(null);
                    }
                } else {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        return ResponseEntity.ok(null);
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

        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add(JdbcFunction.columnCreate(entry.getValue()));
        }

        columnNames.add(configuration.getString(Constants.JDBC_OWNER_USER_ID));
        columnKeys.add(":" + configuration.getString(Constants.JDBC_OWNER_USER_ID));
        columnValues.put(configuration.getString(Constants.JDBC_OWNER_USER_ID), userRecord.getUserId());

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update("INSERT INTO " + collection + "(" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", new MapSqlParameterSource(columnValues), holder);

        Integer id = holder.getKey().intValue();

        List<String> columns = new LinkedList<>();
        for (Map.Entry<String, FieldRecord> entry : fieldRecords.entrySet()) {
            FieldRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    FieldRecord virtualRecord = blobRecords.get(fieldRecord.getVirtualFieldId());
                    columns.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName(), fieldRecord.getJavaType()));
                } else {
                    columns.add("`" + fieldRecord.getName() + "`");
                }
            }
        }

        Map<String, Object> values = jdbcTemplate.queryForMap("SELECT " + StringUtils.join(columns, ", ") + " FROM `" + tableRecord.getName() + "` where " + tableRecord.getName() + "_id = ?", id);

        DocumentCreateResponse response = new DocumentCreateResponse();
        response.getData().setCollectionName(collection);
        response.getData().getAttributes().putAll(values);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/count/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> count(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentCountRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Read.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        Integer count = jdbcTemplate.queryForObject("SELECT count(*) from `" + collection + "`", Integer.class);

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/modify/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyById(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentModifyRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Modify.getLiteral())) {
            return ResponseEntity.ok(null);
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
        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
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

        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
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
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody DocumentDeleteRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(collection)).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Delete.getLiteral())) {
            return ResponseEntity.ok(null);
        }

        FieldRecord fieldRecord = context.select(fieldTable.fields())
                .from(fieldTable)
                .where(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(fieldTable.NAME.eq(tableRecord.getName() + "_id"))
                .and(fieldTable.AUTO_INCREMENT.eq(true))
                .and(fieldTable.SYSTEM.eq(true))
                .fetchOneInto(fieldTable);

        if (fieldRecord == null) {
            return ResponseEntity.ok(null);
        }

        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", id);

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/user",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        User userTable = Tables.USER.as("userTable");
        DocumentUserPrivacy documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollection())).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
        if (userRecord == null) {
            return ResponseEntity.ok(null);
        }

        DocumentUserPrivacyRecord documentUserPrivacyRecord = context.newRecord(documentUserPrivacyTable);
        documentUserPrivacyRecord.setDocumentId(requestBody.getDocumentId());
        documentUserPrivacyRecord.setTableId(tableRecord.getTableId());
        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }
        documentUserPrivacyRecord.setPermisson(permission);
        documentUserPrivacyRecord.setUserId(userRecord.getUserId());

        documentUserPrivacyRecord.store();

        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/role",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Role roleTable = Tables.ROLE.as("roleTable");
        DocumentRolePrivacy documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollection())).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(requestBody.getRoleName())).fetchOneInto(roleTable);
        if (roleRecord == null) {
            return ResponseEntity.ok(null);
        }

        DocumentRolePrivacyRecord documentRolePrivacyRecord = context.newRecord(documentRolePrivacyTable);
        documentRolePrivacyRecord.setDocumentId(requestBody.getDocumentId());
        documentRolePrivacyRecord.setTableId(tableRecord.getTableId());
        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }
        documentRolePrivacyRecord.setPermisson(permission);
        documentRolePrivacyRecord.setRoleId(roleRecord.getRoleId());

        documentRolePrivacyRecord.store();

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/user",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        User userTable = Tables.USER.as("userTable");
        DocumentUserPrivacy documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollection())).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
        if (userRecord == null) {
            return ResponseEntity.ok(null);
        }

        DocumentUserPrivacyRecord documentUserPrivacyRecord = context.select(documentUserPrivacyTable.fields())
                .from(documentUserPrivacyTable)
                .where(documentUserPrivacyTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(documentUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                .and(documentUserPrivacyTable.DOCUMENT_ID.eq(requestBody.getDocumentId()))
                .fetchOneInto(documentUserPrivacyTable);

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }
        documentUserPrivacyRecord.setPermisson(documentUserPrivacyRecord.getPermisson() - permission);

        documentUserPrivacyRecord.update();

        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/role",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        Table tableTable = Tables.TABLE.as("tableTable");
        Role roleTable = Tables.ROLE.as("roleTable");
        DocumentRolePrivacy documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

        TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable).where(tableTable.NAME.eq(requestBody.getCollection())).fetchOneInto(tableTable);
        if (tableRecord == null) {
            return ResponseEntity.ok(null);
        }

        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(requestBody.getRoleName())).fetchOneInto(roleTable);
        if (roleRecord == null) {
            return ResponseEntity.ok(null);
        }

        DocumentRolePrivacyRecord documentRolePrivacyRecord = context.select(documentRolePrivacyTable.fields())
                .from(documentRolePrivacyTable)
                .where(documentRolePrivacyTable.TABLE_ID.eq(tableRecord.getTableId()))
                .and(documentRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                .and(documentRolePrivacyTable.DOCUMENT_ID.eq(requestBody.getDocumentId()))
                .fetchOneInto(documentRolePrivacyTable);

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }
        documentRolePrivacyRecord.setPermisson(documentRolePrivacyRecord.getPermisson() - permission);

        documentRolePrivacyRecord.update();

        return ResponseEntity.ok(null);
    }

}
