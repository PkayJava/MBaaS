package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.link.Link;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.function.ApplicationFunction;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.page.application.ApplicationCreatePage;
import com.angkorteam.mbaas.server.page.application.ApplicationManagementPage;
import com.angkorteam.mbaas.server.page.application.ApplicationModifyPage;
import com.angkorteam.mbaas.server.page.asset.AssetCreatePage;
import com.angkorteam.mbaas.server.page.asset.AssetManagementPage;
import com.angkorteam.mbaas.server.page.asset.AssetModifyPage;
import com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage;
import com.angkorteam.mbaas.server.page.attribute.AttributeManagementPage;
import com.angkorteam.mbaas.server.page.client.ClientCreatePage;
import com.angkorteam.mbaas.server.page.client.ClientManagementPage;
import com.angkorteam.mbaas.server.page.client.ClientModifyPage;
import com.angkorteam.mbaas.server.page.collection.CollectionCreatePage;
import com.angkorteam.mbaas.server.page.collection.CollectionManagementPage;
import com.angkorteam.mbaas.server.page.collection.CollectionRolePrivacyManagementPage;
import com.angkorteam.mbaas.server.page.collection.CollectionUserPrivacyManagementPage;
import com.angkorteam.mbaas.server.page.document.DocumentCreatePage;
import com.angkorteam.mbaas.server.page.document.DocumentManagementPage;
import com.angkorteam.mbaas.server.page.document.DocumentModifyPage;
import com.angkorteam.mbaas.server.page.file.FileCreatePage;
import com.angkorteam.mbaas.server.page.file.FileManagementPage;
import com.angkorteam.mbaas.server.page.file.FileModifyPage;
import com.angkorteam.mbaas.server.page.javascript.JavascriptCreatePage;
import com.angkorteam.mbaas.server.page.javascript.JavascriptManagementPage;
import com.angkorteam.mbaas.server.page.javascript.JavascriptModifyPage;
import com.angkorteam.mbaas.server.page.job.JobCreatePage;
import com.angkorteam.mbaas.server.page.job.JobManagementPage;
import com.angkorteam.mbaas.server.page.job.JobModifyPage;
import com.angkorteam.mbaas.server.page.nashorn.NashornManagementPage;
import com.angkorteam.mbaas.server.page.profile.InformationPage;
import com.angkorteam.mbaas.server.page.profile.PasswordPage;
import com.angkorteam.mbaas.server.page.profile.TimeOTPPage;
import com.angkorteam.mbaas.server.page.profile.TwoMailPage;
import com.angkorteam.mbaas.server.page.query.*;
import com.angkorteam.mbaas.server.page.resource.ResourceCreatePage;
import com.angkorteam.mbaas.server.page.resource.ResourceManagementPage;
import com.angkorteam.mbaas.server.page.resource.ResourceModifyPage;
import com.angkorteam.mbaas.server.page.role.RoleCreatePage;
import com.angkorteam.mbaas.server.page.role.RoleManagementPage;
import com.angkorteam.mbaas.server.page.role.RoleModifyPage;
import com.angkorteam.mbaas.server.page.session.SessionDesktopPage;
import com.angkorteam.mbaas.server.page.session.SessionMobilePage;
import com.angkorteam.mbaas.server.page.setting.SettingCreatePage;
import com.angkorteam.mbaas.server.page.setting.SettingManagementPage;
import com.angkorteam.mbaas.server.page.setting.SettingModifyPage;
import com.angkorteam.mbaas.server.page.user.*;
import com.angkorteam.mbaas.server.service.PusherClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/10/16.
 */
public abstract class MasterPage extends AdminLTEPage {

    private Label pageHeaderLabel;
    private Label pageDescriptionLabel;

    private String menuGeneralClass = "treeview";
    private String menuProfileClass = "treeview";
    private String menuSecurityClass = "treeview";
    private String menuDataClass = "treeview";
    private String menuStorageClass = "treeview";
    private String menuSessionClass = "treeview";
    private String menuPluginClass = "treeview";

    private String mmenuApplicationClass = "";
    private String mmenuSettingClass = "";
    private String mmenuLocalizationClass = "";

