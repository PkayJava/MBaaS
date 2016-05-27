package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/19/16.
 */
public class QueryProvider extends JooqProvider {

    private Table<?> queryTable;
    private Table<?> userTable;

    private TableLike<?> from;

    private final String applicationCode;

    public QueryProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.queryTable = DSL.table(Jdbc.QUERY).as("queryTable");
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
        this.from = this.queryTable.join(this.userTable).on(DSL.field(this.queryTable.getName() + "." + Jdbc.Query.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    public Field<String> getQueryId() {
        return DSL.field(this.queryTable.getName() + "." + Jdbc.Query.QUERY_ID, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.queryTable.getName() + "." + Jdbc.Query.NAME, String.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.queryTable.getName() + "." + Jdbc.Query.DESCRIPTION, String.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.queryTable.getName() + "." + Jdbc.Query.DATE_CREATED, Date.class);
    }

    public Field<String> getSecurity() {
        return DSL.field(this.queryTable.getName() + "." + Jdbc.Query.SECURITY, String.class);
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class);
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
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
