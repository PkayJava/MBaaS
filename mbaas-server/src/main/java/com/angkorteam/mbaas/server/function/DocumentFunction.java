package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentFunction {

    public static void deleteDocument(JdbcTemplate jdbcTemplate, String collection, String documentId) {
        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", documentId);
    }

    public static boolean internalModifyDocument(JdbcTemplate jdbcTemplate, String collectionId, String collectionName, String documentId, DocumentModifyRequest requestBody) {
        Map<String, Map<String, Object>> attributeRecords = new LinkedHashMap<>();

        for (Map<String, Object> attributeRecord : jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ?", collectionId)) {
            attributeRecords.put((String) attributeRecord.get(Jdbc.Attribute.NAME), attributeRecord);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributeRecords, requestBody.getDocument());

        List<String> nulls = new ArrayList<>();
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

            List<String> columns = new LinkedList<>();
            Map<String, Object> values = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : goodDocument.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                if (!(boolean) attributeRecords.get(entry.getKey()).get(Jdbc.Attribute.EAV)) {
                    columns.add(entry.getKey() + " = :" + entry.getKey());
                    values.put(entry.getKey(), entry.getValue());
                }
            }
            for (String nul : nulls) {
                if (!(boolean) attributeRecords.get(nul).get(Jdbc.Attribute.EAV)) {
                    columns.add(nul + " = :" + nul);
                    values.put(nul, null);
                }
            }
            values.put(collectionName + "_id", documentId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("UPDATE `" + collectionName + "` SET " + StringUtils.join(columns, ", ") + " WHERE " + collectionName + "_id = :" + collectionName + "_id", values);

            for (Map.Entry<String, Object> entry : goodDocument.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                Map<String, Object> attributeRecord = attributeRecords.get(entry.getKey());
                if ((boolean) attributeRecord.get(Jdbc.Attribute.EAV)) {
                    TypeEnum attributeType = TypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
                    String eavTable = attributeType.getEavTable();
                    int effect = jdbcTemplate.update("UPDATE " + eavTable + " SET EAV_VALUE = ? WHERE COLLECTION_ID = ? AND ATTRIBUTE_ID = ? AND DOCUMENT_ID = ?", entry.getValue(), collectionId, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID), documentId);
                    if (effect == 0) {
                        jdbcTemplate.update("INSERT INTO " + eavTable + "(" + eavTable + "_id,COLLECTION_ID,DOCUMENT_ID,ATTRIBUTE_ID,ATTRIBUTE_TYPE,EAV_VALUE) values(?,?,?,?,?,?)", UUID.randomUUID().toString(), collectionId, documentId, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID), attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE), entry.getValue());
                    }
                }
            }
            for (String nul : nulls) {
                Map<String, Object> attributeRecord = attributeRecords.get(nul);
                if ((boolean) attributeRecord.get(Jdbc.Attribute.EAV)) {
                    TypeEnum attributeType = TypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
                    String eavTable = attributeType.getEavTable();
                    int effect = jdbcTemplate.update("UPDATE " + eavTable + " SET EAV_VALUE = ? WHERE COLLECTION_ID = ? AND ATTRIBUTE_ID = ? AND DOCUMENT_ID = ?", null, collectionId, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID), documentId);
                    if (effect == 0) {
                        jdbcTemplate.update("INSERT INTO " + eavTable + "(" + eavTable + "_id,COLLECTION_ID,DOCUMENT_ID,ATTRIBUTE_ID,ATTRIBUTE_TYPE,EAV_VALUE) values(?,?,?,?,?,?)", UUID.randomUUID().toString(), collectionId, documentId, attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID), attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE), null);
                    }
                }
            }
        }
        return good;
    }

    public static boolean internalInsertDocument(JdbcTemplate jdbcTemplate, String ownerApplicationUserId, String documentId, String collectionId, String collectionName, DocumentCreateRequest requestBody) {
        Map<String, Map<String, Object>> attributeRecords = new LinkedHashMap<>();
        for (Map<String, Object> attributeRecord : jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ?", collectionId)) {
            attributeRecords.put((String) attributeRecord.get(Jdbc.Attribute.NAME), attributeRecord);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributeRecords, requestBody.getDocument());

        Map<String, Object> goodDocument = new HashMap<>();
        if (good) {
            for (Map.Entry<String, Object> item : requestBody.getDocument().entrySet()) {
                if (item.getValue() != null && item.getValue() instanceof Character) {
                    goodDocument.put(item.getKey(), String.valueOf((Character) item.getValue()));
                } else {
                    goodDocument.put(item.getKey(), item.getValue());
                }
            }

            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            // ownerUserId column
            String jdbcColumnOwnerApplicationUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
            if (attributeRecords.containsKey(jdbcColumnOwnerApplicationUserId)) {
                goodDocument.put(jdbcColumnOwnerApplicationUserId, ownerApplicationUserId);
            }
            String jdbcColumnOptimistic = configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC);
            if (attributeRecords.containsKey(jdbcColumnOptimistic)) {
                goodDocument.put(jdbcColumnOptimistic, 1);
            }
            String jdbcColumnDeleted = configuration.getString(Constants.JDBC_COLUMN_DELETED);
            if (attributeRecords.containsKey(jdbcColumnDeleted)) {
                goodDocument.put(jdbcColumnDeleted, false);
            }
            String jdbcColumnDateCreated = configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED);
            if (attributeRecords.containsKey(jdbcColumnDateCreated)) {
                goodDocument.put(jdbcColumnDateCreated, new Date());
            }

            goodDocument.put(collectionName + "_id", documentId);

            List<String> fields = new LinkedList<>();
            List<Object> values = new LinkedList<>();
            Map<String, Object> eavs = new HashMap<>();
            for (Map.Entry<String, Object> item : goodDocument.entrySet()) {
                Map<String, Object> attributeRecord = attributeRecords.get(item.getKey());
                boolean eav = (boolean) attributeRecord.get(Jdbc.Attribute.EAV);
                if (!eav) {
                    fields.add(item.getKey());
                    values.add(":" + item.getKey());
                } else {
                    eavs.put(item.getKey(), item.getValue());
                }
            }
            String jdbc = "INSERT INTO " + collectionName + "(" + StringUtils.join(fields, ", ") + ") VALUES(" + StringUtils.join(values, ", ") + ")";
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
            template.update(jdbc, goodDocument);
            if (!eavs.isEmpty()) {
                CommonFunction.saveEavAttributes(jdbcTemplate, collectionId, documentId, attributeRecords, eavs);
            }
        }
        return good;
    }
}
