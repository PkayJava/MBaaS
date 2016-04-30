package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/25/16.
 */
public class ApplicationProvider extends JooqProvider {

    private ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
    private UserTable userTable = Tables.USER.as("userTable");
    private ClientTable clientTable = Tables.CLIENT.as("clientTable");

    private String ownerUserId;

    private TableLike<?> from;

    public ApplicationProvider() {
        this(null);
    }

    public ApplicationProvider(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        this.from = this.applicationTable.join(this.userTable).on(this.applicationTable.OWNER_USER_ID.eq(this.userTable.USER_ID)).leftJoin(this.clientTable).on(this.applicationTable.APPLICATION_ID.eq(this.clientTable.APPLICATION_ID));
        setGroupBy(this.applicationTable.APPLICATION_ID);
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<String> getSecurity() {
        return this.applicationTable.SECURITY;
    }

    public Field<String> getName() {
        return this.applicationTable.NAME;
    }

    public Field<String> getPushMasterSecret() {
        return this.applicationTable.PUSH_MASTER_SECRET;
    }

    public Field<String> getPushApplicationId() {
        return this.applicationTable.PUSH_APPLICATION_ID;
    }

    public Field<Date> getDateCreated() {
        return this.applicationTable.DATE_CREATED;
    }

    public Field<String> getDescription() {
        return this.applicationTable.DESCRIPTION;
    }

    public Field<String> getApplicationId() {
        return this.applicationTable.APPLICATION_ID;
    }

    public Field<Integer> getClient() {
        return DSL.count(this.clientTable.CLIENT_ID);
    }

    @Override

    protected TableLike<?> from() {
        return from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        if (ownerUserId != null) {
            where.add(this.userTable.USER_ID.eq(this.ownerUserId));
        }
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}