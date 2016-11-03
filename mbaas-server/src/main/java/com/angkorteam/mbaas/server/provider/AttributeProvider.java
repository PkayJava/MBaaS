package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeProvider extends JooqProvider {

    private AttributeTable attributeTable;

    private TableLike<?> from;

    private String collectionId;

    public AttributeProvider(String collectionId) {
        this.attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        this.collectionId = collectionId;
        this.from = this.attributeTable;
    }

    public Field<String> getName() {
        return this.attributeTable.NAME;
    }

    public Field<String> getAttributeId() {
        return this.attributeTable.ATTRIBUTE_ID;
    }

    public Field<String> getType() {
        return this.attributeTable.TYPE;
    }

    public Field<Boolean> getSystem() {
        return this.attributeTable.SYSTEM;
    }

    public Field<Integer> getLength() {
        return this.attributeTable.LENGTH;
    }

    public Field<Integer> getPrecision() {
        return this.attributeTable.PRECISION;
    }

    public Field<Integer> getOrder() {
        return this.attributeTable.ORDER;
    }

    public Field<Boolean> getEav() {
        return this.attributeTable.EAV;
    }

    public Field<Boolean> getNullable() {
        return this.attributeTable.ALLOW_NULL;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.attributeTable.COLLECTION_ID.eq(this.collectionId));
        return where;
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
