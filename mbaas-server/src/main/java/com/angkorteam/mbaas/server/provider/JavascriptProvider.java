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
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
        this.from = this.javascriptTable.join(this.userTable).on(DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    public Field<String> getJavascriptId() {
        return DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.JAVASCRIPT_ID, String.class);
    }

    public Field<String> getSecurity() {
        return DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.SECURITY, String.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.DATE_CREATED, Date.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.DESCRIPTION, String.class);
    }

    public Field<String> getPath() {
        return DSL.field(this.javascriptTable.getName() + "." + Jdbc.Javascript.PATH, String.class);
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
