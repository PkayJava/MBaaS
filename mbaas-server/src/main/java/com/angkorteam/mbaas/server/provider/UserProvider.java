package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class UserProvider extends JooqProvider {

    private TableLike<?> from;

    private UserTable userTable;

    private RoleTable roleTable;

    public UserProvider() {
        this.userTable = Tables.USER.as("userTable");
        this.roleTable = Tables.ROLE.as("roleTable");
        this.from = this.userTable.join(this.roleTable).on(this.userTable.ROLE_ID.eq(this.roleTable.ROLE_ID));
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }

    public Field<String> getUserId() {
        return this.userTable.USER_ID;
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getFullName() {
        return this.userTable.FULL_NAME;
    }

    public Field<String> getRoleName() {
        return this.roleTable.NAME;
    }

    public Field<String> getRoleId() {
        return this.roleTable.ROLE_ID;
    }

    public Field<String> getStatus() {
        return this.userTable.STATUS;
    }

    public Field<Boolean> getSystem() {
        return this.userTable.SYSTEM;
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
