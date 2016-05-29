package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.link.Link;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.page.mbaas.*;
import com.angkorteam.mbaas.server.service.PusherClient;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by socheat on 3/10/16.
 */
public abstract class MBaaSPage extends AdminLTEPage {

    private Label pageHeaderLabel;
    private Label pageDescriptionLabel;

    private String menuGeneralClass = "treeview";
    private String menuProfileClass = "treeview";
    private String menuSecurityClass = "treeview";

    private String menuSessionClass = "treeview";

    private String mmenuApplicationClass = "";
    private String mmenuSettingClass = "";
    private String mmenuLocalizationClass = "";

    private String mmenuInformationClass = "";
    private String mmenuOneTimePasswordClass = "";
    private String mmenu2Factor2EmailClass = "";
    private String mmenuPasswordClass = "";

    private String mmenuUserClass = "";
    private String mmenuNashornClass = "";

    private String mmenuDesktopClass = "";

    private WebMarkupContainer menuGeneral;
    private WebMarkupContainer menuProfile;
    private WebMarkupContainer menuSecurity;
    private WebMarkupContainer menuSession;
    private WebMarkupContainer mmenuApplication;

    public MBaaSPage() {
    }

    public MBaaSPage(IModel<?> model) {
        super(model);
    }

    public MBaaSPage(PageParameters parameters) {
        super(parameters);
    }

    public String getPageHeader() {
        return null;
    }

