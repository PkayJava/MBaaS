package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/25/16.
 */
public class ApplicationProvider extends JooqProvider {

    private ApplicationTable applicationTable = ApplicationTable.APPLICATION.as("applicationTable");
    private MbaasUserTable mbaasUserTable = Tables.MBAAS_USER.as("mbaasUserTable");

    private String mbaasUserId;

    private TableLike<?> from;

    public ApplicationProvider() {
        this(null);
    }

    public ApplicationProvider(String mbaasUserId) {
        this.mbaasUserId = mbaasUserId;
        this.from = this.applicationTable.join(this.mbaasUserTable).on(this.applicationTable.MBAAS_USER_ID.eq(this.mbaasUserTable.MBAAS_USER_ID));
    }

    public Field<String> getMbaasUserFullName() {
        return this.mbaasUserTable.FULL_NAME;
    }

    public Field<String> getSecurity() {
        return this.applicationTable.SECURITY;
    }

    public Field<String> getSecret() {
        return this.applicationTable.SECRET;
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

    @Override
    protected TableLike<?> from() {
        return from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        if (this.mbaasUserId != null && !"".equals(this.mbaasUserId)) {
            where.add(this.mbaasUserTable.MBAAS_USER_ID.eq(this.mbaasUserId));
        }
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}