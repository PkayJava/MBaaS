package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentFunction {

    public static void deleteDocument(DSLContext context, JdbcTemplate jdbcTemplate, String collection, String documentId) {
        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", documentId);
    }

    public static void modifyDocument(DSLContext context, JdbcTemplate jdbcTemplate, String collection, String documentId, DocumentModifyRequest requestBody) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeIdRecords = new LinkedHashMap<>();
        Map<String, AttributeRecord> attributeNameRecords = new LinkedHashMap<>();
        Map<String, AttributeTypeEnum> typeEnums = new LinkedHashMap<>();

        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeIdRecords.put(attributeRecord.getAttributeId(), attributeRecord);
            attributeNameRecords.put(attributeRecord.getName(), attributeRecord);
            typeEnums.put(attributeRecord.getName(), AttributeTypeEnum.valueOf(attributeRecord.getJavaType()));
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String patternDatetime = configuration.getString(Constants.PATTERN_DATETIME);

        List<String> columns = new LinkedList<>();
        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        Map<String, Object> values = new LinkedHashMap<>();
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
                columns.add(entry.getKey() + " = :" + entry.getKey());
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())) {
                    try {
                        values.put(entry.getKey(), FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue()));
                    } catch (ParseException e) {
                    }
                } else {
                    values.put(entry.getKey(), entry.getValue());
                }
            }
        }
        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                columns.add(entry.getKey() + " = " + MariaDBFunction.columnAdd(entry.getKey(), entry.getValue(), typeEnums));
            }
        }
        values.put(collection + "_id", documentId);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update("UPDATE `" + collectionRecord.getName() + "` SET " + StringUtils.join(columns, ", ") + " WHERE " + collection + "_id = :" + collection + "_id", values);
    }

    public static String insertDocument(DSLContext context, JdbcTemplate jdbcTemplate, String userId, String collection, DocumentCreateRequest requestBody) {

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeIdRecords = new LinkedHashMap<>();
        Map<String, AttributeRecord> attributeNameRecords = new LinkedHashMap<>();
        Map<String, AttributeTypeEnum> typeEnums = new LinkedHashMap<>();
        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeIdRecords.put(attributeRecord.getAttributeId(), attributeRecord);
            attributeNameRecords.put(attributeRecord.getName(), attributeRecord);
            typeEnums.put(attributeRecord.getName(), AttributeTypeEnum.valueOf(attributeRecord.getJavaType()));
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String patternDatetime = configuration.getString(Constants.PATTERN_DATETIME);

        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            AttributeRecord attributeRecord = attributeNameRecords.get(entry.getKey());
            boolean virtual = attributeRecord.getVirtual();
            if (virtual) {
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

        boolean found = false;
        for (Map.Entry<String, AttributeRecord> entry : attributeNameRecords.entrySet()) {
            if (entry.getValue().getVirtual()) {
                found = true;
                AttributeRecord physicalRecord = attributeIdRecords.get(entry.getValue().getVirtualAttributeId());
                if (virtualColumns.get(physicalRecord.getName()) == null || virtualColumns.get(physicalRecord.getName()).isEmpty()) {
                    Map<String, Object> temp = new LinkedHashMap<>();
                    temp.put("_temp", "_temp");
                    typeEnums.put("_temp", AttributeTypeEnum.String);
                    virtualColumns.put(physicalRecord.getName(), temp);
                }
            }
        }
        if (!found) {
            for (Map.Entry<String, AttributeRecord> entry : attributeNameRecords.entrySet()) {
                AttributeRecord attributeRecord = entry.getValue();
                if (attributeRecord.getJavaType().equals(AttributeTypeEnum.Blob.getLiteral()) && attributeRecord.getSystem()) {
                    Map<String, Object> temp = new LinkedHashMap<>();
                    temp.put("_temp", "_temp");
                    typeEnums.put("_temp", AttributeTypeEnum.String);
                    virtualColumns.put(attributeRecord.getName(), temp);
                }
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add(MariaDBFunction.columnCreate(entry.getValue(), typeEnums));
        }

        {
            // ownerUserId column
            String jdbcColumnOwnerUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
            if (attributeNameRecords.containsKey(jdbcColumnOwnerUserId)) {
                columnNames.add(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
                columnKeys.add(":" + configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
                columnValues.put(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID), userId);
            }
        }

        String documentId = UUID.randomUUID().toString();
        {
            // primary column
            columnNames.add("`" + collection + "_id" + "`");
            columnKeys.add("'" + documentId + "'");
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update("INSERT INTO `" + collection + "` (" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", columnValues);
        return documentId;
    }
}
