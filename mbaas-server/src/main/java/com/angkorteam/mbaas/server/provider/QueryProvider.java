package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/19/16.
 */
public class QueryProvider extends JooqProvider {

    private QueryTable queryTable = Tables.QUERY.as("queryTable");

    private UserTable userTable = Tables.USER.as("userTable");

    private String address;

    private TableLike<?> from;

    public QueryProvider(String address) {
        this.address = address;
        this.from = this.queryTable.join(this.userTable).on(this.queryTable.OWNER_USER_ID.eq(this.userTable.USER_ID));
    }

    public Field<String> getQueryId() {
        return this.queryTable.QUERY_ID;
    }

    public Field<String> getPath() {
        return this.queryTable.PATH;
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

    public Field<String> getOwner() {
        return this.userTable.LOGIN;
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
