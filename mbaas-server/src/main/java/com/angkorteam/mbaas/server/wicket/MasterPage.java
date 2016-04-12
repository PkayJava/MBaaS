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
        menuGeneral.add(new AttributeModifier("class", new PropertyModel<>(this, "menuGeneralClass")));
        add(menuGeneral);

        WebMarkupContainer menuProfile = new WebMarkupContainer("menuProfile");
        menuProfile.add(new AttributeModifier("class", new PropertyModel<>(this, "menuProfileClass")));
        add(menuProfile);

        WebMarkupContainer menuSecurity = new WebMarkupContainer("menuSecurity");
        menuSecurity.add(new AttributeModifier("class", new PropertyModel<>(this, "menuSecurityClass")));
        add(menuSecurity);

        WebMarkupContainer menuData = new WebMarkupContainer("menuData");
        menuData.add(new AttributeModifier("class", new PropertyModel<>(this, "menuDataClass")));
        add(menuData);

        WebMarkupContainer menuStorage = new WebMarkupContainer("menuStorage");
        menuStorage.add(new AttributeModifier("class", new PropertyModel<>(this, "menuStorageClass")));
        add(menuStorage);

        WebMarkupContainer menuSession = new WebMarkupContainer("menuSession");
        menuSession.add(new AttributeModifier("class", new PropertyModel<>(this, "menuSessionClass")));
        add(menuSession);

        WebMarkupContainer menuPlugin = new WebMarkupContainer("menuPlugin");
        menuPlugin.add(new AttributeModifier("class", new PropertyModel<>(this, "menuPluginClass")));
        add(menuPlugin);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
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
