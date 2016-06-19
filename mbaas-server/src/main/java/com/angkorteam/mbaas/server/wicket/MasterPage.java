package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.link.Link;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.page.LoginPage;
import com.angkorteam.mbaas.server.page.PagePage;
import com.angkorteam.mbaas.server.page.asset.AssetCreatePage;
import com.angkorteam.mbaas.server.page.asset.AssetManagementPage;
import com.angkorteam.mbaas.server.page.asset.AssetModifyPage;
import com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage;
import com.angkorteam.mbaas.server.page.attribute.AttributeManagementPage;
import com.angkorteam.mbaas.server.page.client.ClientCreatePage;
import com.angkorteam.mbaas.server.page.client.ClientManagementPage;
import com.angkorteam.mbaas.server.page.client.ClientModifyPage;
import com.angkorteam.mbaas.server.page.cms.block.BlockCreatePage;
import com.angkorteam.mbaas.server.page.cms.block.BlockManagementPage;
import com.angkorteam.mbaas.server.page.cms.block.BlockModifyPage;
import com.angkorteam.mbaas.server.page.cms.master.MasterCreatePage;
import com.angkorteam.mbaas.server.page.cms.master.MasterManagementPage;
import com.angkorteam.mbaas.server.page.cms.master.MasterModifyPage;
import com.angkorteam.mbaas.server.page.cms.page.PageCreatePage;
import com.angkorteam.mbaas.server.page.cms.page.PageManagementPage;
import com.angkorteam.mbaas.server.page.cms.page.PageModifyPage;
import com.angkorteam.mbaas.server.page.collection.CollectionCreatePage;
import com.angkorteam.mbaas.server.page.collection.CollectionManagementPage;
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
import com.angkorteam.mbaas.server.page.menu.MenuCreatePage;
import com.angkorteam.mbaas.server.page.menu.MenuManagementPage;
import com.angkorteam.mbaas.server.page.menu.MenuModifyPage;
import com.angkorteam.mbaas.server.page.profile.InformationPage;
import com.angkorteam.mbaas.server.page.profile.PasswordPage;
import com.angkorteam.mbaas.server.page.profile.TimeOTPPage;
import com.angkorteam.mbaas.server.page.profile.TwoMailPage;
import com.angkorteam.mbaas.server.page.query.QueryCreatePage;
import com.angkorteam.mbaas.server.page.query.QueryManagementPage;
import com.angkorteam.mbaas.server.page.query.QueryModifyPage;
import com.angkorteam.mbaas.server.page.query.QueryParameterModifyPage;
import com.angkorteam.mbaas.server.page.role.RoleCreatePage;
import com.angkorteam.mbaas.server.page.role.RoleManagementPage;
import com.angkorteam.mbaas.server.page.role.RoleModifyPage;
import com.angkorteam.mbaas.server.page.session.SessionMobilePage;
import com.angkorteam.mbaas.server.page.user.UserCreatePage;
import com.angkorteam.mbaas.server.page.user.UserManagementPage;
import com.angkorteam.mbaas.server.page.user.UserModifyPage;
import com.angkorteam.mbaas.server.service.PusherClient;
import jdk.nashorn.api.scripting.ClassFilter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/10/16.
 */
public abstract class MasterPage extends AdminLTEPage {

    private Label pageHeaderLabel;
    private Label pageDescriptionLabel;

    private String menuProfileClass = "treeview";
    private String menuSecurityClass = "treeview";
    private String menuDataClass = "treeview";
    private String menuStorageClass = "treeview";
    private String menuSessionClass = "treeview";
    private String menuPluginClass = "treeview";

    private String mmenuInformationClass = "";
    private String mmenuOneTimePasswordClass = "";
    private String mmenu2Factor2EmailClass = "";
    private String mmenuPasswordClass = "";

    private String mmenuUserClass = "";
    private String mmenuRoleClass = "";

    private String mmenuCollectionClass = "";
    private String mmenuQueryClass = "";

    private String mmenuFileClass = "";
    private String mmenuAssetClass = "";

    private String mmenuMobileClass = "";

    private String mmenuJavascriptClass;
    private String mmenuClientClass = "";

    private String mmenuJobClass = "";
    private String mmenuPageClass = "";
    private String mmenuMasterClass = "";
    private String mmenuMenuClass = "";
    private String mmenuBlockClass = "";

    private WebMarkupContainer menuGeneral;
    private WebMarkupContainer menuProfile;
    private WebMarkupContainer menuSecurity;
    private WebMarkupContainer menuSession;

    private WebMarkupContainer menuLogicConsole;

    private WebMarkupContainer menuStorage;
    private WebMarkupContainer mmenuFile;
    private WebMarkupContainer mmenuAsset;

