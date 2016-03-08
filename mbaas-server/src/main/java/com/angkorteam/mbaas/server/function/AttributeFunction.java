package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
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
        attributeRecord.setExposed(true);
        attributeRecord.setVirtualAttributeId(virtualRecord.getAttributeId());
        attributeRecord.setJavaType(requestBody.getJavaType());
        if (requestBody.getJavaType().equals(Integer.class.getName()) || requestBody.getJavaType().equals(int.class.getName())
                || requestBody.getJavaType().equals(Byte.class.getName()) || requestBody.getJavaType().equals(byte.class.getName())
                || requestBody.getJavaType().equals(Short.class.getName()) || requestBody.getJavaType().equals(short.class.getName())
                || requestBody.getJavaType().equals(Long.class.getName()) || requestBody.getJavaType().equals(long.class.getName())
                ) {
            attributeRecord.setSqlType("INT");

        } else if (requestBody.getJavaType().equals(Double.class.getName()) || requestBody.getJavaType().equals(double.class.getName())
                || requestBody.getJavaType().equals(Float.class.getName()) || requestBody.getJavaType().equals(float.class.getName())) {
            attributeRecord.setSqlType("DECIMAL");
        } else if (requestBody.getJavaType().equals(Boolean.class.getName()) || requestBody.getJavaType().equals(boolean.class.getName())) {
            attributeRecord.setSqlType("BIT");
        } else if (requestBody.getJavaType().equals(Date.class.getName()) || requestBody.getJavaType().equals(Time.class.getName()) || requestBody.getJavaType().equals(Timestamp.class.getName())) {
            attributeRecord.setSqlType("DATETIME");
        } else if (requestBody.getJavaType().equals(Character.class.getName()) || requestBody.getJavaType().equals(char.class.getName())
                || requestBody.getJavaType().equals(String.class.getName())) {
            attributeRecord.setSqlType("VARCHAR");
        }
        attributeRecord.store();
        return attributeId;
    }
}