    private String mmenuInformationClass = "";
    private String mmenuOneTimePasswordClass = "";
    private String mmenu2Factor2EmailClass = "";
    private String mmenuPasswordClass = "";

    private String mmenuUserClass = "";
    private String mmenuRoleClass = "";
    private String mmenuNashornClass = "";

    private String mmenuCollectionClass = "";
    private String mmenuQueryClass = "";

    private String mmenuFileClass = "";
    private String mmenuAssetClass = "";

    private String mmenuDesktopClass = "";
    private String mmenuMobileClass = "";

    private String mmenuJavascriptClass;
    private String mmenuClientClass = "";

    private String mmenuJobClass = "";

    private WebMarkupContainer menuGeneral;
    private WebMarkupContainer menuProfile;
    private WebMarkupContainer menuSecurity;
    private WebMarkupContainer menuSession;

    private WebMarkupContainer menuLogicConsole;

    private WebMarkupContainer menuStorage;
    private WebMarkupContainer mmenuFile;
    private WebMarkupContainer mmenuAsset;

    public MasterPage() {
    }

    public MasterPage(IModel<?> model) {
        super(model);
    }

    public MasterPage(PageParameters parameters) {
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
            desktopRecord.setOwnerUserId(session.getUserId());
            desktopRecord.setDateSeen(new Date());
            desktopRecord.setClientIp(getSession().getClientInfo().getProperties().getRemoteAddress());
            desktopRecord.setUserAgent(getSession().getClientInfo().getUserAgent());
            desktopRecord.update();
        }

        BookmarkablePageLink<Void> dashboardPageLink = new BookmarkablePageLink<>("dashboardPageLink", getApplication().getHomePage());
        add(dashboardPageLink);

        StringValue switchApplicationId = getPageParameters().get("switchApplicationId");
        if (switchApplicationId != null && !switchApplicationId.toString("").equals("")) {
            getSession().setApplicationId(switchApplicationId.toString());
        }

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        Label labelDashboard = new Label("labelDashboard", "Mobile BaaS");
        dashboardPageLink.add(labelDashboard);

        this.pageHeaderLabel = new Label("pageHeaderLabel", new PropertyModel<>(this, "pageHeader"));
        add(this.pageHeaderLabel);
        this.pageDescriptionLabel = new Label("pageDescriptionLabel", new PropertyModel<>(this, "pageDescription"));
        add(this.pageDescriptionLabel);

