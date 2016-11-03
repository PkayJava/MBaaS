package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.EavBooleanTable;
import com.angkorteam.mbaas.model.entity.tables.EavCharacterTable;
import com.angkorteam.mbaas.model.entity.tables.EavDateTable;
import com.angkorteam.mbaas.model.entity.tables.EavDateTimeTable;
import com.angkorteam.mbaas.model.entity.tables.EavDecimalTable;
import com.angkorteam.mbaas.model.entity.tables.EavIntegerTable;
import com.angkorteam.mbaas.model.entity.tables.EavTextTable;
import com.angkorteam.mbaas.model.entity.tables.EavTimeTable;
import com.angkorteam.mbaas.model.entity.tables.EavVarcharTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//
//import com.angkorteam.mbaas.configuration.Constants;
//import com.angkorteam.mbaas.plain.enums.TypeEnum;
//import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
//import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
//import com.angkorteam.mbaas.server.Jdbc;
//import org.apache.commons.configuration.XMLPropertiesConfiguration;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//
//import java.util.*;
//

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentFunction {

    public static void deleteDocument(String collectionId, String documentId) {
        DSLContext context = Spring.getBean(DSLContext.class);
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        EavBooleanTable eavBooleanTable = Tables.EAV_BOOLEAN.as("eavBooleanTable");
        EavCharacterTable eavCharacterTable = Tables.EAV_CHARACTER.as("eavCharacterTable");
        EavDateTable eavDateTable = Tables.EAV_DATE.as("eavDateTable");
        EavDecimalTable eavDecimalTable = Tables.EAV_DECIMAL.as("eavDecimalTable");
        EavIntegerTable eavIntegerTable = Tables.EAV_INTEGER.as("eavIntegerTable");
        EavTextTable eavTextTable = Tables.EAV_TEXT.as("eavTextTable");
        EavTimeTable eavTimeTable = Tables.EAV_TIME.as("eavTimeTable");
        EavVarcharTable eavVarcharTable = Tables.EAV_VARCHAR.as("eavVarcharTable");
        EavDateTimeTable eavDateTimeTable = Tables.EAV_DATE_TIME.as("eavDateTimeTable");
        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);
        jdbcTemplate.update("DELETE FROM `" + collection.getName() + "` WHERE " + collection.getName() + "_id = ?", documentId);
        context.delete(eavBooleanTable).where(eavBooleanTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavCharacterTable).where(eavCharacterTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavDateTable).where(eavDateTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavDecimalTable).where(eavDecimalTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavIntegerTable).where(eavIntegerTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavTextTable).where(eavTextTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavTimeTable).where(eavTimeTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavVarcharTable).where(eavVarcharTable.DOCUMENT_ID.eq(documentId)).execute();
        context.delete(eavDateTimeTable).where(eavDateTimeTable.DOCUMENT_ID.eq(documentId)).execute();

    }

    public static boolean internalModifyDocument(String collectionId, String documentId, DocumentModifyRequest requestBody) {
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);
        List<AttributePojo> attributes = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionId)).and(attributeTable.SYSTEM.eq(false)).fetchInto(AttributePojo.class);
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        System system = Spring.getBean(System.class);

        Map<String, AttributePojo> attributeRecords = Maps.newLinkedHashMap();

        for (AttributePojo attributeRecord : attributes) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributeRecords, requestBody.getDocument());

        List<String> nulls = Lists.newArrayList();
        for (Map.Entry<String, Object> item : requestBody.getDocument().entrySet()) {
            if (item.getValue() == null) {
                nulls.add(item.getKey());
            }
        }
        if (!nulls.isEmpty()) {
            for (String key : nulls) {
                requestBody.getDocument().remove(key);
            }
        }

        Map<String, Object> goodDocument = new HashMap<>();
        if (good) {
            for (Map.Entry<String, Object> item : requestBody.getDocument().entrySet()) {
                if (item.getValue() != null && item.getValue() instanceof Character) {
                    goodDocument.put(item.getKey(), String.valueOf((Character) item.getValue()));
                } else {
                    goodDocument.put(item.getKey(), item.getValue());
                }
            }

            List<String> columns = Lists.newLinkedList();
            Map<String, Object> values = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : goodDocument.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                if (!attributeRecords.get(entry.getKey()).getEav()) {
                    columns.add(entry.getKey() + " = :" + entry.getKey());
                    values.put(entry.getKey(), entry.getValue());
                }
            }
            for (String nul : nulls) {
                if (!attributeRecords.get(nul).getEav()) {
                    columns.add(nul + " = :" + nul);
                    values.put(nul, null);
                }
            }
            values.put(collection.getName() + "_id", documentId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("UPDATE `" + collection.getName() + "` SET " + StringUtils.join(columns, ", ") + " WHERE " + collection.getName() + "_id = :" + collection.getName() + "_id", values);

            for (Map.Entry<String, Object> entry : goodDocument.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                AttributePojo attributeRecord = attributeRecords.get(entry.getKey());
                if (attributeRecord.getEav()) {
                    TypeEnum attributeType = TypeEnum.valueOf(attributeRecord.getType());
                    String eavTable = attributeType.getEavTable();
                    int effect = jdbcTemplate.update("UPDATE " + eavTable + " SET EAV_VALUE = ? WHERE COLLECTION_ID = ? AND ATTRIBUTE_ID = ? AND DOCUMENT_ID = ?", entry.getValue(), collectionId, attributeRecord.getAttributeId(), documentId);
                    if (effect == 0) {
                        jdbcTemplate.update("INSERT INTO " + eavTable + "(" + eavTable + "_id,COLLECTION_ID,DOCUMENT_ID,ATTRIBUTE_ID,ATTRIBUTE_TYPE,EAV_VALUE) values(?,?,?,?,?,?)", system.randomUUID(), collectionId, documentId, attributeRecord.getAttributeId(), attributeRecord.getType(), entry.getValue());
                    }
                }
            }
            for (String nul : nulls) {
                AttributePojo attributeRecord = attributeRecords.get(nul);
                if (attributeRecord.getEav()) {
                    TypeEnum attributeType = TypeEnum.valueOf(attributeRecord.getType());
                    String eavTable = attributeType.getEavTable();
                    int effect = jdbcTemplate.update("UPDATE " + eavTable + " SET EAV_VALUE = ? WHERE COLLECTION_ID = ? AND ATTRIBUTE_ID = ? AND DOCUMENT_ID = ?", null, collectionId, attributeRecord.getAttributeId(), documentId);
                    if (effect == 0) {
                        jdbcTemplate.update("INSERT INTO " + eavTable + "(" + eavTable + "_id,COLLECTION_ID,DOCUMENT_ID,ATTRIBUTE_ID,ATTRIBUTE_TYPE,EAV_VALUE) values(?,?,?,?,?,?)", system.randomUUID(), collectionId, documentId, attributeRecord.getAttributeId(), attributeRecord.getType(), null);
                    }
                }
            }
        }
        return good;
    }

    public static boolean internalInsertDocument(String collectionId, DocumentCreateRequest requestBody) {
        DSLContext context = Spring.getBean(DSLContext.class);
        System system = Spring.getBean(System.class);
        String documentId = system.randomUUID();
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);
        List<AttributePojo> attributes = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionId)).fetchInto(AttributePojo.class);

        Map<String, AttributePojo> attributesMap = Maps.newLinkedHashMap();
        for (AttributePojo attribute : attributes) {
            attributesMap.put(attribute.getName(), attribute);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributesMap, requestBody.getDocument());

        Map<String, Object> goodDocument = Maps.newHashMap();
        if (good) {
            for (Map.Entry<String, Object> item : requestBody.getDocument().entrySet()) {
                if (item.getValue() != null && item.getValue() instanceof Character) {
                    goodDocument.put(item.getKey(), String.valueOf((Character) item.getValue()));
                } else {
                    goodDocument.put(item.getKey(), item.getValue());
                }
            }

            goodDocument.put(collection.getName() + "_id", documentId);

            List<String> fields = Lists.newLinkedList();
            List<Object> values = Lists.newLinkedList();
            Map<String, Object> eavs = Maps.newHashMap();
            for (Map.Entry<String, Object> item : goodDocument.entrySet()) {
                AttributePojo attributeRecord = attributesMap.get(item.getKey());
                if (!attributeRecord.getEav()) {
                    fields.add(item.getKey());
                    values.add(":" + item.getKey());
                } else {
                    eavs.put(item.getKey(), item.getValue());
                }
            }
            String jdbc = "INSERT INTO " + collection.getName() + "(" + StringUtils.join(fields, ", ") + ") VALUES(" + StringUtils.join(values, ", ") + ")";
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
            template.update(jdbc, goodDocument);
            if (!eavs.isEmpty()) {
                CommonFunction.saveEavAttributes(collectionId, documentId, attributesMap, eavs);
            }
        }
        return good;
    }
}
