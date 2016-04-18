package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeProvider extends JooqProvider {

    private AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

    private TableLike<?> from;

    private String collectionId;

    public AttributeProvider(String collectionId) {
        this.collectionId = collectionId;
        this.from = attributeTable;
    }

    public Field<String> getName() {
        return this.attributeTable.NAME;
    }

    public Field<String> getAttributeId() {
        return this.attributeTable.ATTRIBUTE_ID;
    }

    public Field<String> getAttributeType() {
        return this.attributeTable.ATTRIBUTE_TYPE;
    }

    public Field<Boolean> getSystem() {
        return this.attributeTable.SYSTEM;
    }

    public Field<Integer> getExtra() {
        return this.attributeTable.EXTRA;
    }

    public Field<String> getVisibility() {
        return this.attributeTable.VISIBILITY;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(attributeTable.COLLECTION_ID.eq(this.collectionId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
