package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
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

    private String applicationId;

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

        boolean verified = userRecord != null;

        if (verified) {
            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
            ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.OWNER_USER_ID.eq(userRecord.getUserId())).limit(1).fetchOneInto(applicationTable);
            if (applicationRecord != null) {
                this.applicationId = applicationRecord.getApplicationId();
            }
        }

        return verified;
    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdministrator() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_ADMINISTRATOR))) {
            return true;
        }
        return false;
    }

    public boolean isBackOffice() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_BACKOFFICE))) {
            return true;
        }
        return false;
    }

    public boolean isRegistered() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_REGISTERED))) {
            return true;
        }
        return false;
    }

    public boolean isAnonymous() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_ANONYMOUS))) {
            return true;
        }
        return false;
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
        if (this.context != null) {
            this.context.delete(Tables.DESKTOP).where(Tables.DESKTOP.SESSION_ID.eq(getId())).execute();
        }
        Session session = SESSIONS.remove(getId());
        if (session != null) {
            try {
                session.invalidateNow();
            } catch (WicketRuntimeException e) {
            }
        }
    }

    public final String getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
