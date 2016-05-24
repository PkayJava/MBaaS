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
        this.from = this.clientTable.join(this.userTable).on(this.clientTable.field(Jdbc.Client.APPLICATION_USER_ID, String.class).eq(this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getClientId() {
        return this.clientTable.field(Jdbc.Client.CLIENT_ID, String.class);
    }

    public Field<String> getClientSecret() {
        return this.clientTable.field(Jdbc.Client.CLIENT_SECRET, String.class);
    }

    public Field<String> getApplicationUser() {
        return this.userTable.field(Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
    }

    public Field<String> getSecurity() {
        return this.clientTable.field(Jdbc.Client.SECURITY, String.class);
    }

    public Field<String> getName() {
        return this.clientTable.field(Jdbc.Client.NAME, String.class);
    }

    public Field<Date> getDateCreated() {
        return this.clientTable.field(Jdbc.Client.DATE_CREATED, Date.class);
    }

    public Field<String> getDescription() {
        return this.clientTable.field(Jdbc.Client.DESCRIPTION, String.class);
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