    private Map<String, String> mmenuItems;
    private Map<String, String> mmenuPages;

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
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS("div[class=\"form-group\"] div.feedbackPanelERROR, div[class=\"form-group\"] label.error { color: #a94442; } div[class=\"form-group\"] input.error, div[class=\"form-group\"] select.error { border-color: #a94442; -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075); box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075); } div[class=\"form-group\"] input.error:focus { border-color: #843534; -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 6px #ce8483; box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 6px #ce8483; } .error .input-group-addon { color: #a94442; background-color: #f2dede; border-color: #a94442; } .error .form-control-feedback { color: #a94442; }", "bootstrap"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.mmenuItems = new HashMap<>();
        this.mmenuPages = new HashMap<>();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

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

        Label labelDashboard = new Label("labelDashboard", "MBaaS");
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
            WebMarkupContainer mmenuMobile = new WebMarkupContainer("mmenuMobile");
            mmenuMobile.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuMobileClass")));
            this.menuSession.add(mmenuMobile);
        }

        {
            this.menuLogicConsole = new WebMarkupContainer("menuLogicConsole");
            add(this.menuLogicConsole);

            WebMarkupContainer mmenuJavascript = new WebMarkupContainer("mmenuJavascript");
            mmenuJavascript.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuJavascriptClass")));
            this.menuLogicConsole.add(mmenuJavascript);

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

            WebMarkupContainer mmenuPage = new WebMarkupContainer("mmenuPage");
            mmenuPage.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuPageClass")));
            this.menuLogicConsole.add(mmenuPage);

            WebMarkupContainer mmenuMaster = new WebMarkupContainer("mmenuMaster");
            mmenuMaster.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuMasterClass")));
            this.menuLogicConsole.add(mmenuMaster);

            WebMarkupContainer mmenuBlock = new WebMarkupContainer("mmenuBlock");
            mmenuBlock.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuBlockClass")));
            this.menuLogicConsole.add(mmenuBlock);

            WebMarkupContainer mmenuMenu = new WebMarkupContainer("mmenuMenu");
            mmenuMenu.add(AttributeModifier.replace("class", new PropertyModel<>(this, "mmenuMenuClass")));
            this.menuLogicConsole.add(mmenuMenu);
        }

