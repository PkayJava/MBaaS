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
 * Created by socheat on 3/11/16.
 */
public class JavascriptProvider extends JooqProvider {

    private final String applicationCode;

    private Table<?> javascriptTable;
    private Table<?> userTable;

    private TableLike<?> from;

    public JavascriptProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.javascriptTable = DSL.table(Jdbc.JAVASCRIPT).as("javascriptTable");
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.from = this.javascriptTable.join(this.userTable).on(this.javascriptTable.field(Jdbc.Javascript.APPLICATION_USER_ID, String.class).eq(this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getJavascriptId() {
        return this.javascriptTable.field(Jdbc.Javascript.JAVASCRIPT_ID, String.class);
    }

    public Field<String> getSecurity() {
        return this.javascriptTable.field(Jdbc.Javascript.SECURITY, String.class);
    }

    public Field<Date> getDateCreated() {
        return this.javascriptTable.field(Jdbc.Javascript.DATE_CREATED, Date.class);
    }

    public Field<String> getDescription() {
        return this.javascriptTable.field(Jdbc.Javascript.DESCRIPTION, String.class);
    }

    public Field<String> getPath() {
        return this.javascriptTable.field(Jdbc.Javascript.PATH, String.class);
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
