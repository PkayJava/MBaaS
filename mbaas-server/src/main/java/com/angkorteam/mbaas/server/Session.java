package com.angkorteam.mbaas.server;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * Created by socheat on 10/23/16.
 */
public class Session extends AuthenticatedWebSession {

    private Roles roles;

    public Session(Request request) {
        super(request);
        this.roles = new Roles();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        DSLContext context = Spring.getBean(DSLContext.class);
        UserTable userTable = Tables.USER.as("userTable");
        UserPojo user = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(UserPojo.class);
        if (user != null) {
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            RolePojo role = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(user.getRoleId())).fetchOneInto(RolePojo.class);
            if (role != null) {
                this.roles.add(role.getName());
            }
        }
        return user != null;
    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }
}
