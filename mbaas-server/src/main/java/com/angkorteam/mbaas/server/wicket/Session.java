package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by socheat on 3/1/16.
 */
public class Session extends AuthenticatedWebSession {

    private Roles roles;

    private String userId;

    private transient DSLContext context;

    private final static transient Logger LOGGER = LoggerFactory.getLogger(Session.class);

    public static final transient Map<String, Session> SESSIONS = new WeakHashMap<>();

    public Session(Request request) {
        super(request);
        this.context = getDSLContext();
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

            DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");
            context.delete(desktopTable).where(desktopTable.SESSION_ID.eq(sessionId)).execute();

            DesktopRecord desktopRecord = context.newRecord(desktopTable);
            desktopRecord.setDesktopId(UUID.randomUUID().toString());
            desktopRecord.setOwnerUserId(userRecord.getUserId());
            desktopRecord.setDateSeen(new Date());
            desktopRecord.setDateCreated(new Date());
            desktopRecord.setSessionId(sessionId);
            desktopRecord.setUserAgent(getClientInfo().getUserAgent());
            desktopRecord.setClientIp(getClientInfo().getProperties().getRemoteAddress());
            desktopRecord.store();

            this.userId = userRecord.getUserId();

            Application application = (Application) getApplication();
            application.trackSession(sessionId, this, SESSIONS);
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

    public final MailSender getMailSender() {
        Application application = (Application) getApplication();
        return application.getMailSender();
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();
        LOGGER.info("session {} is revoked", getId());
        this.context.delete(Tables.DESKTOP).where(Tables.DESKTOP.SESSION_ID.eq(getId())).execute();
        Session session = SESSIONS.remove(getId());
        if (session != null) {
            try {
                session.invalidateNow();
            } catch (WicketRuntimeException e) {
            }
        }
    }
}
