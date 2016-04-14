package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PrimaryRecord;
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
        PrimaryTable primaryTable = Tables.PRIMARY.as("primaryTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("CollectionROlePrivacyTable");
        CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
        DocumentUserPrivacyTable documentUserPrivacyTable = Tables.DOCUMENT_USER_PRIVACY.as("documentUserPrivacyTable");
        DocumentRolePrivacyTable documentRolePrivacyTable = Tables.DOCUMENT_ROLE_PRIVACY.as("documentRolePrivacyTable");
        IndexTable indexTable = Tables.INDEX.as("indexTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);

        if (collectionRecord != null) {
            context.delete(attributeTables).where(attributeTables.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(primaryTable).where(primaryTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(indexTable).where(indexTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionUserPrivacyTable).where(collectionUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionRolePrivacyTable).where(collectionRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(documentUserPrivacyTable).where(documentUserPrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(documentRolePrivacyTable).where(documentRolePrivacyTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            context.delete(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
            jdbcTemplate.execute("DROP TABLE `" + requestBody.getCollectionName() + "`");
        }
    }

    public static void createCollection(DSLContext context, JdbcTemplate jdbcTemplate, String userId, CollectionCreateRequest requestBody) {
        PrimaryTable primaryTable = Tables.PRIMARY.as("primaryTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTables = Tables.ATTRIBUTE.as("attributeTables");
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        String primaryName = requestBody.getCollectionName() + "_id";

        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_EXTRA)).append("` BLOB, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("` INT(11) NOT NULL DEFAULT 0, ");
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            if (attribute.getJavaType().equals(AttributeTypeEnum.Boolean.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` BIT(1), ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Byte.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Short.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Integer.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Long.getLiteral())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11), ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Float.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Double.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` DECIMAL(15,4), ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Character.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.String.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` VARCHAR(255), ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Time.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` TIME, ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Date.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` DATE, ");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.DateTime.getLiteral())) {
                buffer.append("`").append(attribute.getName()).append("` DATETIME, ");
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
        collectionRecord.setOwnerUserId(userId);
        collectionRecord.store();

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(primaryName);
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(true);
            attributeRecord.setSystem(true);
            attributeRecord.setVirtual(false);
            attributeRecord.setVisibility(VisibilityEnum.Shown.getLiteral());
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(AttributeTypeEnum.String.getLiteral());
            attributeRecord.setSqlType("VARCHAR");
            attributeRecord.store();

            PrimaryRecord primaryRecord = context.newRecord(primaryTable);
            primaryRecord.setAttributeId(attributeRecord.getAttributeId());
            primaryRecord.setCollectionId(collectionRecord.getCollectionId());
            primaryRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_EXTRA));
            attributeRecord.setNullable(true);
            attributeRecord.setSystem(true);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(AttributeTypeEnum.Blob.getLiteral());
            attributeRecord.setSqlType("BLOB");
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(AttributeTypeEnum.Integer.getLiteral());
            attributeRecord.setSqlType("INT");
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DATE_CREATED));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(AttributeTypeEnum.DateTime.getLiteral());
            attributeRecord.setSqlType("DATETIME");
            attributeRecord.store();
        }

        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_DELETED));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(AttributeTypeEnum.Boolean.getLiteral());
            attributeRecord.setSqlType("BIT");
            attributeRecord.store();
        }
        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(AttributeTypeEnum.String.getLiteral());
            attributeRecord.setSqlType("VARCHAR");
            attributeRecord.store();
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(attribute.getName());
            attributeRecord.setNullable(attribute.isNullable());
            attributeRecord.setSystem(false);
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(attribute.getJavaType());
            if (attribute.getJavaType().equals(AttributeTypeEnum.Boolean.getLiteral())) {
                attributeRecord.setSqlType("BIT");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Byte.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Short.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Integer.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Long.getLiteral())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11)");
                attributeRecord.setSqlType("INT");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Float.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.Double.getLiteral())) {
                attributeRecord.setSqlType("DECIMAL");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Character.getLiteral())
                    || attribute.getJavaType().equals(AttributeTypeEnum.String.getLiteral())) {
                attributeRecord.setSqlType("VARCHAR");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Time.getLiteral())) {
                attributeRecord.setSqlType("TIME");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.Date.getLiteral())) {
                attributeRecord.setSqlType("DATE");
            } else if (attribute.getJavaType().equals(AttributeTypeEnum.DateTime.getLiteral())) {
                attributeRecord.setSqlType("DATETIME");
            }
            attributeRecord.store();
        }

        context.update(collectionTable).set(collectionTable.LOCKED, false).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
    }
}
