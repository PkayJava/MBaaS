package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.AuthorizationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.AuthorizationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/27/16.
 */
@Mount("/oauth2/permission")
public class PermissionPage extends AdminLTEPage {

    private String applicationText;
    private Label applicationLabel;

    private String client;
    private Label clientLabel;

    private String applicationId;
    private String clientId;
    private String userId;
    private String responseType;
    private String redirectUri;
    private String state;
    private String scope;

    private Button grantButton;
    private Button denyButton;

    private Form<Void> form;
    private boolean denied;

    public PermissionPage() {
        setResponsePage(StarterPage.class);
    }

    public PermissionPage(String applicationId, String clientId, String userId, String responseType, String redirectUri, String state, String scope) {
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.userId = userId;
        this.responseType = responseType;
        this.redirectUri = redirectUri;
        this.state = state;
        this.scope = scope;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (this.denied) {
            setResponsePage(StarterPage.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getSession().getDSLContext();
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(this.clientId)).fetchOneInto(clientTable);

        ApplicationRecord applicationRecord = null;
        if (clientRecord != null) {
            this.client = clientRecord.getName();
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord != null) {
            this.applicationText = applicationRecord.getName();
        }

        if (applicationRecord != null) {
            this.scope = applicationRecord.getOauthRoles();
        }

        this.form = new Form<>("form");
        add(this.form);

        this.applicationLabel = new Label("applicationLabel", new PropertyModel<>(this, "applicationText"));
        this.add(this.applicationLabel);

        this.clientLabel = new Label("clientLabel", new PropertyModel<>(this, "client"));
        this.add(this.clientLabel);

        RepeatingView permissions = new RepeatingView("permissions");
        if (this.scope != null) {
            for (String oauthRole : this.scope.split(", ")) {
                WebMarkupContainer container = new WebMarkupContainer(permissions.newChildId());
                permissions.add(container);
                Label permission = new Label("permission", oauthRole);
                container.add(permission);
            }
        } else {
            permissions.setVisible(false);
        }
        this.form.add(permissions);

        this.grantButton = new Button("grantButton");
        this.grantButton.setOnSubmit(this::grantButtonOnSubmit);
        this.form.add(this.grantButton);

        this.denyButton = new Button("denyButton");
        this.denyButton.setOnSubmit(this::denyButtonOnSubmit);
        this.form.add(this.denyButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void grantButtonOnSubmit(Button button) {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        this.denied = true;
        List<String> params = new ArrayList<>();
        if (this.state != null && !"".equals(this.state)) {
            params.add("state=" + this.state);
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Integer timeToLive = configuration.getInt(Constants.AUTHORIZATION_TIME_TO_LIVE);

        DSLContext context = getSession().getDSLContext();
        AuthorizationTable authorizationTable = Tables.AUTHORIZATION.as("authorizationTable");
        AuthorizationRecord authorizationRecord = context.newRecord(authorizationTable);
        String uuid = UUID.randomUUID().toString();
        authorizationRecord.setAuthorizationId(uuid);
        authorizationRecord.setDateCreated(new Date());
        authorizationRecord.setApplicationId(this.applicationId);
        authorizationRecord.setClientId(this.clientId);
        authorizationRecord.setOwnerUserId(this.userId);
        authorizationRecord.setState(this.state);
        authorizationRecord.setTimeToLive(timeToLive);
        authorizationRecord.store();
        params.add("code=" + authorizationRecord.getAuthorizationId());
        if (this.redirectUri == null || "".equals(this.redirectUri)) {
            this.redirectUri = HttpFunction.getHttpAddress(request) + "/web/oauth2/response";
        }
        if (params.isEmpty()) {
            setResponsePage(new RedirectPage(this.redirectUri));
        } else {
            setResponsePage(new RedirectPage(this.redirectUri + "?" + StringUtils.join(params, "&")));
        }
    }

    private void denyButtonOnSubmit(Button button) {
        this.denied = true;
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        getSession().removeAttribute(this.state);
        List<String> params = new ArrayList<>();
        if (this.state != null && !"".equals(this.state)) {
            params.add("state=" + this.state);
        }
        if (this.redirectUri == null || "".equals(this.redirectUri)) {
            this.redirectUri = HttpFunction.getHttpAddress(request) + "/web/oauth2/response";
        }
        params.add("error=consent_required");
        params.add("error_uri=");
        params.add("error_description=The user denied access to your application");
        if (params.isEmpty()) {
            setResponsePage(new RedirectPage(this.redirectUri));
        } else {
            setResponsePage(new RedirectPage(this.redirectUri + "?" + StringUtils.join(params, "&")));
        }
    }
}
