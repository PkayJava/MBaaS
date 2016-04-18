package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.EavTextRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentFunction {

    public static void deleteDocument(DSLContext context, JdbcTemplate jdbcTemplate, String collection, String documentId) {
        jdbcTemplate.update("DELETE FROM `" + collection + "` WHERE " + collection + "_id = ?", documentId);
    }

    public static boolean modifyDocument(DSLContext context, JdbcTemplate jdbcTemplate, String collection, String documentId, DocumentModifyRequest requestBody) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();

        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributeRecords, requestBody.getDocument());

        Map<String, Object> goodDocument = new HashMap<>();
        if (good) {
            // checkData Type
            good = CommonFunction.checkDataTypes(attributeRecords, requestBody.getDocument(), goodDocument);
        }

        if (good) {
            List<String> columns = new LinkedList<>();
            Map<String, Object> values = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : goodDocument.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                columns.add(entry.getKey() + " = :" + entry.getKey());
                values.put(entry.getKey(), entry.getValue());
            }
            values.put(collection + "_id", documentId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            namedParameterJdbcTemplate.update("UPDATE `" + collectionRecord.getName() + "` SET " + StringUtils.join(columns, ", ") + " WHERE " + collection + "_id = :" + collection + "_id", values);
        }
        return good;
    }

    public static boolean insertDocument(DSLContext context, JdbcTemplate jdbcTemplate, String ownerUserId, String documentId, String collection, DocumentCreateRequest requestBody) {

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
            attributeRecords.put(attributeRecord.getName(), attributeRecord);
        }

        // ensureField
        boolean good = CommonFunction.ensureAttributes(attributeRecords, requestBody.getDocument());

        Map<String, Object> goodDocument = new HashMap<>();
        if (good) {
            // checkData Type
            good = CommonFunction.checkDataTypes(attributeRecords, requestBody.getDocument(), goodDocument);
        }
        if (good) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            // ownerUserId column
            String jdbcColumnOwnerUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
            if (attributeRecords.containsKey(jdbcColumnOwnerUserId)) {
                goodDocument.put(jdbcColumnOwnerUserId, ownerUserId);
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

            goodDocument.put(collection + "_id", documentId);

            List<String> fields = new LinkedList<>();
            List<Object> values = new LinkedList<>();
            Map<String, Object> eavs = new HashMap<>();
            for (Map.Entry<String, Object> item : goodDocument.entrySet()) {
                AttributeRecord attributeRecord = attributeRecords.get(item.getKey());
                if (!attributeRecord.getEav()) {
                    fields.add(item.getKey());
                    values.add(":" + item.getKey());
                } else {
                    eavs.put(item.getKey(), item.getValue());
                }
            }
            String jdbc = "INSERT INTO " + collection + "(" + StringUtils.join(fields, ",") + ") VALUES(" + StringUtils.join(values, ",") + ")";
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
            template.update(jdbc, goodDocument);
            if (!eavs.isEmpty()) {
                CommonFunction.saveEavAttributes(collectionRecord.getCollectionId(), documentId, context, attributeRecords, eavs);
            }
        }
        return good;
    }
}
