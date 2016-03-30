package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValue;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * Created by socheat on 3/27/16.
 */
@Mount("/oauth2/authorize")
public class AuthorizePage extends AdminLTEPage {

    private String applicationText;
    private Label applicationLabel;

    private String client;
    private Label clientLabel;

    private String applicationId;
    private String clientId;
    private String responseType;
    private String redirectUri;
    private String state;
    private String scope;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private Button okayButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        StringValue clientId = getPageParameters().get("client_id");
        StringValue responseType = getPageParameters().get("response_type");
        StringValue redirectUri = getPageParameters().get("redirect_uri");
        StringValue state = getPageParameters().get("state");
        StringValue scope = getPageParameters().get("scope");

        this.clientId = clientId.toString("");
        this.responseType = responseType.toString("");
        this.redirectUri = redirectUri.toString("");
        this.state = state.toString("");
        this.scope = scope.toString("");

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
            this.applicationId = applicationRecord.getApplicationId();
            this.applicationText = applicationRecord.getName();
        }

        this.form = new Form<>("form");
        add(this.form);

        this.applicationLabel = new Label("applicationLabel", new PropertyModel<>(this, "applicationText"));
        this.form.add(this.applicationLabel);

        this.clientLabel = new Label("clientLabel", new PropertyModel<>(this, "client"));
        this.form.add(this.clientLabel);

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.form.add(this.loginField);
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);
        this.form.add(this.loginFeedback);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setRequired(true);
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.okayButton = new Button("okayButton");
        this.okayButton.setOnSubmit(this::okayButtonOnSubmit);
        this.form.add(this.okayButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void okayButtonOnSubmit(Button components) {
        DSLContext context = getSession().getDSLContext();

        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(this.login)).and(userTable.PASSWORD.eq(DSL.md5(this.password))).fetchOneInto(userTable);

        if (userRecord != null) {
            PermissionPage permissionPage = new PermissionPage(this.applicationId, this.clientId, userRecord.getUserId(), this.responseType, this.redirectUri, this.state, this.scope);
            setResponsePage(permissionPage);
        } else {
            this.loginField.error("incorrect");
            this.passwordField.error("incorrect");
        }
    }
}
