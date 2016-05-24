package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class RoleProvider extends JooqProvider {

    private final String applicationCode;

    private Table<?> roleTable;

    private TableLike<?> from;

    public RoleProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.roleTable = DSL.table("role").as("roleTable");
        this.from = this.roleTable;
    }

    public Field<String> getRoleId() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.ROLE_ID, String.class);
    }

    public Field<Boolean> getSystem() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.SYSTEM, Boolean.class);
    }

    public Field<String> getName() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.NAME, String.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.DESCRIPTION, String.class);
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
