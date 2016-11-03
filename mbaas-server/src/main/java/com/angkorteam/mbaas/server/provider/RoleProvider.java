package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class RoleProvider extends JooqProvider {

    private RoleTable roleTable;

    private TableLike<?> from;

    public RoleProvider() {
        this.roleTable = Tables.ROLE.as("roleTable");
        this.from = this.roleTable;
    }

    public Field<String> getRoleId() {
        return this.roleTable.ROLE_ID;
    }

    public Field<Boolean> getSystem() {
        return this.roleTable.SYSTEM;
    }

    public Field<String> getName() {
        return this.roleTable.NAME;
    }

    public Field<String> getDescription() {
        return this.roleTable.DESCRIPTION;
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
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
