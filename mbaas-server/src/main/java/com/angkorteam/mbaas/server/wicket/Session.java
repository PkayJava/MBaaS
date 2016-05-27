package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasRoleTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasRoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private String mbaasUserId;

    private String applicationUserId;

    private String applicationCode;

    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);

    public static final transient Map<String, Session> SESSIONS = new WeakHashMap<>();

    public Session(Request request) {
        super(request);
    }

    public final boolean mbaasSignIn(final String username, final String password) {
        DSLContext context = getDSLContext();
        MbaasUserTable mbaasUserTable = Tables.MBAAS_USER.as("mbaasUserTable");
        MbaasUserRecord mbaasUserRecord = context.select(mbaasUserTable.fields()).from(mbaasUserTable).where(mbaasUserTable.LOGIN.eq(username)).and(mbaasUserTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(mbaasUserTable);
        if (mbaasUserRecord == null) {
            return false;
        }
        String sessionId = getId();
        this.roles = new Roles();

        MbaasRoleTable mbaasRoleTable = Tables.MBAAS_ROLE.as("mbaasRoleTable");
        MbaasRoleRecord mbaasRoleRecord = context.select(mbaasRoleTable.fields()).from(mbaasRoleTable).where(mbaasRoleTable.MBAAS_ROLE_ID.eq(mbaasUserRecord.getMbaasRoleId())).fetchOneInto(mbaasRoleTable);
        this.roles.add(mbaasRoleRecord.getName());

        DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");
        context.delete(desktopTable).where(desktopTable.SESSION_ID.eq(sessionId)).execute();

        DesktopRecord desktopRecord = context.newRecord(desktopTable);
        desktopRecord.setDesktopId(UUID.randomUUID().toString());
        desktopRecord.setMbaasUserId(mbaasUserRecord.getMbaasUserId());
        desktopRecord.setDateSeen(new Date());
        desktopRecord.setDateCreated(new Date());
        desktopRecord.setSessionId(sessionId);
        desktopRecord.setUserAgent(getClientInfo().getUserAgent());
        desktopRecord.setClientIp(getClientInfo().getProperties().getRemoteAddress());
        desktopRecord.store();

        this.mbaasUserId = mbaasUserRecord.getMbaasUserId();

        signIn(true);

        Application application = (Application) getApplication();
        application.trackSession(sessionId, this, SESSIONS);
        bind();

        return true;
    }

    public final boolean applicationSignIn(final String secret, final String username, final String password) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.SECRET.eq(secret)).fetchOneInto(applicationTable);
        if (applicationRecord == null) {
            return false;
        }
        JdbcTemplate jdbcTemplate = getJdbcTemplate(applicationRecord.getCode());
        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.USER + " WHERE " + Jdbc.User.LOGIN + " = ? AND " + Jdbc.User.PASSWORD + " = MD5(?)", username, password);
        if (userRecord == null) {
            return false;
        }
        String sessionId = getId();
        this.roles = new Roles();
        Map<String, Object> roleRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " = ?", userRecord.get(Jdbc.User.ROLE_ID));
        this.roles.add((String) roleRecord.get(Jdbc.Role.NAME));

        this.applicationCode = applicationRecord.getCode();

        DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");
        context.delete(desktopTable).where(desktopTable.SESSION_ID.eq(sessionId)).execute();

        DesktopRecord desktopRecord = context.newRecord(desktopTable);
        desktopRecord.setDesktopId(UUID.randomUUID().toString());
        desktopRecord.setMbaasUserId(applicationRecord.getMbaasUserId());
        desktopRecord.setApplicationUserId((String) userRecord.get(Jdbc.User.USER_ID));
        desktopRecord.setDateSeen(new Date());
        desktopRecord.setDateCreated(new Date());
        desktopRecord.setSessionId(sessionId);
        desktopRecord.setUserAgent(getClientInfo().getUserAgent());
        desktopRecord.setClientIp(getClientInfo().getProperties().getRemoteAddress());
        desktopRecord.store();

        this.mbaasUserId = applicationRecord.getMbaasUserId();
        this.applicationUserId = (String) userRecord.get(Jdbc.User.USER_ID);

        signIn(true);

        Application application = (Application) getApplication();
        application.trackSession(sessionId, this, SESSIONS);

        bind();
        return true;
    }

    @Override
    protected boolean authenticate(String username, String password) {
        throw new WicketRuntimeException("authenticate is not implemented");
    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }

    public final String getMbaasUserId() {
        return mbaasUserId;
    }

    public final String getApplicationUserId() {
        return this.applicationUserId;
    }

    public boolean isMBaaSAdministrator() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_MBAAS_ADMINISTRATOR))) {
            return true;
        }
        return false;
    }

    public boolean isMBaaSSystem() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_MBAAS_SYSTEM))) {
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

    public boolean isAdministrator() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        if (isSignedIn() && getRoles().hasRole(configuration.getString(Constants.ROLE_ADMINISTRATOR))) {
            return true;
        }
        return false;
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }

    public final JdbcTemplate getJdbcTemplate(String applicationCode) {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate(applicationCode);
    }

    public final MailSender getMailSender() {
        Application application = (Application) getApplication();
        return application.getMailSender();
    }

    public final String getApplicationCode() {
        return applicationCode;
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();
        LOGGER.info("session {} is revoked", getId());
        if (getDSLContext() != null) {
            getDSLContext().delete(Tables.DESKTOP).where(Tables.DESKTOP.SESSION_ID.eq(getId())).execute();
        }
        Session session = SESSIONS.remove(getId());
        if (session != null) {
            try {
                session.invalidateNow();
            } catch (WicketRuntimeException e) {
            }
        }
    }
}
