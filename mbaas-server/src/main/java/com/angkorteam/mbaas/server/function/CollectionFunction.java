package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionFunction {
    //
//    public static void deleteCollection(DSLContext context, JdbcTemplate jdbcTemplate, CollectionDeleteRequest requestBody) {
//
//        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
//        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
//        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("CollectionROlePrivacyTable");
//        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
//        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
//        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");
//
//        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
//
//        if (collectionRecord != null) {
//            context.delete(attributeTables).where(attributeTables.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            context.delete(collectionUserPrivacyTable).where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            context.delete(collectionRolePrivacyTable).where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            context.delete(documentUserPrivacyTable).where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            context.delete(documentRolePrivacyTable).where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            context.delete(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
//            jdbcTemplate.execute("DROP TABLE `" + requestBody.getCollectionName() + "`");
//        }
//    }
//
    public static void createCollection(JdbcTemplate jdbcTemplate, String applicationCode, String ownerApplicationUserId, CollectionCreateRequest requestBody) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String primaryName = requestBody.getCollectionName() + "_id";
        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` VARCHAR(100) NOT NULL, ");
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attribute.getAttributeType());
            if (attributeType == AttributeTypeEnum.Boolean
                    || attributeType == AttributeTypeEnum.Byte
                    || attributeType == AttributeTypeEnum.Short
                    || attributeType == AttributeTypeEnum.Integer
                    || attributeType == AttributeTypeEnum.Long
                    || attributeType == AttributeTypeEnum.Float
                    || attributeType == AttributeTypeEnum.Double
                    || attributeType == AttributeTypeEnum.Time
                    || attributeType == AttributeTypeEnum.Date
                    || attributeType == AttributeTypeEnum.DateTime
                    || attributeType == AttributeTypeEnum.Character
                    ) {
                buffer.append("`").append(attribute.getName()).append("` " + attributeType.getSqlType() + ", ");
                buffer.append("INDEX(`").append(attribute.getName()).append("`), ");
            } else if (attributeType == AttributeTypeEnum.String) {
                buffer.append("`").append(attribute.getName()).append("` " + attributeType.getSqlType() + ", ");
                buffer.append("FULLTEXT(`").append(attribute.getName()).append("`), ");
            } else if (attributeType == AttributeTypeEnum.Text) {
                buffer.append("`").append(attribute.getName()).append("` " + attributeType.getSqlType() + ", ");
                buffer.append("FULLTEXT(`").append(attribute.getName()).append("`), ");
            }
        }
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_OWNER_APPLICATION_USER_ID)).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED)).append("` DATETIME NOT NULL DEFAULT NOW(), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OWNER_APPLICATION_USER_ID)).append("`), ");
        buffer.append("PRIMARY KEY (`").append(primaryName).append("`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());
        String collectionId = UUID.randomUUID().toString();
        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Collection.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Collection.NAME, requestBody.getCollectionName());
            fields.put(Jdbc.Collection.SYSTEM, false);
            fields.put(Jdbc.Collection.LOCKED, true);
            fields.put(Jdbc.Collection.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Collection.OWNER_APPLICATION_USER_ID, ownerApplicationUserId);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.COLLECTION);
            jdbcInsert.execute(fields);
        }

        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Attribute.NAME, primaryName);
            fields.put(Jdbc.Attribute.EXTRA, AttributeExtraEnum.PRIMARY | AttributeExtraEnum.AUTO_INCREMENT | AttributeExtraEnum.EXPOSED);
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Shown.getLiteral());
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, AttributeTypeEnum.String.getLiteral());
            fields.put(Jdbc.Attribute.SYSTEM, true);
            fields.put(Jdbc.Attribute.EAV, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }

        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Attribute.NAME, configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED));
            fields.put(Jdbc.Attribute.EXTRA, AttributeExtraEnum.EXPOSED);
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Hided.getLiteral());
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, AttributeTypeEnum.DateTime.getLiteral());
            fields.put(Jdbc.Attribute.SYSTEM, true);
            fields.put(Jdbc.Attribute.EAV, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }

        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Attribute.NAME, configuration.getString(Constants.JDBC_COLUMN_DELETED));
            fields.put(Jdbc.Attribute.EXTRA, 0);
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Hided.getLiteral());
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, AttributeTypeEnum.Boolean.getLiteral());
            fields.put(Jdbc.Attribute.SYSTEM, true);
            fields.put(Jdbc.Attribute.EAV, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }
        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Attribute.NAME, configuration.getString(Constants.JDBC_COLUMN_OWNER_APPLICATION_USER_ID));
            fields.put(Jdbc.Attribute.EXTRA, 0);
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Hided.getLiteral());
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, AttributeTypeEnum.String.getLiteral());
            fields.put(Jdbc.Attribute.SYSTEM, true);
            fields.put(Jdbc.Attribute.EAV, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attribute.getAttributeType());
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionId);
            fields.put(Jdbc.Attribute.NAME, attribute.getName());
            if (attribute.isNullable()) {
                fields.put(Jdbc.Attribute.EXTRA, AttributeExtraEnum.NULLABLE | AttributeExtraEnum.EXPOSED);
            } else {
                fields.put(Jdbc.Attribute.EXTRA, AttributeExtraEnum.EXPOSED);
            }
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Hided.getLiteral());
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, attributeType.getLiteral());
            fields.put(Jdbc.Attribute.SYSTEM, false);
            fields.put(Jdbc.Attribute.EAV, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }
        jdbcTemplate.update("UPDATE " + Jdbc.COLLECTION + " SET " + Jdbc.Collection.LOCKED + " = ? WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", false, collectionId);
    }
}
