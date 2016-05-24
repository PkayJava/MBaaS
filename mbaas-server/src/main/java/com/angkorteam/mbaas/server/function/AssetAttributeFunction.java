package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/9/16.
 */
public class AssetAttributeFunction {

    public static String createAttribute(DSLContext context, CollectionAttributeCreateRequest requestBody) {
//        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("UserPrivacyTable");
//        String attributeId = AttributeFunction.createAttribute(context, requestBody);
//        UserPrivacyRecord userPrivacyRecord = context.newRecord(userPrivacyTable);
//        String uuid = UUID.randomUUID().toString();
//        userPrivacyRecord.setUserPrivacyId(uuid);
//        userPrivacyRecord.setAttributeId(attributeId);
//        userPrivacyRecord.setUserId(userId);
//        userPrivacyRecord.setScope(scope.getLiteral());
//        userPrivacyRecord.store();
//        return attributeId;
        return null;
    }

    public static void deleteAttribute(DSLContext context, JdbcTemplate jdbcTemplate, CollectionAttributeDeleteRequest requestBody) {
//        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
//        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
//        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");
//        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(requestBody.getCollectionName())).fetchOneInto(collectionTable);
//        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).and(attributeTable.NAME.eq(requestBody.getAttributeName())).fetchOneInto(attributeTable);
//        AttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);
//        context.delete(userPrivacyTable).where(userPrivacyTable.ATTRIBUTE_ID.eq(attributeRecord.getAttributeId())).execute();
    }

}
