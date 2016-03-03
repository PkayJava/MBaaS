package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PrimaryRecord;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionFunction {

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
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11), ");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` DECIMAL(15,4), ");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` BIT(1), ");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` DATETIME, ");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                buffer.append("`").append(attribute.getName()).append("` VARCHAR(255), ");
            }
        }
        buffer.append("`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("` VARCHAR(100) NOT NULL, ");
        buffer.append("`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("` BIT(1) NOT NULL DEFAULT 0, ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_OPTIMISTIC)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_COLUMN_DELETED)).append("`), ");
        buffer.append("INDEX(`").append(configuration.getString(Constants.JDBC_OWNER_USER_ID)).append("`), ");
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
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(String.class.getName());
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
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Byte.class.getName() + "[]");
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
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Integer.class.getName());
            attributeRecord.setSqlType("INT");
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
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(Boolean.class.getName());
            attributeRecord.setSqlType("BIT");
            attributeRecord.store();
        }
        {
            AttributeRecord attributeRecord = context.newRecord(attributeTables);
            attributeRecord.setAttributeId(UUID.randomUUID().toString());
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(configuration.getString(Constants.JDBC_OWNER_USER_ID));
            attributeRecord.setNullable(false);
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setSystem(true);
            attributeRecord.setExposed(false);
            attributeRecord.setJavaType(String.class.getName());
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
            attributeRecord.setAutoIncrement(false);
            attributeRecord.setVirtual(false);
            attributeRecord.setExposed(true);
            attributeRecord.setJavaType(attribute.getJavaType());
            if (attribute.getJavaType().equals(Integer.class.getName()) || attribute.getJavaType().equals(int.class.getName())
                    || attribute.getJavaType().equals(Byte.class.getName()) || attribute.getJavaType().equals(byte.class.getName())
                    || attribute.getJavaType().equals(Short.class.getName()) || attribute.getJavaType().equals(short.class.getName())
                    || attribute.getJavaType().equals(Long.class.getName()) || attribute.getJavaType().equals(long.class.getName())
                    ) {
                buffer.append("`").append(attribute.getName()).append("` INT(11)");
                attributeRecord.setSqlType("INT");
            } else if (attribute.getJavaType().equals(Double.class.getName()) || attribute.getJavaType().equals(double.class.getName())
                    || attribute.getJavaType().equals(Float.class.getName()) || attribute.getJavaType().equals(float.class.getName())) {
                attributeRecord.setSqlType("DECIMAL");
            } else if (attribute.getJavaType().equals(Boolean.class.getName()) || attribute.getJavaType().equals(boolean.class.getName())) {
                attributeRecord.setSqlType("BIT");
            } else if (attribute.getJavaType().equals(Date.class.getName()) || attribute.getJavaType().equals(Time.class.getName()) || attribute.getJavaType().equals(Timestamp.class.getName())) {
                attributeRecord.setSqlType("DATETIME");
            } else if (attribute.getJavaType().equals(Character.class.getName()) || attribute.getJavaType().equals(char.class.getName())
                    || attribute.getJavaType().equals(String.class.getName())) {
                attributeRecord.setSqlType("VARCHAR");
            }
            attributeRecord.store();
        }

        context.update(collectionTable).set(collectionTable.LOCKED, false).where(collectionTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).execute();
    }
}
