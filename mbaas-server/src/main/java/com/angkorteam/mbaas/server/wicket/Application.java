package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.jooq.IDSLContext;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.Scope;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.page.DashboardPage;
import com.angkorteam.mbaas.server.page.LoginPage;
import com.angkorteam.mbaas.server.page.mbaas.MBaaSDashboardPage;
import com.angkorteam.mbaas.server.page.profile.InformationPage;
import com.angkorteam.mbaas.server.service.PusherClient;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.core.util.resource.ClassPathResourceFinder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.resource.DynamicJQueryResourceReference;
import org.apache.wicket.util.file.Path;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.jooq.DSLContext;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.web.context.support.WebApplicationContextUtils;

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

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        ResourceStreamLocator streamLocator = new ResourceStreamLocator(new org.apache.wicket.core.util.resource.locator.ResourceStreamLocator(new ClassPathResourceFinder(""), new WebApplicationPath(getServletContext(), "/"), new ClassPathResourceFinder("META-INF/resources/"), new Path(FileUtils.getTempDirectoryPath())));
        getResourceSettings().setResourceStreamLocator(streamLocator);
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
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
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
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return applicationContext.getPusherClient();
    }

    public final JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return applicationContext.getJavascriptService();
    }

    public final Gson getGson() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return applicationContext.getGson();
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
