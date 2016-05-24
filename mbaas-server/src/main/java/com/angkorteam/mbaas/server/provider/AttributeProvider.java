package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeProvider extends JooqProvider {

    private Table<?> attributeTable;

    private TableLike<?> from;

    private String collectionId;

    private final String applicationCode;

    public AttributeProvider(String applicationCode, String collectionId) {
        this.applicationCode = applicationCode;
        this.attributeTable = DSL.table(Jdbc.ATTRIBUTE).as("attributeTable");
        this.collectionId = collectionId;
        this.from = attributeTable;
    }

    public Field<String> getName() {
        return this.attributeTable.field(Jdbc.Attribute.NAME, String.class);
    }

    public Field<String> getAttributeId() {
        return this.attributeTable.field(Jdbc.Attribute.ATTRIBUTE_ID, String.class);
    }

    public Field<String> getAttributeType() {
        return this.attributeTable.field(Jdbc.Attribute.ATTRIBUTE_TYPE, String.class);
    }

    public Field<Boolean> getSystem() {
        return this.attributeTable.field(Jdbc.Attribute.SYSTEM, Boolean.class);
    }

    public Field<Integer> getExtra() {
        return this.attributeTable.field(Jdbc.Attribute.EXTRA, Integer.class);
    }

    public Field<String> getVisibility() {
        return this.attributeTable.field(Jdbc.Attribute.VISIBILITY, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(attributeTable.field(Jdbc.Attribute.COLLECTION_ID, String.class).eq(this.collectionId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
