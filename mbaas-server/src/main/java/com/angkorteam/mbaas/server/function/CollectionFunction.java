package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.IndexAttributeTable;
import com.angkorteam.mbaas.model.entity.tables.InstanceAttributeTable;
import com.angkorteam.mbaas.model.entity.tables.PrimaryAttributeTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.IndexAttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PrimaryAttributeRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionFunction {

    public static void deleteCollection(CollectionDeleteRequest requestBody) {
        DSLContext context = Spring.getBean(DSLContext.class);
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        IndexAttributeTable indexAttributeTable = Tables.INDEX_ATTRIBUTE.as("indexAttributeTable");
        PrimaryAttributeTable primaryAttributeTable = Tables.PRIMARY_ATTRIBUTE.as("primaryAttributeTable");
        InstanceAttributeTable instanceAttributeTable = Tables.INSTANCE_ATTRIBUTE.as("instanceAttributeTable");
        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(CollectionPojo.class);
        if (collection != null) {
            context.delete(attributeTable).where(attributeTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
            context.delete(indexAttributeTable).where(indexAttributeTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
            context.delete(primaryAttributeTable).where(primaryAttributeTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
            context.delete(instanceAttributeTable).where(instanceAttributeTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
            context.delete(collectionTable).where(collectionTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
            jdbcTemplate.execute("drop table `" + collection.getName() + "`");
        }
    }

    public static void createCollection(CollectionCreateRequest requestBody) {
        String primaryName = requestBody.getCollectionName() + "_id";
        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE `").append(requestBody.getCollectionName()).append("` (");
        buffer.append("`").append(primaryName).append("` VARCHAR(100) NOT NULL, ");
        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            TypeEnum attributeType = TypeEnum.valueOf(attribute.getType());
            buffer.append("`").append(attribute.getName()).append("` " + attributeType.getSqlType() + ", ");
            buffer.append(attribute.getIndex() + "(`").append(attribute.getName()).append("`), ");
        }
        buffer.append("PRIMARY KEY (`").append(primaryName).append("`)");
        buffer.append(" )");
        DSLContext context = Spring.getBean(DSLContext.class);
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        System system = Spring.getBean(System.class);
        jdbcTemplate.execute(buffer.toString());
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        String collectionId = system.randomUUID();
        {
            CollectionRecord collectionRecord = context.newRecord(collectionTable);
            collectionRecord.setCollectionId(collectionId);
            collectionRecord.setName(requestBody.getCollectionName());
            collectionRecord.setSystem(false);
            collectionRecord.setLocked(false);
            collectionRecord.setMutable(false);
            collectionRecord.store();
        }

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        PrimaryAttributeTable primaryAttributeTable = Tables.PRIMARY_ATTRIBUTE.as("primaryAttributeTable");
        IndexAttributeTable indexAttributeTable = Tables.INDEX_ATTRIBUTE.as("indexAttributeTable");
        {
            String attributeId = system.randomUUID();
            AttributeRecord attributeRecord = context.newRecord(attributeTable);
            attributeRecord.setAttributeId(attributeId);
            attributeRecord.setCollectionId(collectionId);
            attributeRecord.setName(primaryName);
            attributeRecord.setSystem(true);
            attributeRecord.setAllowNull(false);
            attributeRecord.setEav(false);
            attributeRecord.setType(TypeEnum.String.getLiteral());
            attributeRecord.setLength(100);
            attributeRecord.setOrder(1);
            attributeRecord.setPrecision(0);
            attributeRecord.store();

            PrimaryAttributeRecord primaryAttributeRecord = context.newRecord(primaryAttributeTable);
            primaryAttributeRecord.setPrimaryAttributeId(system.randomUUID());
            primaryAttributeRecord.setCollectionId(collectionId);
            primaryAttributeRecord.setAttributeId(attributeId);
            primaryAttributeRecord.store();
        }

        for (CollectionCreateRequest.Attribute attribute : requestBody.getAttributes()) {
            String attributeId = system.randomUUID();
            AttributeRecord attributeRecord = context.newRecord(attributeTable);
            attributeRecord.setAttributeId(attributeId);
            attributeRecord.setCollectionId(collectionId);
            attributeRecord.setName(attribute.getName());
            if (attributeRecord.getName().equals("system")) {
                attributeRecord.setSystem(true);
            } else {
                attributeRecord.setSystem(false);
            }
            attributeRecord.setAllowNull(attribute.isNullable());
            attributeRecord.setEav(false);
            attributeRecord.setType(attribute.getType());
            attributeRecord.setLength(attribute.getLength());
            attributeRecord.setOrder(attribute.getOrder());
            attributeRecord.setPrecision(attribute.getPrecision());
            attributeRecord.store();

            IndexAttributeRecord indexAttributeRecord = context.newRecord(indexAttributeTable);
            indexAttributeRecord.setIndexAttributeId(system.randomUUID());
            indexAttributeRecord.setCollectionId(collectionId);
            indexAttributeRecord.setAttributeId(attributeId);
            indexAttributeRecord.setName(attribute.getIndex() + "_" + system.randomUUID());
            indexAttributeRecord.setType(attribute.getIndex());
            indexAttributeRecord.store();
        }
    }
}