        {
            WebMarkupContainer applicationMenu = new WebMarkupContainer("applicationMenu");
            add(applicationMenu);
            Label currentApplicationLabel = new Label("currentApplicationLabel", new PropertyModel<>(this, "currentApplicationName"));
            applicationMenu.add(currentApplicationLabel);
            List<ApplicationRecord> applicationRecords = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.OWNER_USER_ID.eq(getSession().getUserId())).fetchInto(applicationTable);
            RepeatingView fields = new RepeatingView("applicationItems");
            applicationMenu.add(fields);
            for (ApplicationRecord applicationRecord : applicationRecords) {
                WebMarkupContainer markupContainer = new WebMarkupContainer(fields.newChildId());
                PageParameters parameters = new PageParameters();
                parameters.add("switchApplicationId", applicationRecord.getApplicationId());
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("applicationItemLink", ClientManagementPage.class, parameters);
                markupContainer.add(link);
                Label applicationItemLabel = new Label("applicationItemLabel", applicationRecord.getName());
                link.add(applicationItemLabel);
                fields.add(markupContainer);
            }
            applicationMenu.setVisible(!getSession().isRegistered());
        }

        {
            Link<Void> logoutLink = new Link<>("logoutLink");
            add(logoutLink);
            logoutLink.setOnClick(this::logoutLinkOnClick);
            logoutLink.setVisible(getSession().isSignedIn());

            Link<Void> backupLink = new Link<>("backupLink");
            add(backupLink);
            backupLink.setOnClick(this::backupLinkOnClick);
            backupLink.setVisible(getSession().getApplicationId() != null && !"".equals(getSession().getApplicationId()));

            Link<Void> importLink = new Link<>("importLink");
            add(importLink);
            importLink.setOnClick(this::importLinkOnClick);
            importLink.setVisible(getSession().isSignedIn());
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
            WebMarkupContainer mmenuRole = new WebMarkupContainer("mmenuRole");
            mmenuRole.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuRoleClass")));
            this.menuSecurity.add(mmenuRole);
            WebMarkupContainer mmenuNashorn = new WebMarkupContainer("mmenuNashorn");
            mmenuNashorn.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuNashornClass")));
            this.menuSecurity.add(mmenuNashorn);
        }

        {
            this.menuStorage = new WebMarkupContainer("menuStorage");
            this.menuStorage.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuStorageClass")));
            add(this.menuStorage);
            this.mmenuFile = new WebMarkupContainer("mmenuFile");
            this.mmenuFile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuFileClass")));
            this.menuStorage.add(this.mmenuFile);
            this.mmenuAsset = new WebMarkupContainer("mmenuAsset");
            this.mmenuAsset.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuAssetClass")));
            this.menuStorage.add(this.mmenuAsset);
        }

        {
            this.menuSession = new WebMarkupContainer("menuSession");
            this.menuSession.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuSessionClass")));
            add(this.menuSession);
            WebMarkupContainer mmenuDesktop = new WebMarkupContainer("mmenuDesktop");
            mmenuDesktop.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuDesktopClass")));
            this.menuSession.add(mmenuDesktop);
            WebMarkupContainer mmenuMobile = new WebMarkupContainer("mmenuMobile");
            mmenuMobile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuMobileClass")));
            this.menuSession.add(mmenuMobile);
        }

        {
            this.menuLogicConsole = new WebMarkupContainer("menuLogicConsole");
            add(this.menuLogicConsole);

            Label currentApplicationConsole = new Label("currentApplicationConsole", new PropertyModel<>(this, "currentApplicationName"));
            this.menuLogicConsole.add(currentApplicationConsole);

            WebMarkupContainer mmenuJavascript = new WebMarkupContainer("mmenuJavascript");
            mmenuJavascript.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuJavascriptClass")));
            this.menuLogicConsole.add(mmenuJavascript);

            WebMarkupContainer mmenuApplication = new WebMarkupContainer("mmenuApplication");
            mmenuApplication.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuApplicationClass")));
            this.menuLogicConsole.add(mmenuApplication);

            WebMarkupContainer mmenuClient = new WebMarkupContainer("mmenuClient");
            mmenuClient.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuClientClass")));
            this.menuLogicConsole.add(mmenuClient);

            WebMarkupContainer mmenuCollection = new WebMarkupContainer("mmenuCollection");
            mmenuCollection.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuCollectionClass")));
            this.menuLogicConsole.add(mmenuCollection);

            WebMarkupContainer mmenuQuery = new WebMarkupContainer("mmenuQuery");
            mmenuQuery.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuQueryClass")));
            this.menuLogicConsole.add(mmenuQuery);

            WebMarkupContainer mmenuJob = new WebMarkupContainer("mmenuJob");
            mmenuJob.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuJobClass")));
            this.menuLogicConsole.add(mmenuJob);
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        boolean isAdministrator = getSession().isAdministrator();
        boolean isBackOffice = getSession().isBackOffice();
        boolean isRegistered = getSession().isRegistered();
        this.menuGeneral.setVisible(isAdministrator);
        this.menuSecurity.setVisible(isAdministrator);
        this.menuSession.setVisible(isAdministrator);
        this.menuLogicConsole.setVisible((getSession().getApplicationId() != null && !"".equals(getSession().getApplicationId()) && (isAdministrator || isBackOffice)));
        this.mmenuFile.setVisible(isAdministrator);
        this.mmenuAsset.setVisible(isAdministrator || isBackOffice || isRegistered);
        this.menuStorage.setVisible(this.mmenuAsset.isVisible() || this.mmenuFile.isVisible());

        // Parent Menu
        if (getPage() instanceof SettingManagementPage
                || getPage() instanceof SettingModifyPage
                || getPage() instanceof SettingCreatePage
                || getPage() instanceof ResourceManagementPage
                || getPage() instanceof ResourceModifyPage
                || getPage() instanceof ResourceCreatePage) {
            this.menuGeneralClass = "treeview active";
        } else {
            this.menuGeneralClass = "treeview";
        }

        if (isRegistered || getPage() instanceof InformationPage || getPage() instanceof TimeOTPPage || getPage() instanceof TwoMailPage || getPage() instanceof PasswordPage) {
            this.menuProfileClass = "treeview active";
        } else {
            this.menuProfileClass = "treeview";
        }

        if (getPage() instanceof UserManagementPage
                || getPage() instanceof UserModifyPage
                || getPage() instanceof UserCreatePage
                || getPage() instanceof UserAttributeManagementPage
                || getPage() instanceof UserAttributePermissionModifyPage
                || getPage() instanceof UserAttributePermissionCreatePage
                || getPage() instanceof UserAttributeCreatePage
                || getPage() instanceof RoleManagementPage
                || getPage() instanceof RoleModifyPage
                || getPage() instanceof RoleCreatePage
                || getPage() instanceof NashornManagementPage) {
            this.menuSecurityClass = "treeview active";
        } else {
            this.menuSecurityClass = "treeview";
        }

        if (getPage() instanceof FileManagementPage
                || getPage() instanceof FileModifyPage
                || getPage() instanceof FileCreatePage
                || getPage() instanceof AssetManagementPage
                || getPage() instanceof AssetModifyPage
                || getPage() instanceof AssetCreatePage) {
            this.menuStorageClass = "treeview active";
        } else {
            this.menuStorageClass = "treeview";
        }

        if (getPage() instanceof SessionDesktopPage || getPage() instanceof SessionMobilePage) {
            this.menuSessionClass = "treeview active";
        } else {
            this.menuSessionClass = "treeview";
        }

        if (getPage() instanceof JavascriptManagementPage || getPage() instanceof JavascriptCreatePage || getPage() instanceof JavascriptModifyPage) {
            this.menuPluginClass = "treeview active";
        } else {
            this.menuPluginClass = "treeview";
        }

        // Menu
        if (getPage() instanceof ApplicationManagementPage
                || getPage() instanceof ApplicationCreatePage
                || getPage() instanceof ApplicationModifyPage) {
            this.mmenuApplicationClass = "active";
        } else {
            this.mmenuApplicationClass = "";
        }
        if (getPage() instanceof ClientManagementPage
                || getPage() instanceof ClientModifyPage
                || getPage() instanceof ClientCreatePage) {
            this.mmenuClientClass = "active";
        } else {
            this.mmenuClientClass = "";
        }
        if (getPage() instanceof SettingManagementPage || getPage() instanceof SettingCreatePage || getPage() instanceof SettingModifyPage) {
            this.mmenuSettingClass = "active";
        } else {
            this.mmenuSettingClass = "";
        }
        if (getPage() instanceof ResourceManagementPage || getPage() instanceof ResourceCreatePage || getPage() instanceof ResourceModifyPage) {
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
                || getPage() instanceof UserAttributeManagementPage
                || getPage() instanceof UserAttributeCreatePage
                || getPage() instanceof UserAttributePermissionCreatePage
                || getPage() instanceof UserAttributePermissionModifyPage
                || getPage() instanceof UserCreatePage
                || getPage() instanceof UserModifyPage) {
            this.mmenuUserClass = "active";
        } else {
            this.mmenuUserClass = "";
        }
        if (getPage() instanceof RoleManagementPage || getPage() instanceof RoleCreatePage || getPage() instanceof RoleModifyPage) {
            this.mmenuRoleClass = "active";
        } else {
            this.mmenuRoleClass = "";
        }
        if (getPage() instanceof NashornManagementPage) {
            this.mmenuNashornClass = "active";
        } else {
            this.mmenuNashornClass = "";
        }
        if (getPage() instanceof CollectionManagementPage
                || getPage() instanceof CollectionCreatePage
                || getPage() instanceof CollectionRolePrivacyManagementPage
                || getPage() instanceof CollectionUserPrivacyManagementPage
                || getPage() instanceof DocumentManagementPage
                || getPage() instanceof DocumentCreatePage
                || getPage() instanceof DocumentModifyPage
                || getPage() instanceof AttributeManagementPage
                || getPage() instanceof AttributeCreatePage) {
            this.mmenuCollectionClass = "active";
        } else {
            this.mmenuCollectionClass = "";
        }
        if (getPage() instanceof QueryManagementPage
                || getPage() instanceof QueryUserPrivacyManagementPage
                || getPage() instanceof QueryRolePrivacyManagementPage
                || getPage() instanceof QueryCreatePage
                || getPage() instanceof QueryModifyPage) {
            this.mmenuQueryClass = "active";
        } else {
            this.mmenuQueryClass = "";
        }
        if (getPage() instanceof FileManagementPage
                || getPage() instanceof FileCreatePage
                || getPage() instanceof FileModifyPage) {
            this.mmenuFileClass = "active";
        } else {
            this.mmenuFileClass = "";
        }
        if (getPage() instanceof AssetManagementPage
                || getPage() instanceof AssetCreatePage
                || getPage() instanceof AssetModifyPage) {
            this.mmenuAssetClass = "active";
        } else {
            this.mmenuAssetClass = "";
        }
        if (getPage() instanceof SessionDesktopPage) {
            this.mmenuDesktopClass = "active";
        } else {
            this.mmenuDesktopClass = "";
        }
        if (getPage() instanceof SessionMobilePage) {
            this.mmenuMobileClass = "active";
        } else {
            this.mmenuMobileClass = "";
        }
        if (getPage() instanceof JavascriptManagementPage || getPage() instanceof JavascriptCreatePage || getPage() instanceof JavascriptModifyPage) {
            this.mmenuJavascriptClass = "active";
        } else {
            this.mmenuJavascriptClass = "";
        }
        if (getPage() instanceof JobManagementPage || getPage() instanceof JobCreatePage || getPage() instanceof JobModifyPage) {
            this.mmenuJobClass = "active";
        } else {
            this.mmenuJobClass = "";
        }

        if (getSession().isBackOffice() && getApplicationQuantity() <= 0) {
            if (getPage() instanceof ApplicationCreatePage) {
            } else {
                setResponsePage(ApplicationCreatePage.class);
            }
        }
    }

    public int getApplicationQuantity() {
        DSLContext context = getDSLContext();
        return context.selectCount().from(Tables.APPLICATION).where(Tables.APPLICATION.OWNER_USER_ID.eq(getSession().getUserId())).fetchOneInto(int.class);
    }

    public String getCurrentApplicationName() {
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        DSLContext context = getDSLContext();
        ApplicationRecord applicationRecord = null;
        if (getSession().getApplicationId() != null && !"".equals(getSession().getApplicationId())) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(getSession().getApplicationId())).fetchOneInto(applicationTable);
        }
        if (applicationRecord != null) {
            return applicationRecord.getName();
        } else {
            return "Select One Application";
        }
    }

    private void backupLinkOnClick(Link link) {
        try {
            java.io.File mbaas = ApplicationFunction.backup(getJdbcTemplate(), getSession().getApplicationId());

            IResourceStream resourceStream = new FileResourceStream(
                    new org.apache.wicket.util.file.File(mbaas));
            getRequestCycle().scheduleRequestHandlerAfterCurrent(
                    new ResourceStreamRequestHandler(resourceStream) {
                        @Override
                        public void respond(IRequestCycle requestCycle) {
                            super.respond(requestCycle);
                            Files.remove(mbaas);
                        }
                    }.setFileName(mbaas.getName())
                            .setContentDisposition(ContentDisposition.ATTACHMENT)
                            .setCacheDuration(Duration.NONE));
        } catch (IOException e) {
        }
    }

    private void importLinkOnClick(Link link) {
        System.out.println("Import");
    }

    private void logoutLinkOnClick(Link link) {
        getSession().invalidateNow();
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }

    public final String getNavigatorLanguage() {
        return getSession().getClientInfo().getProperties().getNavigatorLanguage();
    }

    public final JdbcTemplate getJdbcTemplate() {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate();
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
