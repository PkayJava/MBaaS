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
public class UserProvider extends JooqProvider {

    private final String applicationCode;

    private TableLike<?> from;

    private Table<?> userTable;

    private Table<?> roleTable;

    public UserProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.userTable = DSL.table("application_user").as("userTable");
        this.roleTable = DSL.table("role").as("roleTable");
        this.from = this.userTable.join(this.roleTable).on(DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.ROLE_ID, String.class).eq(DSL.field(this.roleTable.getName() + "." + Jdbc.ApplicationUser.ROLE_ID, String.class)));
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    public Field<String> getLogin() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
    }

    public Field<String> getFullName() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.FULL_NAME, String.class);
    }

    public Field<String> getRoleName() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.NAME, String.class);
    }

    public Field<String> getRoleId() {
        return DSL.field(this.roleTable.getName() + "." + Jdbc.Role.ROLE_ID, String.class);
    }

    public Field<String> getStatus() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.STATUS, String.class);
    }

    public Field<Boolean> getSystem() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.SYSTEM, Boolean.class);
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
