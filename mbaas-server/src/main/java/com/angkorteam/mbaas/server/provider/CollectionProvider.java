package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.collect.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionProvider extends JooqProvider {

    private TableLike<?> from;

    private CollectionTable collectionTable;

    public CollectionProvider() {
        this.collectionTable = Tables.COLLECTION.as("collectionTable");
        this.from = this.collectionTable;
        setSort("name", SortOrder.ASCENDING);
    }

    public Field<Boolean> getSystem() {
        return this.collectionTable.SYSTEM;
    }

    public Field<String> getName() {
        return this.collectionTable.NAME;
    }

    public Field<String> getCollectionId() {
        return this.collectionTable.COLLECTION_ID;
    }

    public Field<Boolean> getLocked() {
        return this.collectionTable.LOCKED;
    }

    public Field<Boolean> getMutable() {
        return this.collectionTable.MUTABLE;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = Lists.newArrayList();
        where.add(this.collectionTable.SYSTEM.eq(false).or(this.collectionTable.NAME.eq("user")));
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
