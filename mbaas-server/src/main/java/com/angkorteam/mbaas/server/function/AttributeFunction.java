package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.IndexAttributeTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.IndexAttributeRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import com.google.common.base.Strings;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeFunction {

    public static void deleteAttribute(CollectionAttributeDeleteRequest requestBody) {
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        IndexAttributeTable indexAttributeTable = Tables.INDEX_ATTRIBUTE.as("indexAttributeTable");
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);

        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(CollectionPojo.class);
        AttributePojo attribute = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collection.getCollectionId())).fetchOneInto(AttributePojo.class);

        if (!attribute.getEav()) {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        context.delete(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attribute.getAttributeId())).execute();
        context.delete(indexAttributeTable).where(indexAttributeTable.ATTRIBUTE_ID.eq(attribute.getAttributeId())).and(indexAttributeTable.COLLECTION_ID.eq(collection.getCollectionId())).execute();
    }

    public static boolean createAttribute(CollectionAttributeCreateRequest requestBody) {
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        IndexAttributeTable indexAttributeTable = Tables.INDEX_ATTRIBUTE.as("indexAttributeTable");
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);

        CollectionPojo collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(CollectionPojo.class);

        boolean good = collection != null;
        if (good) {
            int count = context.selectCount().from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collection.getCollectionId())).fetchOneInto(int.class);
            good = count <= 0;
        }
        if (good) {
            TypeEnum attributeType = TypeEnum.valueOf(requestBody.getType());

            String indexName = null;
            if (!requestBody.isEav()) {
                String jdbc = null;
                String notNull = "";
                if (!requestBody.isNullable()) {
                    notNull = " NOT NULL";
                }
                if (Strings.isNullOrEmpty(requestBody.getIndex())) {
                    if (attributeType == TypeEnum.Text) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + notNull + ";";
                    } else if (attributeType == TypeEnum.Long) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + notNull + ";";
                    } else if (attributeType == TypeEnum.String) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + notNull + ";";
                    } else if (attributeType == TypeEnum.Double) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + "," + requestBody.getPrecision() + ")" + notNull + ";";
                    } else if (attributeType == TypeEnum.Boolean || attributeType == TypeEnum.Character) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(1)" + notNull + ";";
                    } else if (attributeType == TypeEnum.Time || attributeType == TypeEnum.Date || attributeType == TypeEnum.DateTime) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + notNull + ";";
                    }
                } else {
                    String index = null;
                    if (requestBody.getIndex().equals("KEY")) {
                        indexName = "index__" + collection.getName() + "__" + requestBody.getAttributeName();
                        index = "KEY `" + indexName + "` (`" + requestBody.getAttributeName() + "`);";
                    } else if (requestBody.getIndex().equals("UNIQUE KEY")) {
                        indexName = "unique__" + collection.getName() + "__" + requestBody.getAttributeName();
                        index = "UNIQUE KEY `" + indexName + "` (`" + requestBody.getAttributeName() + "`);";
                    } else if (requestBody.getIndex().equals("FULLTEXT KEY")) {
                        indexName = "fulltext__" + collection.getName() + "__" + requestBody.getAttributeName();
                        index = "FULLTEXT KEY `" + indexName + "` (`" + requestBody.getAttributeName() + "`);";
                    }
                    if (attributeType == TypeEnum.Text) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + notNull + " , ADD " + index;
                    } else if (attributeType == TypeEnum.Long) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + notNull + " , ADD " + index;
                    } else if (attributeType == TypeEnum.String) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + notNull + " , ADD " + index;
                    } else if (attributeType == TypeEnum.Double) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + "," + requestBody.getPrecision() + ")" + notNull + " , ADD " + index;
                    } else if (attributeType == TypeEnum.Boolean || attributeType == TypeEnum.Character) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(1)" + notNull + " , ADD " + index;
                    } else if (attributeType == TypeEnum.Time || attributeType == TypeEnum.Date || attributeType == TypeEnum.DateTime) {
                        jdbc = "ALTER TABLE " + collection.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + notNull + " , ADD " + index;
                    }
                }
                if (!Strings.isNullOrEmpty(jdbc)) {
                    jdbcTemplate.execute(jdbc);
                }
            }

            System system = Spring.getBean(System.class);
            String attributeId = system.randomUUID();
            AttributeRecord attributeRecord = context.newRecord(attributeTable);
            attributeRecord.setAttributeId(attributeId);
            attributeRecord.setCollectionId(collection.getCollectionId());
            attributeRecord.setName(requestBody.getAttributeName());
            attributeRecord.setType(attributeType.getLiteral());
            attributeRecord.setEav(requestBody.isEav());
            attributeRecord.setAllowNull(requestBody.isNullable());
            attributeRecord.setSystem(false);
            attributeRecord.setLength(requestBody.getLength());
            attributeRecord.setPrecision(requestBody.getPrecision());
            attributeRecord.setOrder(requestBody.getOrder());
            attributeRecord.store();

            if (!Strings.isNullOrEmpty(requestBody.getIndex())) {
                IndexAttributeRecord indexAttributeRecord = context.newRecord(indexAttributeTable);
                indexAttributeRecord.setIndexAttributeId(system.randomUUID());
                indexAttributeRecord.setType(requestBody.getIndex());
                indexAttributeRecord.setCollectionId(collection.getCollectionId());
                indexAttributeRecord.setAttributeId(attributeId);
                indexAttributeRecord.setName(indexName);
            }
        }
        return good;
    }
}
