package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.Jdbc;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeFunction {

    public static void deleteAttribute(JdbcTemplate jdbcTemplate, CollectionAttributeDeleteRequest requestBody) {
        Map<String, Object> collectionRecord = null;
        collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.NAME + " = ?", requestBody.getCollectionName());

        Map<String, Object> attributeRecord = null;
        attributeRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.NAME + " = ? AND " + Jdbc.Attribute.COLLECTION_ID + " = ?", requestBody.getAttributeName(), collectionRecord.get(Jdbc.Collection.COLLECTION_ID));

        if (!(boolean) attributeRecord.get(Jdbc.Attribute.EAV)) {
            jdbcTemplate.execute("ALTER TABLE `" + requestBody.getCollectionName() + "` DROP COLUMN `" + requestBody.getAttributeName() + "`");
        }
        jdbcTemplate.update("DELETE FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.ATTRIBUTE_ID + " = ?", attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_ID));
    }

    public static boolean createAttribute(Schema schema, JdbcTemplate jdbcTemplate, String applicationCode, String attributeId, CollectionAttributeCreateRequest requestBody) {
        Map<String, Object> collectionRecord = null;
        collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.NAME + " = ?", requestBody.getCollectionName());

        boolean good = collectionRecord != null;
        if (good) {
            Table table = schema.getTable(requestBody.getCollectionName());
            good = !table.hasColumn(requestBody.getAttributeName());
        }
        if (good) {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.NAME + " = ?", int.class, collectionRecord.get(Jdbc.Collection.COLLECTION_ID), requestBody.getAttributeName());
            good = count <= 0;
        }
        if (good) {
            int extra = 0;

            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(requestBody.getAttributeType());

            if (!requestBody.isEav()) {
                String jdbc;
                if (attributeType == AttributeTypeEnum.Text
                        || attributeType == AttributeTypeEnum.String) {
                    if (requestBody.getLength() == null || "".equals(requestBody.getLength())) {
                        jdbc = "ALTER TABLE " + collectionRecord.get(Jdbc.Collection.NAME) + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + " , ADD FULLTEXT (`" + requestBody.getAttributeName() + "`);";
                    } else {
                        jdbc = "ALTER TABLE " + collectionRecord.get(Jdbc.Collection.NAME) + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + " , ADD FULLTEXT (`" + requestBody.getAttributeName() + "`);";
                    }
                } else {
                    if (requestBody.getLength() == null || "".equals(requestBody.getLength())) {
                        jdbc = "ALTER TABLE " + collectionRecord.get(Jdbc.Collection.NAME) + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + " , ADD INDEX (`" + requestBody.getAttributeName() + "`);";
                    } else {
                        jdbc = "ALTER TABLE " + collectionRecord.get(Jdbc.Collection.NAME) + " ADD `" + requestBody.getAttributeName() + "` " + attributeType.getSqlType() + "(" + requestBody.getLength() + ")" + " , ADD INDEX (`" + requestBody.getAttributeName() + "`);";
                    }
                }
                jdbcTemplate.execute(jdbc);
            }

            if (requestBody.isNullable()) {
                extra = extra | AttributeExtraEnum.NULLABLE;
            }
            extra = extra | AttributeExtraEnum.EXPOSED;
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Attribute.ATTRIBUTE_ID, attributeId);
            fields.put(Jdbc.Attribute.EXTRA, extra);
            fields.put(Jdbc.Attribute.APPLICATION_CODE, applicationCode);
            fields.put(Jdbc.Attribute.DATE_CREATED, new Date());
            fields.put(Jdbc.Attribute.COLLECTION_ID, collectionRecord.get(Jdbc.Collection.COLLECTION_ID));
            fields.put(Jdbc.Attribute.NAME, requestBody.getAttributeName());
            fields.put(Jdbc.Attribute.VISIBILITY, VisibilityEnum.Hided.getLiteral());
            fields.put(Jdbc.Attribute.ATTRIBUTE_TYPE, attributeType.getLiteral());
            fields.put(Jdbc.Attribute.EAV, requestBody.isEav());
            fields.put(Jdbc.Attribute.SYSTEM, false);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.ATTRIBUTE);
            jdbcInsert.execute(fields);
        }
        return good;
    }
}
