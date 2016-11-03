package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/4/16.
 */
public class DocumentProvider extends JooqProvider {

    private TableLike<?> from;

    private CollectionPojo collection;

    public DocumentProvider(String collectionId) {
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        List<AttributePojo> attributes = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionId)).fetchInto(AttributePojo.class);

        Table<Record> table = DSL.table(this.collection.getName());
        String documentIdField = this.collection.getName() + "." + this.collection.getName() + "_id";

        for (AttributePojo attribute : attributes) {
            TypeEnum attributeType = TypeEnum.valueOf(attribute.getType());
            if (attribute.getEav()) {
                if (TypeEnum.Boolean == attributeType) {
                    Table<?> joinTable = DSL.table(Tables.EAV_BOOLEAN.getName()).as("join_" + attribute.getName());
                    Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                    Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                    Field<Boolean> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Boolean.class).as(attribute.getName());
                    table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                    boardField(attribute.getName(), joinEavValue);
                } else if (TypeEnum.Long == attributeType) {
                    Table<?> joinTable = DSL.table(Tables.EAV_INTEGER.getName()).as("join_" + attribute.getName());
                    Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                    Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                    Field<Integer> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Integer.class).as(attribute.getName());
                    table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                    boardField(attribute.getName(), joinEavValue);
                } else if (TypeEnum.Double == attributeType) {
                    Table<?> joinTable = DSL.table(Tables.EAV_DECIMAL.getName()).as("join_" + attribute.getName());
                    Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                    Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                    Field<Double> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Double.class).as(attribute.getName());
                    table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                    boardField(attribute.getName(), joinEavValue);
                } else if (TypeEnum.Character == attributeType) {
                    Table<?> joinTable = DSL.table(Tables.EAV_CHARACTER.getName()).as("join_" + attribute.getName());
                    Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                    Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                    Field<String> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", String.class).as(attribute.getName());
                    table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                    boardField(attribute.getName(), joinEavValue);
                } else if (TypeEnum.String == attributeType || TypeEnum.Text == attributeType) {
                    Table<?> joinTable = DSL.table(Tables.EAV_VARCHAR.getName()).as("join_" + attribute.getName());
                    Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                    Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                    Field<String> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", String.class).as(attribute.getName());
                    table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                    boardField(attribute.getName(), joinEavValue);
                } else if (TypeEnum.Time == attributeType
                        || TypeEnum.Date == attributeType
                        || TypeEnum.DateTime == attributeType) {
                    if (TypeEnum.Time == attributeType) {
                        Table<?> joinTable = DSL.table(Tables.EAV_TIME.getName()).as("join_" + attribute.getName());
                        Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                        Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                        Field<Date> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Date.class).as(attribute.getName());
                        table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                        boardField(attribute.getName(), joinEavValue);
                    } else if (TypeEnum.Date == attributeType) {
                        Table<?> joinTable = DSL.table(Tables.EAV_DATE.getName()).as("join_" + attribute.getName());
                        Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                        Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                        Field<Date> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Date.class).as(attribute.getName());
                        table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                        boardField(attribute.getName(), joinEavValue);
                    } else if (TypeEnum.DateTime == attributeType) {
                        Table<?> joinTable = DSL.table(Tables.EAV_DATE_TIME.getName()).as("join_" + attribute.getName());
                        Field<String> joinDocumentId = DSL.field(joinTable.getName() + "." + "document_id", String.class);
                        Field<String> joinAttributeId = DSL.field(joinTable.getName() + "." + "attribute_id", String.class);
                        Field<Date> joinEavValue = DSL.field(joinTable.getName() + "." + "eav_value", Date.class).as(attribute.getName());
                        table = table.leftJoin(joinTable).on(DSL.field(documentIdField).eq(joinDocumentId)).and(joinAttributeId.eq(attribute.getAttributeId()));
                        boardField(attribute.getName(), joinEavValue);
                    }
                }
            } else {
                if (TypeEnum.Boolean == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), Boolean.class));
                } else if (TypeEnum.Long == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), Long.class));
                } else if (TypeEnum.Double == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), Double.class));
                } else if (TypeEnum.Character == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), String.class));
                } else if (TypeEnum.String == attributeType || TypeEnum.Text == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), String.class));
                } else if (TypeEnum.Time == attributeType
                        || TypeEnum.Date == attributeType
                        || TypeEnum.DateTime == attributeType) {
                    boardField(attribute.getName(), DSL.field(this.collection.getName() + "." + attribute.getName(), Date.class));
                }
            }
        }

        this.from = table;
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
