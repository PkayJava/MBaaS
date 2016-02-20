package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.response.*;
import com.angkorteam.mbaas.server.factory.PermissionFactoryBean;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.*;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
@Controller
@RequestMapping(path = "/document")
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

    //region /document/create/{collection}

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
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        if (sessionRecord == null) {
            errorMessages.put("session", "session invalid");
        }

        UserRecord userRecord = null;
        if (sessionRecord != null) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("session", "session invalid");
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            errorMessages.put("collection", "collection is not found");
        }

        Map<String, AttributeRecord> attributeIdRecords = new LinkedHashMap<>();
        Map<String, AttributeRecord> attributeNameRecords = new LinkedHashMap<>();
        if (collectionRecord != null) {
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
                attributeIdRecords.put(attributeRecord.getAttributeId(), attributeRecord);
                attributeNameRecords.put(attributeRecord.getName(), attributeRecord);

                if (!attributeRecord.getSystem() && !attributeRecord.getNullable() && !requestBody.getDocument().containsKey(attributeRecord.getName())) {
                    errorMessages.put(attributeRecord.getName(), "is required");
                }
            }
        }

        String patternDatetime = configuration.getString(Constants.PATTERN_DATETIME);

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (!attributeNameRecords.containsKey(entry.getKey())) {
                errorMessages.put(entry.getKey(), "is not allow");
            } else {
                AttributeRecord attributeRecord = attributeNameRecords.get(entry.getKey());
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())) {
                    if (attributeRecord.getNullable()) {
                        if (entry.getValue() != null) {
                            if (entry.getValue().getClass().getName().equals(String.class.getName())) {
                                try {
                                    FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue());
                                } catch (ParseException e) {
                                    errorMessages.put(entry.getKey(), "is bad value");
                                }
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        }
                    } else {
                        if (entry.getValue() != null) {
                            if (entry.getValue().getClass().getName().equals(String.class.getName())) {
                                try {
                                    FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue());
                                } catch (ParseException e) {
                                    errorMessages.put(entry.getKey(), "is bad value");
                                }
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        } else {
                            errorMessages.put(entry.getKey(), "is required");
                        }
                    }
                } else if (attributeRecord.getJavaType().equals(Byte.class.getName()) || attributeRecord.getJavaType().equals(byte.class.getName())
                        || attributeRecord.getJavaType().equals(Short.class.getName()) || attributeRecord.getJavaType().equals(short.class.getName())
                        || attributeRecord.getJavaType().equals(Integer.class.getName()) || attributeRecord.getJavaType().equals(int.class.getName())
                        || attributeRecord.getJavaType().equals(Long.class.getName()) || attributeRecord.getJavaType().equals(long.class.getName())) {
                    if (attributeRecord.getNullable()) {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Byte.class.getName()) || clazzName.equals(byte.class.getName())
                                    || clazzName.equals(Short.class.getName()) || clazzName.equals(short.class.getName())
                                    || clazzName.equals(Integer.class.getName()) || clazzName.equals(int.class.getName())
                                    || clazzName.equals(Long.class.getName()) || clazzName.equals(long.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        }
                    } else {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Byte.class.getName()) || clazzName.equals(byte.class.getName())
                                    || clazzName.equals(Short.class.getName()) || clazzName.equals(short.class.getName())
                                    || clazzName.equals(Integer.class.getName()) || clazzName.equals(int.class.getName())
                                    || clazzName.equals(Long.class.getName()) || clazzName.equals(long.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        } else {
                            errorMessages.put(entry.getKey(), "is required");
                        }
                    }
                } else if (attributeRecord.getJavaType().equals(Float.class.getName()) || attributeRecord.getJavaType().equals(float.class.getName())
                        || attributeRecord.getJavaType().equals(Double.class.getName()) || attributeRecord.getJavaType().equals(double.class.getName())) {
                    if (attributeRecord.getNullable()) {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Float.class.getName()) || clazzName.equals(float.class.getName())
                                    || clazzName.equals(Double.class.getName()) || clazzName.equals(double.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        }
                    } else {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Float.class.getName()) || clazzName.equals(float.class.getName())
                                    || clazzName.equals(Double.class.getName()) || clazzName.equals(double.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        } else {
                            errorMessages.put(entry.getKey(), "is required");
                        }
                    }
                } else if (attributeRecord.getJavaType().equals(Boolean.class.getName()) || attributeRecord.getJavaType().equals(boolean.class.getName())) {
                    if (attributeRecord.getNullable()) {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Boolean.class.getName()) || clazzName.equals(boolean.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        }
                    } else {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(Boolean.class.getName()) || clazzName.equals(boolean.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        } else {
                            errorMessages.put(entry.getKey(), "is required");
                        }
                    }
                } else if (attributeRecord.getJavaType().equals(String.class.getName())
                        || attributeRecord.getJavaType().equals(Character.class.getName()) || attributeRecord.getJavaType().equals(char.class.getName())) {
                    if (attributeRecord.getNullable()) {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(String.class.getName())
                                    || clazzName.equals(Character.class.getName()) || clazzName.equals(char.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        }
                    } else {
                        if (entry.getValue() != null) {
                            String clazzName = entry.getValue().getClass().getName();
                            if (clazzName.equals(String.class.getName())
                                    || clazzName.equals(Character.class.getName()) || clazzName.equals(char.class.getName())) {
                            } else {
                                errorMessages.put(entry.getKey(), "is bad value");
                            }
                        } else {
                            errorMessages.put(entry.getKey(), "is required");
                        }
                    }
                } else {
                    errorMessages.put(entry.getKey(), "is bad value");
                }
            }
        }

        if (collectionRecord != null) {
            if (permission.isAdministratorUser(session)
                    || permission.isBackOfficeUser(session)
                    || permission.isCollectionOwner(session, collection)
                    || permission.hasCollectionPermission(session, collection, PermissionEnum.Create.getLiteral())) {
            } else {
                errorMessages.put("permission", "don't have write permission to this collection");
            }
        }

        if (!errorMessages.isEmpty()) {
            DocumentCreateResponse response = new DocumentCreateResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            AttributeRecord attributeRecord = attributeNameRecords.get(entry.getKey());
            if (attributeRecord.getVirtual()) {
                AttributeRecord physicalRecord = attributeIdRecords.get(attributeRecord.getVirtualAttributeId());
                if (!virtualColumns.containsKey(physicalRecord.getName())) {
                    virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                }
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())) {
                    try {
                        virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue()));
                    } catch (ParseException e) {
                    }
                } else {
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
                }
            } else {
                columnNames.add(entry.getKey());
                columnKeys.add(":" + entry.getKey());
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())) {
                    try {
                        columnValues.put(entry.getKey(), FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue()));
                    } catch (ParseException e) {
                    }
                } else {
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add(JdbcFunction.columnCreate(entry.getValue()));
        }

        {
            // ownerUserId column
            columnNames.add(configuration.getString(Constants.JDBC_OWNER_USER_ID));
            columnKeys.add(":" + configuration.getString(Constants.JDBC_OWNER_USER_ID));
            columnValues.put(configuration.getString(Constants.JDBC_OWNER_USER_ID), userRecord.getUserId());
        }

        String id = UUID.randomUUID().toString();
        {
            // primary column
            columnNames.add("`" + collection + "_id" + "`");
            columnKeys.add("'" + id + "'");
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update("INSERT INTO `" + collection + "` (" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", columnValues);

        DocumentCreateResponse response = new DocumentCreateResponse();
        response.getData().setCollectionName(collection);
        response.getData().setDocumentId(id);
        response.getData().getAttributes().put(collection + "_id", id);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/count/{collection}

    @RequestMapping(
            method = RequestMethod.POST, path = "/count/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentCountResponse> count(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("collection") String collection,
            @RequestBody DocumentCountRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");

        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        if (sessionRecord == null) {
            errorMessages.put("session", "session invalid");
        }

        UserRecord userRecord = null;
        if (sessionRecord != null) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("session", "session invalid");
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            errorMessages.put("collection", "collection is not found");
        }

        if (collectionRecord != null) {
            if (permission.isAdministratorUser(session)
                    || permission.isBackOfficeUser(session)
                    || permission.isCollectionOwner(session, collection)
                    || permission.hasCollectionPermission(session, collection, PermissionEnum.Read.getLiteral())) {
            } else {
                errorMessages.put("permission", "don't have write permission to this collection");
            }
        }

        if (!errorMessages.isEmpty()) {
            DocumentCountResponse response = new DocumentCountResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        Long total = jdbcTemplate.queryForObject("SELECT count(*) from `" + collection + "` where ", Long.class);
        DocumentCountResponse response = new DocumentCountResponse();
        response.getData().setTotal(total);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/modify/{collection}/{id}

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
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        SessionRecord sessionRecord = context.select(sessionTable.fields()).from(sessionTable).where(sessionTable.SESSION_ID.eq(session)).fetchOneInto(sessionTable);
        if (sessionRecord == null) {
            errorMessages.put("session", "session invalid");
        }

        UserRecord userRecord = null;
        if (sessionRecord != null) {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(sessionRecord.getUserId())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("session", "session invalid");
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            errorMessages.put("collection", "collection is not found");
        }

        Map<String, AttributeRecord> attributeIdRecords = new LinkedHashMap<>();
        Map<String, AttributeRecord> attributeNameRecords = new LinkedHashMap<>();
        if (collectionRecord != null) {
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
                attributeIdRecords.put(attributeRecord.getAttributeId(), attributeRecord);
                attributeNameRecords.put(attributeRecord.getName(), attributeRecord);

                if (!attributeRecord.getSystem() && !attributeRecord.getNullable() && !requestBody.getDocument().containsKey(attributeRecord.getName())) {
                    errorMessages.put(attributeRecord.getName(), "is required");
                }
            }
        }

        if (collectionRecord != null) {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + collection + "` WHERE `" + collection + "_id` = ?", Integer.class, id);
            if (count == 0) {
                errorMessages.put(id, "is not found");
            }
        }

        String patternDatetime = configuration.getString(Constants.PATTERN_DATETIME);

        List<String> columns = new LinkedList<>();
        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            AttributeRecord fieldRecord = attributeNameRecords.get(entry.getKey());
            if (fieldRecord == null) {
                continue;
            }
            if (fieldRecord.getVirtual()) {
                AttributeRecord physicalRecord = attributeIdRecords.get(fieldRecord.getVirtualAttributeId());
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

    //endregion

    //region /document/delete/{collection}/{id}

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

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            return ResponseEntity.ok(null);
        }

//        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Delete.getLiteral())) {
//            return ResponseEntity.ok(null);
//        }

        AttributeRecord fieldRecord = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(attributeTable.NAME.eq(collectionRecord.getName() + "_id"))
                .and(attributeTable.AUTO_INCREMENT.eq(true))
                .and(attributeTable.SYSTEM.eq(true))
                .fetchOneInto(attributeTable);

        if (fieldRecord == null) {
            return ResponseEntity.ok(null);
        }

        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", id);

        return ResponseEntity.ok(null);
    }

    //endregion

    //region /document/permission/grant/username

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentPermissionUsernameResponse> grantPermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        UserTable userTable = Tables.USER.as("userTable");
        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields())
                    .from(collectionTable)
                    .where(collectionTable.NAME.eq(requestBody.getCollectionName()))
                    .fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        UserRecord userRecord = null;
        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("collectionName", "is required");
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("username", "is not found");
            }
        }

        if (requestBody.getDocumentId() == null || "".equals(requestBody.getDocumentId())) {
            errorMessages.put("documentId", "is required");
        } else {
            if (collectionRecord != null) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + collectionRecord.getName() + "` WHERE " + collectionRecord.getName() + "_id = ?", Integer.class, requestBody.getDocumentId());
                if (count == 0) {
                    errorMessages.put("documentId", "is not found");
                }
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
            DocumentPermissionUsernameResponse response = new DocumentPermissionUsernameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }

        DocumentUserPrivacyRecord documentUserPrivacyRecord = context.newRecord(documentUserPrivacyTable);
        documentUserPrivacyRecord.setDocumentId(requestBody.getDocumentId());
        documentUserPrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
        documentUserPrivacyRecord.setPermisson(permission);
        documentUserPrivacyRecord.setUserId(userRecord.getUserId());
        documentUserPrivacyRecord.store();

        DocumentPermissionUsernameResponse response = new DocumentPermissionUsernameResponse();
        response.getData().setPermission(permission);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/permission/grant/rolename

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/grant/rolename",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentPermissionRoleNameResponse> grantPermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields())
                    .from(collectionTable)
                    .where(collectionTable.NAME.eq(requestBody.getCollectionName()))
                    .fetchOneInto(collectionTable);
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

        if (requestBody.getDocumentId() == null || "".equals(requestBody.getDocumentId())) {
            errorMessages.put("documentId", "is required");
        } else {
            if (collectionRecord != null) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + collectionRecord.getName() + "` WHERE " + collectionRecord.getName() + "_id = ?", Integer.class, requestBody.getDocumentId());
                if (count == 0) {
                    errorMessages.put("documentId", "is not found");
                }
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
            DocumentPermissionRoleNameResponse response = new DocumentPermissionRoleNameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        int permission = 0;
        for (Integer action : requestBody.getActions()) {
            permission = permission | action;
        }

        DocumentRolePrivacyRecord documentRolePrivacyRecord = context.newRecord(documentRolePrivacyTable);
        documentRolePrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
        documentRolePrivacyRecord.setDocumentId(requestBody.getDocumentId());
        documentRolePrivacyRecord.setRoleId(roleRecord.getRoleId());
        documentRolePrivacyRecord.setPermisson(permission);
        documentRolePrivacyRecord.store();

        DocumentPermissionRoleNameResponse response = new DocumentPermissionRoleNameResponse();
        response.getData().setPermission(permission);

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/permission/revoke/username

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentPermissionUsernameResponse> revokePermissionUsername(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionUsernameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        UserTable userTable = Tables.USER.as("userTable");
        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields())
                    .from(collectionTable)
                    .where(collectionTable.NAME.eq(requestBody.getCollectionName()))
                    .fetchOneInto(collectionTable);
            if (collectionRecord == null) {
                errorMessages.put("collectionName", "is not found");
            }
        }

        UserRecord userRecord = null;
        if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
            errorMessages.put("collectionName", "is required");
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(userTable);
            if (userRecord == null) {
                errorMessages.put("username", "is not found");
            }
        }

        if (requestBody.getDocumentId() == null || "".equals(requestBody.getDocumentId())) {
            errorMessages.put("documentId", "is required");
        } else {
            if (collectionRecord != null) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + collectionRecord.getName() + "` WHERE " + collectionRecord.getName() + "_id = ?", Integer.class, requestBody.getDocumentId());
                if (count == 0) {
                    errorMessages.put("documentId", "is not found");
                }
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
            DocumentPermissionUsernameResponse response = new DocumentPermissionUsernameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        DocumentUserPrivacyRecord documentUserPrivacyRecord = context.select(documentUserPrivacyTable.fields())
                .from(documentUserPrivacyTable)
                .where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(documentUserPrivacyTable.USER_ID.eq(userRecord.getUserId()))
                .and(documentUserPrivacyTable.DOCUMENT_ID.eq(requestBody.getDocumentId()))
                .fetchOneInto(documentUserPrivacyTable);

        int revokePermission = 0;
        for (Integer action : requestBody.getActions()) {
            revokePermission = revokePermission | action;
        }

        if (documentUserPrivacyRecord != null) {
            int permission = documentUserPrivacyRecord.getPermisson();
            if ((permission & revokePermission) == revokePermission) {
                documentUserPrivacyRecord.setPermisson(permission - revokePermission);
            }
            documentUserPrivacyRecord.update();
        } else {
            documentUserPrivacyRecord = context.newRecord(documentUserPrivacyTable);
            documentUserPrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
            documentUserPrivacyRecord.setUserId(userRecord.getUserId());
            documentUserPrivacyRecord.setDocumentId(requestBody.getDocumentId());
            documentUserPrivacyRecord.setPermisson(0);
            documentUserPrivacyRecord.store();
        }

        DocumentPermissionUsernameResponse response = new DocumentPermissionUsernameResponse();
        response.getData().setPermission(documentUserPrivacyRecord.getPermisson());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/permission/revoke/rolename

    @RequestMapping(
            method = RequestMethod.POST, path = "/permission/revoke/rolename",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentPermissionRoleNameResponse> revokePermissionRoleName(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody DocumentPermissionRoleNameRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

        CollectionRecord collectionRecord = null;
        if (requestBody.getCollectionName() == null || "".equals(requestBody.getCollectionName())) {
            errorMessages.put("collectionName", "is required");
        } else {
            collectionRecord = context.select(collectionTable.fields())
                    .from(collectionTable)
                    .where(collectionTable.NAME.eq(requestBody.getCollectionName()))
                    .fetchOneInto(collectionTable);
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

        if (requestBody.getDocumentId() == null || "".equals(requestBody.getDocumentId())) {
            errorMessages.put("documentId", "is required");
        } else {
            if (collectionRecord != null) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `" + collectionRecord.getName() + "` WHERE " + collectionRecord.getName() + "_id = ?", Integer.class, requestBody.getDocumentId());
                if (count == 0) {
                    errorMessages.put("documentId", "is not found");
                }
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
            DocumentPermissionRoleNameResponse response = new DocumentPermissionRoleNameResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        DocumentRolePrivacyRecord documentRolePrivacyRecord = context.select(documentRolePrivacyTable.fields())
                .from(documentRolePrivacyTable)
                .where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .and(documentRolePrivacyTable.ROLE_ID.eq(roleRecord.getRoleId()))
                .and(documentRolePrivacyTable.DOCUMENT_ID.eq(requestBody.getDocumentId()))
                .fetchOneInto(documentRolePrivacyTable);

        int revokePermission = 0;
        for (Integer action : requestBody.getActions()) {
            revokePermission = revokePermission | action;
        }

        if (documentRolePrivacyRecord != null) {
            int permission = documentRolePrivacyRecord.getPermisson();
            if ((permission & revokePermission) == revokePermission) {
                documentRolePrivacyRecord.setPermisson(permission - revokePermission);
            }
            documentRolePrivacyRecord.update();
        } else {
            documentRolePrivacyRecord = context.newRecord(documentRolePrivacyTable);
            documentRolePrivacyRecord.setCollectionId(collectionRecord.getCollectionId());
            documentRolePrivacyRecord.setRoleId(roleRecord.getRoleId());
            documentRolePrivacyRecord.setDocumentId(requestBody.getDocumentId());
            documentRolePrivacyRecord.setPermisson(0);
            documentRolePrivacyRecord.store();
        }

        DocumentPermissionRoleNameResponse response = new DocumentPermissionRoleNameResponse();
        response.getData().setPermission(documentRolePrivacyRecord.getPermisson());

        return ResponseEntity.ok(response);
    }

    //endregion

    //region /document/query/{collection}

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

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            return ResponseEntity.ok(null);
        }

//        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Read.getLiteral())) {
//            return ResponseEntity.ok(null);
//        }

        Map<String, AttributeRecord> fieldRecords = new LinkedHashMap<>();
        for (AttributeRecord fieldRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            fieldRecords.put(fieldRecord.getAttributeId(), fieldRecord);
        }

        List<String> fields = new LinkedList<>();
        for (Map.Entry<String, AttributeRecord> entry : fieldRecords.entrySet()) {
            AttributeRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    AttributeRecord virtualRecord = fieldRecords.get(fieldRecord.getVirtualAttributeId());
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName(), fieldRecord.getJavaType()));
                } else {
                    fields.add("`" + entry.getKey() + "`");
                }
            }
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT " + StringUtils.join(fields, ", ") + " from " + collection + " limit 0,10");

        return ResponseEntity.ok(null);
    }

    //endregion

    //region /document/query/{collection}/{id}

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

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);
        if (collectionRecord == null) {
            return ResponseEntity.ok(null);
        }

//        if (!permission.hasCollectionAccess(session, collection, PermissionEnum.Read.getLiteral())) {
//            return ResponseEntity.ok(null);
//        }

        Map<String, AttributeRecord> fieldRecords = new LinkedHashMap<>();
        for (AttributeRecord fieldRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            fieldRecords.put(fieldRecord.getAttributeId(), fieldRecord);
        }

        List<String> fields = new LinkedList<>();
        for (Map.Entry<String, AttributeRecord> entry : fieldRecords.entrySet()) {
            AttributeRecord fieldRecord = entry.getValue();
            if (fieldRecord.getExposed()) {
                if (fieldRecord.getVirtual()) {
                    AttributeRecord virtualRecord = fieldRecords.get(fieldRecord.getVirtualAttributeId());
                    fields.add(JdbcFunction.columnGet(virtualRecord.getName(), fieldRecord.getName(), fieldRecord.getJavaType()));
                } else {
                    fields.add("`" + entry.getKey() + "`");
                }
            }
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT " + StringUtils.join(fields, ", ") + " from " + collection + " where " + collection + "_id = ?", id);

        return ResponseEntity.ok(null);
    }

    //endregion

}
