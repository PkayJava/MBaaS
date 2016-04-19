package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeFunction {

    public static void deleteAttribute(DSLContext context, JdbcTemplate jdbcTemplate, CollectionAttributeDeleteRequest requestBody) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);

        if (!attributeRecord.getEav()) {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        attributeRecord.delete();
    }

    public static boolean createAttribute(DSLContext context, JdbcTemplate jdbcTemplate, String attributeId, CollectionAttributeCreateRequest requestBody) {

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);

        boolean good = collectionRecord != null;
        if (good) {
            int count = context.selectCount().from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).and(attributeTable.NAME.eq(requestBody.getAttributeName())).fetchOneInto(int.class);
            good = count <= 0;
        }
        if (good) {
            int extra = 0;

            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(requestBody.getAttributeType());

            if (!requestBody.isEav()) {
                String jdbc;
                if (attributeType == AttributeTypeEnum.Text
                        || attributeType == AttributeTypeEnum.String) {
                    jdbc = "ALTER TABLE " + collectionRecord.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + " , ADD FULLTEXT (`" + requestBody.getAttributeName() + "`);";
                } else {
                    jdbc = "ALTER TABLE " + collectionRecord.getName() + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + " , ADD INDEX (`" + requestBody.getAttributeName() + "`);";
                }
                jdbcTemplate.execute(jdbc);
            }

            AttributeRecord attributeRecord = context.newRecord(attributeTable);
            attributeRecord.setAttributeId(attributeId);
            if (requestBody.isNullable()) {
                extra = extra | AttributeExtraEnum.NULLABLE;
            }
            extra = extra | AttributeExtraEnum.EXPOSED;
            attributeRecord.setExtra(extra);
            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
            attributeRecord.setName(requestBody.getAttributeName());
            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
            attributeRecord.setAttributeType(AttributeTypeEnum.valueOf(requestBody.getAttributeType()).getLiteral());
            attributeRecord.setEav(requestBody.isEav());
            attributeRecord.setSystem(false);
            attributeRecord.setAttributeType(attributeType.getLiteral());
            attributeRecord.store();
        }
        return good;
    }
}
