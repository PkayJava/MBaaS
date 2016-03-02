package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
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
        this.from = userTable.join(roleTable).on(userTable.ROLE_ID.eq(roleTable.ROLE_ID));
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    public Field<String> getUserId() {
        return this.userTable.USER_ID;
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
