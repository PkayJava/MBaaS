package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.link.Link;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.page.application.ApplicationManagementPage;
import com.angkorteam.mbaas.server.page.asset.AssetManagementPage;
import com.angkorteam.mbaas.server.page.collection.CollectionManagementPage;
import com.angkorteam.mbaas.server.page.file.FileManagementPage;
import com.angkorteam.mbaas.server.page.javascript.JavascriptManagementPage;
import com.angkorteam.mbaas.server.page.nashorn.NashornManagementPage;
import com.angkorteam.mbaas.server.page.profile.InformationPage;
import com.angkorteam.mbaas.server.page.profile.PasswordPage;
import com.angkorteam.mbaas.server.page.profile.TimeOTPPage;
import com.angkorteam.mbaas.server.page.profile.TwoMailPage;
import com.angkorteam.mbaas.server.page.query.QueryManagementPage;
import com.angkorteam.mbaas.server.page.resource.ResourceManagementPage;
import com.angkorteam.mbaas.server.page.role.RoleManagementPage;
import com.angkorteam.mbaas.server.page.session.SessionDesktopPage;
import com.angkorteam.mbaas.server.page.session.SessionMobilePage;
import com.angkorteam.mbaas.server.page.setting.SettingManagementPage;
import com.angkorteam.mbaas.server.page.user.UserManagementPage;
import com.angkorteam.mbaas.server.service.PusherClient;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.security.access.method.P;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

        super.onInitialize();

        this.pageHeaderLabel = new Label("pageHeaderLabel", new PropertyModel<>(this, "pageHeader"));
        add(this.pageHeaderLabel);
        this.pageDescriptionLabel = new Label("pageDescriptionLabel", new PropertyModel<>(this, "pageDescription"));
        add(this.pageDescriptionLabel);

        Link<Void> logoutLink = new Link<>("logoutLink");
        add(logoutLink);
        logoutLink.setOnClick(this::logoutLinkOnClick);
        logoutLink.setVisible(getSession().isSignedIn());

        WebMarkupContainer menuGeneral = new WebMarkupContainer("menuGeneral");
        menuGeneral.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuGeneralClass")));
        add(menuGeneral);
        WebMarkupContainer mmenuApplication = new WebMarkupContainer("mmenuApplication");
        mmenuApplication.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuApplicationClass")));
        menuGeneral.add(mmenuApplication);
        WebMarkupContainer mmenuSetting = new WebMarkupContainer("mmenuSetting");
        mmenuSetting.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuSettingClass")));
        menuGeneral.add(mmenuSetting);
        WebMarkupContainer mmenuLocalization = new WebMarkupContainer("mmenuLocalization");
        mmenuLocalization.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuLocalizationClass")));
        menuGeneral.add(mmenuLocalization);

        WebMarkupContainer menuProfile = new WebMarkupContainer("menuProfile");
        menuProfile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuProfileClass")));
        add(menuProfile);
        WebMarkupContainer mmenuInformation = new WebMarkupContainer("mmenuInformation");
        mmenuInformation.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuInformationClass")));
        menuProfile.add(mmenuInformation);
        WebMarkupContainer mmenuOneTimePassword = new WebMarkupContainer("mmenuOneTimePassword");
        mmenuOneTimePassword.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuOneTimePasswordClass")));
        menuProfile.add(mmenuOneTimePassword);
        WebMarkupContainer mmenu2Factor2Email = new WebMarkupContainer("mmenu2Factor2Email");
        mmenu2Factor2Email.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenu2Factor2EmailClass")));
        menuProfile.add(mmenu2Factor2Email);
        WebMarkupContainer mmenuPassword = new WebMarkupContainer("mmenuPassword", new PropertyModel<>(this, "mmenuPasswordClass"));
        menuProfile.add(mmenuPassword);

        WebMarkupContainer menuSecurity = new WebMarkupContainer("menuSecurity");
        menuSecurity.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuSecurityClass")));
        add(menuSecurity);
        WebMarkupContainer mmenuUser = new WebMarkupContainer("mmenuUser");
        mmenuUser.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuUserClass")));
        menuSecurity.add(mmenuUser);
        WebMarkupContainer mmenuRole = new WebMarkupContainer("mmenuRole");
        mmenuRole.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuRoleClass")));
        menuSecurity.add(mmenuRole);
        WebMarkupContainer mmenuNashorn = new WebMarkupContainer("mmenuNashorn");
        mmenuNashorn.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuNashornClass")));
        menuSecurity.add(mmenuNashorn);

        WebMarkupContainer menuData = new WebMarkupContainer("menuData");
        menuData.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuDataClass")));
        add(menuData);
        WebMarkupContainer mmenuCollection = new WebMarkupContainer("mmenuCollection");
        mmenuCollection.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuCollectionClass")));
        menuData.add(mmenuCollection);
        WebMarkupContainer mmenuQuery = new WebMarkupContainer("mmenuQuery");
        mmenuQuery.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuQueryClass")));
        menuData.add(mmenuQuery);

        WebMarkupContainer menuStorage = new WebMarkupContainer("menuStorage");
        menuStorage.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuStorageClass")));
        add(menuStorage);
        WebMarkupContainer mmenuFile = new WebMarkupContainer("mmenuFile");
        mmenuFile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuFileClass")));
        menuStorage.add(mmenuFile);
        WebMarkupContainer mmenuAsset = new WebMarkupContainer("mmenuAsset");
        mmenuAsset.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuAssetClass")));
        menuStorage.add(mmenuAsset);

        WebMarkupContainer menuSession = new WebMarkupContainer("menuSession");
        menuSession.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuSessionClass")));
        add(menuSession);
        WebMarkupContainer mmenuDesktop = new WebMarkupContainer("mmenuDesktop");
        mmenuDesktop.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuDesktopClass")));
        menuSession.add(mmenuDesktop);
        WebMarkupContainer mmenuMobile = new WebMarkupContainer("mmenuMobile");
        mmenuMobile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuMobileClass")));
        menuSession.add(mmenuMobile);

        WebMarkupContainer menuPlugin = new WebMarkupContainer("menuPlugin");
        menuPlugin.add(AttributeModifier.replace("class", new PropertyModel<>(this, "menuPluginClass")));
        add(menuPlugin);
        WebMarkupContainer mmenuJavascript = new WebMarkupContainer("mmenuJavascript");
        mmenuJavascript.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuJavascriptClass")));
        menuPlugin.add(mmenuJavascript);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        // Parent Menu
        if (getPage() instanceof ApplicationManagementPage || getPage() instanceof SettingManagementPage || getPage() instanceof ResourceManagementPage) {
            this.menuGeneralClass = "treeview active";
        } else {
            this.menuGeneralClass = "treeview";
        }

        if (getPage() instanceof InformationPage || getPage() instanceof TimeOTPPage || getPage() instanceof TwoMailPage || getPage() instanceof PasswordPage) {
            this.menuProfileClass = "treeview active";
        } else {
            this.menuProfileClass = "treeview";
        }

        if (getPage() instanceof UserManagementPage || getPage() instanceof RoleManagementPage || getPage() instanceof NashornManagementPage) {
            this.menuSecurityClass = "treeview active";
        } else {
            this.menuSecurityClass = "treeview";
        }

        if (getPage() instanceof CollectionManagementPage || getPage() instanceof QueryManagementPage) {
            this.menuDataClass = "treeview active";
        } else {
            this.menuDataClass = "treeview";
        }

        if (getPage() instanceof FileManagementPage || getPage() instanceof AssetManagementPage) {
            this.menuStorageClass = "treeview active";
        } else {
            this.menuStorageClass = "treeview";
        }

        if (getPage() instanceof SessionDesktopPage || getPage() instanceof SessionMobilePage) {
            this.menuSessionClass = "treeview active";
        } else {
            this.menuSessionClass = "treeview";
        }

        if (getPage() instanceof JavascriptManagementPage) {
            this.menuPluginClass = "treeview active";
        } else {
            this.menuPluginClass = "treeview";
        }

        // Menu
        if (getPage() instanceof ApplicationManagementPage) {
            this.mmenuApplicationClass = "active";
        } else {
            this.mmenuApplicationClass = "";
        }
        if (getPage() instanceof SettingManagementPage) {
            this.mmenuSettingClass = "active";
        } else {
            this.mmenuSettingClass = "";
        }
        if (getPage() instanceof ResourceManagementPage) {
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
        if (getPage() instanceof UserManagementPage) {
            this.mmenuUserClass = "active";
        } else {
            this.mmenuUserClass = "";
        }
        if (getPage() instanceof RoleManagementPage) {
            this.mmenuRoleClass = "active";
        } else {
            this.mmenuRoleClass = "";
        }
        if (getPage() instanceof NashornManagementPage) {
            this.mmenuNashornClass = "active";
        } else {
            this.mmenuNashornClass = "";
        }
        if (getPage() instanceof CollectionManagementPage) {
            this.mmenuCollectionClass = "active";
        } else {
            this.mmenuCollectionClass = "";
        }
        if (getPage() instanceof QueryManagementPage) {
            this.mmenuQueryClass = "active";
        } else {
            this.mmenuQueryClass = "";
        }
        if (getPage() instanceof FileManagementPage) {
            this.mmenuFileClass = "active";
        } else {
            this.mmenuFileClass = "";
        }
        if (getPage() instanceof AssetManagementPage) {
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
        if (getPage() instanceof JavascriptManagementPage) {
            this.mmenuJavascriptClass = "active";
        } else {
            this.mmenuJavascriptClass = "";
        }
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
}