    public String getPageDescription() {
        return null;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Session session = getSession();

        DSLContext context = getDSLContext();
        DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");

        DesktopRecord desktopRecord = context.select(desktopTable.fields()).from(desktopTable).where(desktopTable.SESSION_ID.eq(session.getId())).fetchOneInto(desktopTable);
        if (desktopRecord != null) {
            desktopRecord.setMbaasUserId(session.getMbaasUserId());
            desktopRecord.setApplicationUserId(session.getApplicationUserId());
            desktopRecord.setDateSeen(new Date());
            desktopRecord.setClientIp(getSession().getClientInfo().getProperties().getRemoteAddress());
            desktopRecord.setUserAgent(getSession().getClientInfo().getUserAgent());
            desktopRecord.update();
        }

        BookmarkablePageLink<Void> dashboardPageLink = new BookmarkablePageLink<>("dashboardPageLink", getApplication().getHomePage());
        add(dashboardPageLink);

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        Label labelDashboard = new Label("labelDashboard", "Mobile BaaS");
        dashboardPageLink.add(labelDashboard);

        this.pageHeaderLabel = new Label("pageHeaderLabel", new PropertyModel<>(this, "pageHeader"));
        add(this.pageHeaderLabel);
        this.pageDescriptionLabel = new Label("pageDescriptionLabel", new PropertyModel<>(this, "pageDescription"));
        add(this.pageDescriptionLabel);

        {
            Link<Void> logoutLink = new Link<>("logoutLink");
            add(logoutLink);
            logoutLink.setOnClick(this::logoutLinkOnClick);
            logoutLink.setVisible(getSession().isSignedIn());
        }

        {
            this.menuGeneral = new WebMarkupContainer("menuGeneral");
            this.menuGeneral.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuGeneralClass")));
            add(this.menuGeneral);
            WebMarkupContainer mmenuSetting = new WebMarkupContainer("mmenuSetting");
            mmenuSetting.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuSettingClass")));
            this.menuGeneral.add(mmenuSetting);
            WebMarkupContainer mmenuLocalization = new WebMarkupContainer("mmenuLocalization");
            mmenuLocalization.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuLocalizationClass")));
            this.menuGeneral.add(mmenuLocalization);
        }

        {
            this.menuProfile = new WebMarkupContainer("menuProfile");
            this.menuProfile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuProfileClass")));
            add(this.menuProfile);
            WebMarkupContainer mmenuInformation = new WebMarkupContainer("mmenuInformation");
            mmenuInformation.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuInformationClass")));
            this.menuProfile.add(mmenuInformation);
            WebMarkupContainer mmenuOneTimePassword = new WebMarkupContainer("mmenuOneTimePassword");
            mmenuOneTimePassword.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuOneTimePasswordClass")));
            this.menuProfile.add(mmenuOneTimePassword);
            WebMarkupContainer mmenu2Factor2Email = new WebMarkupContainer("mmenu2Factor2Email");
            mmenu2Factor2Email.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenu2Factor2EmailClass")));
            this.menuProfile.add(mmenu2Factor2Email);
            WebMarkupContainer mmenuPassword = new WebMarkupContainer("mmenuPassword");
            mmenuPassword.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuPasswordClass")));
            this.menuProfile.add(mmenuPassword);
        }

        {
            this.menuSecurity = new WebMarkupContainer("menuSecurity");
            this.menuSecurity.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuSecurityClass")));
            add(this.menuSecurity);
            WebMarkupContainer mmenuUser = new WebMarkupContainer("mmenuUser");
            mmenuUser.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuUserClass")));
            this.menuSecurity.add(mmenuUser);
            WebMarkupContainer mmenuNashorn = new WebMarkupContainer("mmenuNashorn");
            mmenuNashorn.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuNashornClass")));
            this.menuSecurity.add(mmenuNashorn);
        }

        {
            this.menuSession = new WebMarkupContainer("menuSession");
            this.menuSession.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuSessionClass")));
            add(this.menuSession);
            WebMarkupContainer mmenuDesktop = new WebMarkupContainer("mmenuDesktop");
            mmenuDesktop.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuDesktopClass")));
            this.menuSession.add(mmenuDesktop);
        }
        {
            this.mmenuApplication = new WebMarkupContainer("mmenuApplication");
            mmenuApplication.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuApplicationClass")));
            this.add(mmenuApplication);
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        boolean isMbaasAdministrator = getSession().isMBaaSAdministrator();
        boolean isMbaasSystem = getSession().isMBaaSSystem();
        this.menuGeneral.setVisible(isMbaasAdministrator || isMbaasSystem);
        this.menuSecurity.setVisible(isMbaasSystem);
        this.menuSession.setVisible(isMbaasSystem);
        this.mmenuApplication.setVisible(isMbaasAdministrator || isMbaasSystem);
        this.menuProfile.setVisible(isMbaasAdministrator || isMbaasSystem);

        // Parent Menu
        if (getPage() instanceof SettingManagementPage
                || getPage() instanceof SettingModifyPage
                || getPage() instanceof SettingCreatePage
                || getPage() instanceof LocalizationManagementPage
                || getPage() instanceof LocalizationModifyPage
                || getPage() instanceof LocalizationCreatePage) {
            this.menuGeneralClass = "treeview active";
        } else {
            this.menuGeneralClass = "treeview";
        }

        if (getPage() instanceof InformationPage || getPage() instanceof TimeOTPPage || getPage() instanceof TwoMailPage || getPage() instanceof PasswordPage) {
            this.menuProfileClass = "treeview active";
        } else {
            this.menuProfileClass = "treeview";
        }

        if (getPage() instanceof UserManagementPage
                || getPage() instanceof UserModifyPage
                || getPage() instanceof UserCreatePage
                || getPage() instanceof UserPasswordModifyPage
                || getPage() instanceof NashornManagementPage) {
            this.menuSecurityClass = "treeview active";
        } else {
            this.menuSecurityClass = "treeview";
        }

        if (getPage() instanceof SessionDesktopPage) {
            this.menuSessionClass = "treeview active";
        } else {
            this.menuSessionClass = "treeview";
        }

        // Menu
        if (getPage() instanceof ApplicationManagementPage
                || getPage() instanceof ApplicationCreatePage
                || getPage() instanceof ApplicationModifyPage) {
            this.mmenuApplicationClass = "active";
        } else {
            this.mmenuApplicationClass = "";
        }
        if (getPage() instanceof SettingManagementPage || getPage() instanceof SettingCreatePage || getPage() instanceof SettingModifyPage) {
            this.mmenuSettingClass = "active";
        } else {
            this.mmenuSettingClass = "";
        }
        if (getPage() instanceof LocalizationManagementPage || getPage() instanceof LocalizationCreatePage || getPage() instanceof LocalizationModifyPage) {
            this.mmenuLocalizationClass = "active";
        } else {
            this.mmenuLocalizationClass = "";
        }
        if (getPage() instanceof InformationPage) {
            this.mmenuInformationClass = "active";
        } else {
            this.mmenuInformationClass = "";
        }
        if (getPage() instanceof TimeOTPPage) {
            this.mmenuOneTimePasswordClass = "active";
        } else {
            this.mmenuOneTimePasswordClass = "";
        }
        if (getPage() instanceof TwoMailPage) {
            this.mmenu2Factor2EmailClass = "active";
        } else {
            this.mmenu2Factor2EmailClass = "";
        }
        if (getPage() instanceof PasswordPage) {
            this.mmenuPasswordClass = "active";
        } else {
            this.mmenuPasswordClass = "";
        }
        if (getPage() instanceof UserManagementPage
                || getPage() instanceof UserPasswordModifyPage
                || getPage() instanceof UserCreatePage
                || getPage() instanceof UserModifyPage) {
            this.mmenuUserClass = "active";
        } else {
            this.mmenuUserClass = "";
        }
        if (getPage() instanceof NashornManagementPage) {
            this.mmenuNashornClass = "active";
        } else {
            this.mmenuNashornClass = "";
        }
        if (getPage() instanceof SessionDesktopPage) {
            this.mmenuDesktopClass = "active";
        } else {
            this.mmenuDesktopClass = "";
        }
    }

    private void logoutLinkOnClick(Link link) {
        getSession().invalidateNow();
        setResponsePage(MBaaSLoginPage.class);
    }

    public DbSupport getDbSupport() {
        Application application = (Application) getApplication();
        return application.getDbSupport();
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }

    public final DSLContext getDSLContext(String applicationCode) {
        Application application = (Application) getApplication();
        return application.getDSLContext(applicationCode);
    }

    public final ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        Application application = (Application) getApplication();
        return application.getApplicationDataSource();
    }

    public final String getNavigatorLanguage() {
        return getSession().getClientInfo().getProperties().getNavigatorLanguage();
    }

    public final JdbcTemplate getJdbcTemplate() {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate();
    }

    public final JdbcTemplate getJdbcTemplate(String applicationCode) {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate(applicationCode);
    }

    public ServletContext getServletContext() {
        Application application = (Application) getApplication();
        return application.getServletContext();
    }

    public MailSender getMailSender() {
        Application application = (Application) getApplication();
        return application.getMailSender();
    }

    public String getHttpAddress() {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        return HttpFunction.getHttpAddress(request);
    }

    public PusherClient getPusherClient() {
        Application application = (Application) getApplication();
        return application.getPusherClient();
    }

    public JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        Application application = (Application) getApplication();
        return application.getJavascriptService();
    }
}
