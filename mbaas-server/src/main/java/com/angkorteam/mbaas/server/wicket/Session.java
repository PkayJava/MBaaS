package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.WicketTable;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.model.entity.tables.records.WicketRecord;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 3/1/16.
 */
public class Session extends AuthenticatedWebSession {

    private Roles roles;

    private String userId;

    public Session(Request request) {
        super(request);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(userTable);
        if (userRecord != null) {
            String sessionId = getId();
            this.roles = new Roles();
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(userRecord.getRoleId())).fetchOneInto(roleTable);
            this.roles.add(roleRecord.getName());

            WicketTable wicketTable = Tables.WICKET.as("wicketTable");
            context.delete(wicketTable).where(wicketTable.SESSION_ID.eq(sessionId)).execute();

            WicketRecord wicketRecord = context.newRecord(wicketTable);
            wicketRecord.setWicketId(UUID.randomUUID().toString());
            wicketRecord.setUserId(userRecord.getUserId());
            wicketRecord.setDateSeen(new Date());
            wicketRecord.setDateCreated(new Date());
            wicketRecord.setSessionId(sessionId);
            wicketRecord.store();

            this.userId = userRecord.getUserId();

            Application application = (Application) getApplication();
            application.trackSession(sessionId, this);
        }
        return userRecord != null;
    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }

    public String getUserId() {
        return userId;
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }
}
