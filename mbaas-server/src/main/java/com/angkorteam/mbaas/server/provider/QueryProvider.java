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
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.from = this.queryTable.join(this.userTable).on(this.queryTable.field(Jdbc.Query.APPLICATION_USER_ID, String.class).eq(this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getQueryId() {
        return this.queryTable.field(Jdbc.Query.QUERY_ID, String.class);
    }

    public Field<String> getName() {
        return this.queryTable.field(Jdbc.Query.NAME, String.class);
    }

    public Field<String> getDescription() {
        return this.queryTable.field(Jdbc.Query.DESCRIPTION, String.class);
    }

    public Field<Date> getDateCreated() {
        return this.queryTable.field(Jdbc.Query.DATE_CREATED, Date.class);
    }

    public Field<String> getSecurity() {
        return this.queryTable.field(Jdbc.Query.SECURITY, String.class);
    }

    public Field<String> getApplicationUser() {
        return this.userTable.field(Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
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
