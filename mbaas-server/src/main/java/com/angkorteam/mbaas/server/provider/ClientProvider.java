package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/25/16.
 */
public class ClientProvider extends JooqProvider {

    private UserTable userTable = Tables.USER.as("userTable");
    private ClientTable clientTable = Tables.CLIENT.as("clientTable");

    private String applicationId;

    private TableLike<?> from;

    public ClientProvider(String applicationId) {
        this.applicationId = applicationId;
        this.from = this.clientTable.join(this.userTable).on(this.clientTable.OWNER_USER_ID.eq(this.userTable.USER_ID));
    }

    public Field<String> getClientId() {
        return this.clientTable.CLIENT_ID;
    }

    public Field<String> getClientSecret() {
        return this.clientTable.CLIENT_SECRET;
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<String> getSecurity() {
        return this.clientTable.SECURITY;
    }

    public Field<String> getName() {
        return this.clientTable.NAME;
    }

    public Field<Date> getDateCreated() {
        return this.clientTable.DATE_CREATED;
    }

    public Field<String> getDescription() {
        return this.clientTable.DESCRIPTION;
    }

    @Override

    protected TableLike<?> from() {
        return from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.clientTable.APPLICATION_ID.eq(this.applicationId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}