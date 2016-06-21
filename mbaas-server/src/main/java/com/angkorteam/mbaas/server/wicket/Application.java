package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.jooq.IDSLContext;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.server.Scope;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.page.DashboardPage;
import com.angkorteam.mbaas.server.page.LoginPage;
import com.angkorteam.mbaas.server.page.mbaas.MBaaSDashboardPage;
import com.angkorteam.mbaas.server.page.profile.InformationPage;
import com.angkorteam.mbaas.server.service.PusherClient;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.google.gson.Gson;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.resource.DynamicJQueryResourceReference;
import org.apache.wicket.settings.ExceptionSettings;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.jooq.DSLContext;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 */
public class Application extends AuthenticatedWebApplication implements IDSLContext {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(Application.class);


    @Override
    public RuntimeConfigurationType getConfigurationType() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        return RuntimeConfigurationType.valueOf(configuration.getString(Constants.WICKET));
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        Session session = (Session) Session.get();
        if (session.isSignedIn()) {
            if (session.getApplicationUserId() != null && !"".equals(session.getApplicationUserId())) {
                if (session.isAdministrator()) {
                    return DashboardPage.class;
                } else if (session.isRegistered()) {
                    return InformationPage.class;
                }
            } else if (session.getMbaasUserId() != null && !"".equals(session.getMbaasUserId())) {
                return MBaaSDashboardPage.class;
            }
        }
        return LoginPage.class;
    }

    @Override
    public org.apache.wicket.Session newSession(Request request, Response response) {
        Session session = (Session) super.newSession(request, response);
        RequestCycle requestCycle = RequestCycle.get();
        HttpServletRequest httpServletRequest = (HttpServletRequest) requestCycle.getRequest().getContainerRequest();
        String hostname = httpServletRequest.getServerName();
        DSLContext context = getDSLContext();
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = null;
        if (hostnameRecord != null) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);
        }
        if (applicationRecord != null) {
            session.setStyle(applicationRecord.getCode());
        }

        return session;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        getExceptionSettings().setAjaxErrorHandlingStrategy(ExceptionSettings.AjaxErrorStrategy.REDIRECT_TO_ERROR_PAGE);
        getExceptionSettings().setThreadDumpStrategy(ExceptionSettings.ThreadDumpStrategy.THREAD_HOLDING_LOCK);
        getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
        getRequestCycleSettings().setBufferResponse(true);
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        initPageMount();
        getJavaScriptLibrarySettings().setJQueryReference(new DynamicJQueryResourceReference());
    }

    protected void initPageMount() {
        Reflections reflections = new Reflections(Scope.class.getPackage().getName());
        Set<Class<?>> pages = reflections.getTypesAnnotatedWith(Mount.class);
        Map<String, String> mounted = new HashMap<>();
        if (pages != null && !pages.isEmpty()) {
            for (Class<?> page : pages) {
                if (WebPage.class.isAssignableFrom(page)) {
                    Mount mount = page.getAnnotation(Mount.class);
                    if (mount.value() != null && !"".equals(mount.value())) {
                        String url = mount.value().startsWith("/") ? mount.value() : "/" + mount.value();
                        if (mounted.containsKey(url)) {
                            throw new WicketRuntimeException(url + " is ambiguous between " + page.getName() + " and " + mounted.get(url));
                        } else {
                            mounted.put(url, page.getName());
                            mountPage(mount.value(), (Class<WebPage>) page);
                        }
                    }
                }
            }
        }
    }

    public final ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getApplicationDataSource();
    }

    @Override
    public final DSLContext getDSLContext() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getDSLContext();
    }

    public final DSLContext getDSLContext(String applicationCode) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(applicationCode)).fetchOneInto(applicationTable);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getApplicationDataSource().getDSLContext(applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
    }

    public final JdbcTemplate getJdbcTemplate() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getJdbcTemplate();
    }

    public final void close(String applicationCode) {
        ApplicationUtils.getApplication().getApplicationDataSource().destroyApplication(applicationCode);
    }

    public final JdbcTemplate getJdbcTemplate(String applicationCode) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(applicationCode)).fetchOneInto(applicationTable);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getApplicationDataSource().getJdbcTemplate(applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
    }

    public final DbSupport getDbSupport() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getDbSupport();
    }

    public final Schema getSchema(String applicationCode) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(applicationCode)).fetchOneInto(applicationTable);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getApplicationDataSource().getDbSchema(applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
    }

    public final MailSender getMailSender() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getMailSender();
    }

    public final PusherClient getPusherClient() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getPusherClient();
    }

    public final JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getJavascriptService();
    }

    public final Gson getGson() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getGson();
    }

    public final ScriptEngineFactory getScriptEngineFactory() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getScriptEngineFactory();
    }

    public final ClassFilter getClassFilter() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getClassFilter();
    }

    public final ScriptEngine getScriptEngine() {
        ScriptEngineFactory scriptEngineFactory = getScriptEngineFactory();
        ScriptEngine scriptEngine = null;
        if (scriptEngineFactory instanceof NashornScriptEngineFactory) {
            scriptEngine = ((NashornScriptEngineFactory) scriptEngineFactory).getScriptEngine(getClassFilter());
        } else {
            scriptEngine = scriptEngineFactory.getScriptEngine();
        }
        JavascripUtils.eval(scriptEngine);
        return scriptEngine;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return Session.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    public void trackSession(String sessionId, Session session, Map<String, Session> sessions) {
        sessions.put(sessionId, session);
    }

    public void invalidate(String sessionId, Map<String, Session> sessions) {
        LOGGER.info("session {} is revoked", sessionId);
        DSLContext context = getDSLContext();
        context.delete(Tables.DESKTOP).where(Tables.DESKTOP.SESSION_ID.eq(sessionId)).execute();
        Session session = sessions.remove(sessionId);
        if (session != null) {
            try {
                session.invalidateNow();
            } catch (WicketRuntimeException e) {
            }
        }
    }

}
