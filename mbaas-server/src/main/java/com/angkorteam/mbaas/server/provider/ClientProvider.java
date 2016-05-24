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
 * Created by socheat on 3/25/16.
 */
public class ClientProvider extends JooqProvider {

    private Table<?> userTable;
    private Table<?> clientTable;

    private final String applicationCode;

    private TableLike<?> from;

    public ClientProvider(String applicationCode) {
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.clientTable = DSL.table(Jdbc.CLIENT).as("clientTable");
        this.applicationCode = applicationCode;
        this.from = this.clientTable.join(this.userTable).on(DSL.field(this.clientTable.getName() + "." + Jdbc.Client.APPLICATION_USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getClientId() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.CLIENT_ID, String.class);
    }

    public Field<String> getClientSecret() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.CLIENT_SECRET, String.class);
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
    }

    public Field<String> getSecurity() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.SECURITY, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.NAME, String.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.DATE_CREATED, Date.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.DESCRIPTION, String.class);
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    @Override

    protected TableLike<?> from() {
        return from;
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