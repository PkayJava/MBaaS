package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/19/16.
 */
public class QueryProvider extends JooqProvider {

    private QueryTable queryTable;

    private TableLike<?> from;

    public QueryProvider() {
        this.queryTable = Tables.QUERY.as("queryTable");
        this.from = this.queryTable;
    }

    public Field<String> getQueryId() {
        return this.queryTable.QUERY_ID;
    }

    public Field<String> getName() {
        return this.queryTable.NAME;
    }

    public Field<String> getDescription() {
        return this.queryTable.DESCRIPTION;
    }

    public Field<Date> getDateCreated() {
        return this.queryTable.DATE_CREATED;
    }

    public Field<String> getSecurity() {
        return this.queryTable.SECURITY;
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
