package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeFunction {

    public static void deleteAttribute(DSLContext context, JdbcTemplate jdbcTemplate, CollectionAttributeDeleteRequest requestBody) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(requestBody.getAttributeName())).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);

        if (attributeRecord.getVirtual()) {
            AttributeRecord virtualRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeRecord.getVirtualAttributeId())).fetchOneInto(attributeTable);
            jdbcTemplate.execute("UPDATE `" + requestBody.getCollectionName() + "`" + " SET " + virtualRecord.getName() + " = " + MariaDBFunction.columnDelete(virtualRecord.getName(), requestBody.getAttributeName()));
        } else {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        attributeRecord.delete();
    }

    public static String createAttribute(DSLContext context, CollectionAttributeCreateRequest requestBody) {

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        AttributeRecord virtualRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.NAME.eq(configuration.getString(Constants.JDBC_COLUMN_EXTRA))).and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchOneInto(attributeTable);

        String attributeId = UUID.randomUUID().toString();

        AttributeRecord attributeRecord = context.newRecord(attributeTable);
        attributeRecord.setAttributeId(attributeId);
        attributeRecord.setNullable(requestBody.isNullable());
        attributeRecord.setCollectionId(collectionRecord.getCollectionId());
        attributeRecord.setVirtual(true);
        attributeRecord.setSystem(false);
        attributeRecord.setName(requestBody.getAttributeName());
        attributeRecord.setAutoIncrement(false);
        attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
        attributeRecord.setExposed(true);
        attributeRecord.setVirtualAttributeId(virtualRecord.getAttributeId());
        attributeRecord.setJavaType(requestBody.getJavaType());
        if (requestBody.getJavaType().equals(AttributeTypeEnum.Boolean.getLiteral())) {
            attributeRecord.setSqlType("BIT");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.Byte.getLiteral())
                || requestBody.getJavaType().equals(AttributeTypeEnum.Short.getLiteral())
                || requestBody.getJavaType().equals(AttributeTypeEnum.Integer.getLiteral())
                || requestBody.getJavaType().equals(AttributeTypeEnum.Long.getLiteral())
                ) {
            attributeRecord.setSqlType("INT");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.Float.getLiteral())
                || requestBody.getJavaType().equals(AttributeTypeEnum.Double.getLiteral())) {
            attributeRecord.setSqlType("DECIMAL");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.Character.getLiteral())
                || requestBody.getJavaType().equals(AttributeTypeEnum.String.getLiteral())) {
            attributeRecord.setSqlType("VARCHAR");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.Time.getLiteral())) {
            attributeRecord.setSqlType("TIME");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.Date.getLiteral())) {
            attributeRecord.setSqlType("DATE");
        } else if (requestBody.getJavaType().equals(AttributeTypeEnum.DateTime.getLiteral())) {
            attributeRecord.setSqlType("DATETIME");
        }
        attributeRecord.store();
        return attributeId;
    }
}
