package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionFunction {

    public static void deleteCollection(DSLContext context, JdbcTemplate jdbcTemplate, CollectionDeleteRequest requestBody) {

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("CollectionROlePrivacyTable");
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);

        if (collectionRecord != null) {
            context.delete(attributeTables).where(attributeTables.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionUserPrivacyTable).where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionRolePrivacyTable).where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(documentUserPrivacyTable).where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(documentRolePrivacyTable).where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            jdbcTemplate.execute("DROP TABLE `" + requestBody.getCollectionName() + "`");
        }
    }

    public static void createCollection(DSLContext context, JdbcTemplate jdbcTemplate, String applicationId, String ownerUserId, CollectionCreateRequest requestBody) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String primaryName = requestBody.getCollectionName() + "_id";

        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("` INT(11) NOT NULL DEFAULT 0, ");
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
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID)).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED)).append("` DATETIME NOT NULL DEFAULT NOW(), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID)).append("`), ");
        buffer.append("PRIMARY KEY (`").append(primaryName).append("`)");
        buffer.append(" )");
        jdbcTemplate.execute(buffer.toString());

        CollectionRecord collectionRecord = context.newRecord(collectionTable);
        collectionRecord.setCollectionId(UUID.randomUUID().toString());
        collectionRecord.setName(requestBody.getCollectionName());
        collectionRecord.setSystem(false);
        collectionRecord.setLocked(true);
        collectionRecord.setApplicationId(applicationId);
        collectionRecord.setOwnerUserId(ownerUserId);
        collectionRecord.store();

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(primaryName);
            attributeRecord.setExtra(AttributeExtraEnum.PRIMARY | AttributeExtraEnum.AUTO_INCREMENT | AttributeExtraEnum.EXPOSED);
            attributeRecord.setVisibility(VisibilityEnum.Shown.getLiteral());
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setAttributeType(AttributeTypeEnum.String.getLiteral());
            attributeRecord.setSystem(true);
            attributeRecord.setEav(false);
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
            attributeRecord.setExtra(0);
            attributeRecord.setSystem(true);
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setEav(false);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED));
            attributeRecord.setExtra(AttributeExtraEnum.EXPOSED);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAttributeType(AttributeTypeEnum.DateTime.getLiteral());
            attributeRecord.setSystem(true);
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setEav(false);
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DELETED));
            attributeRecord.setExtra(0);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAttributeType(AttributeTypeEnum.Boolean.getLiteral());
            attributeRecord.setSystem(true);
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setEav(false);
            attributeRecord.store();
        }
        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setExtra(0);
            attributeRecord.setSystem(true);
            attributeRecord.setEav(false);
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setAttributeType(AttributeTypeEnum.String.getLiteral());
            attributeRecord.store();
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attribute.getAttributeType());
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(attribute.getName());
            attributeRecord.setSystem(false);
            attributeRecord.setApplicationId(applicationId);
            attributeRecord.setEav(false);
            if (attribute.isNullable()) {
                attributeRecord.setExtra(AttributeExtraEnum.NULLABLE | AttributeExtraEnum.EXPOSED);
            } else {
                attributeRecord.setExtra(AttributeExtraEnum.EXPOSED);
            }
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAttributeType(attributeType.getLiteral());
            attributeRecord.store();
        }

        context.update(collectionTable).set(collectionTable.LOCKED, false).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
    }
}