        String rootMenuId = null;
        {
            Map<String, Object> rootMenu = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.PARENT_MENU_ID + " IS NULL");
            rootMenuId = (String) rootMenu.get(Jdbc.Menu.MENU_ID);
            Label menuWebflowConsole = new Label("menuWebflowConsole", (String) rootMenu.get(Jdbc.Menu.TITLE));
            this.add(menuWebflowConsole);
        }
        {
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.PARENT_MENU_ID + " = ?", rootMenuId);
            RepeatingView menus = new RepeatingView("menus");
            add(menus);
            for (Map<String, Object> item : items) {
                String menuId = (String) item.get(Jdbc.Menu.MENU_ID);
                this.mmenuItems.put(menuId, "treeview");
                WebMarkupContainer menu = new WebMarkupContainer(menus.newChildId());
                menu.add(AttributeModifier.replace("class", new PropertyModel<>(this.mmenuItems, menuId)));
                menus.add(menu);
                Label menuLabel = new Label("menuLabel", (String) item.get(Jdbc.Menu.TITLE));
                menu.add(menuLabel);
                List<Map<String, Object>> pageRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.MENU_ID + " = ?", menuId);
                RepeatingView pages = new RepeatingView("pages");
                menu.add(pages);
                for (Map<String, Object> pageRecord : pageRecords) {
                    String pageId = (String) pageRecord.get(Jdbc.Page.PAGE_ID);
                    Roles roles = new Roles();
                    roles.addAll(jdbcTemplate.queryForList("SELECT " + Jdbc.ROLE + "." + Jdbc.Role.NAME + " FROM " + Jdbc.PAGE_ROLE + " JOIN " + Jdbc.ROLE + " ON " + Jdbc.PAGE_ROLE + "." + Jdbc.PageRole.ROLE_ID + " = " + Jdbc.ROLE + "." + Jdbc.Role.ROLE_ID + " WHERE " + Jdbc.PAGE_ROLE + "." + Jdbc.PageRole.PAGE_ID + " = ?", String.class, pageId));
                    if (ApplicationUtils.getApplication().hasAnyRole(roles)) {
                        WebMarkupContainer page = new WebMarkupContainer(pages.newChildId());
                        page.add(AttributeModifier.replace("class", new PropertyModel<>(this.mmenuPages, pageId)));
                        pages.add(page);
                        this.mmenuPages.put(pageId, "");
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add("pageId", pageId);
                        BookmarkablePageLink<Void> pageLink = new BookmarkablePageLink<>("pageLink", PagePage.class, pageParameters);
                        page.add(pageLink);
                        Label pageLabel = new Label("pageLabel", (String) pageRecord.get(Jdbc.Page.TITLE));
                        pageLink.add(pageLabel);
                    }
                }
            }
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        boolean isAdministrator = getSession().isAdministrator();
        boolean isRegistered = getSession().isRegistered();
        this.menuSecurity.setVisible(isAdministrator);
        this.menuSession.setVisible(isAdministrator);
        this.menuLogicConsole.setVisible(isAdministrator);
        this.mmenuFile.setVisible(isAdministrator);
        this.mmenuAsset.setVisible(isAdministrator);
        this.menuStorage.setVisible(this.mmenuAsset.isVisible() || this.mmenuFile.isVisible());

        // Parent Menu
        for (String key : this.mmenuItems.keySet()) {
            this.mmenuItems.put(key, "treeview");
        }
        for (String key : this.mmenuPages.keySet()) {
            this.mmenuPages.put(key, "");
        }

        if (getPage() instanceof PagePage) {
            String pageId = getPageParameters().get("pageId").toString("");
            Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", pageId);
            String menuId = (String) pageRecord.get(Jdbc.Page.MENU_ID);
            this.mmenuItems.put(menuId, "treeview active");
            this.mmenuPages.put(pageId, "active");
        }

        if (isRegistered || getPage() instanceof InformationPage || getPage() instanceof TimeOTPPage || getPage() instanceof TwoMailPage || getPage() instanceof PasswordPage) {
            this.menuProfileClass = "treeview active";
        } else {
            this.menuProfileClass = "treeview";
        }

        if (getPage() instanceof UserManagementPage
                || getPage() instanceof UserModifyPage
                || getPage() instanceof UserCreatePage
                || getPage() instanceof RoleManagementPage
                || getPage() instanceof RoleModifyPage
                || getPage() instanceof RoleCreatePage) {
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

        if (getPage() instanceof JavascriptManagementPage || getPage() instanceof JavascriptCreatePage || getPage() instanceof JavascriptModifyPage) {
            this.menuPluginClass = "treeview active";
        } else {
            this.menuPluginClass = "treeview";
        }

        // Menu
        if (getPage() instanceof ClientManagementPage
                || getPage() instanceof ClientModifyPage
                || getPage() instanceof ClientCreatePage) {
            this.mmenuClientClass = "active";
        } else {
            this.mmenuClientClass = "";
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
        if (getPage() instanceof CollectionManagementPage
                || getPage() instanceof CollectionCreatePage
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
                || getPage() instanceof QueryCreatePage
                || getPage() instanceof QueryModifyPage
                || getPage() instanceof QueryParameterModifyPage) {
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
        if (getPage() instanceof SessionMobilePage) {
            this.menuSessionClass = "treeview active";
            this.mmenuMobileClass = "active";
        } else {
            this.menuSessionClass = "treeview";
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

        if (getPage() instanceof PageCreatePage || getPage() instanceof PageManagementPage || getPage() instanceof PageModifyPage) {
            this.mmenuPageClass = "active";
        } else {
            this.mmenuPageClass = "";
        }

        if (getPage() instanceof BlockCreatePage || getPage() instanceof BlockManagementPage || getPage() instanceof BlockModifyPage) {
            this.mmenuBlockClass = "active";
        } else {
            this.mmenuBlockClass = "";
        }

        if (getPage() instanceof MasterCreatePage || getPage() instanceof MasterManagementPage || getPage() instanceof MasterModifyPage) {
            this.mmenuMasterClass = "active";
        } else {
            this.mmenuMasterClass = "";
        }

        if (getPage() instanceof MenuCreatePage || getPage() instanceof MenuManagementPage || getPage() instanceof MenuModifyPage) {
            this.mmenuMenuClass = "active";
        } else {
            this.mmenuMenuClass = "";
        }
    }

    public final String getApplicationCode() {
        Session session = getSession();
        return session.getApplicationCode();
    }

    private void logoutLinkOnClick(Link link) {
        getSession().invalidateNow();
        setResponsePage(LoginPage.class);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }

    public final DSLContext getApplicationDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext(getSession().getApplicationCode());
    }

    public final String getNavigatorLanguage() {
        return getSession().getClientInfo().getProperties().getNavigatorLanguage();
    }

    public final JdbcTemplate getJdbcTemplate() {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate();
    }

    public final JdbcTemplate getApplicationJdbcTemplate() {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate(getSession().getApplicationCode());
    }

    public final Schema getApplicationSchema() {
        Application application = (Application) getApplication();
        return application.getSchema(getSession().getApplicationCode());
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

    public DbSupport getDbSupport() {
        Application application = (Application) getApplication();
        return application.getDbSupport();
    }

    public ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        Application application = (Application) getApplication();
        return application.getApplicationDataSource();
    }

    public JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        Application application = (Application) getApplication();
        return application.getJavascriptService();
    }

    public ScriptEngineFactory getScriptEngineFactory() {
        Application application = (Application) getApplication();
        return application.getScriptEngineFactory();
    }

    public ClassFilter getClassFilter() {
        Application application = (Application) getApplication();
        return application.getClassFilter();
    }

    public ScriptEngine getScriptEngine() {
        Application application = (Application) getApplication();
        return application.getScriptEngine();
    }
}
