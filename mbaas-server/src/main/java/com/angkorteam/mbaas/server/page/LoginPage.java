package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.server.validator.HostnameValidator;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/login")
public class LoginPage extends AdminLTEPage {

    private String hostname;
    private TextField<String> hostnameField;
    private TextFeedbackPanel hostnameFeedback;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private Button loginButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        DSLContext context = ApplicationUtils.getApplication().getDSLContext();

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        String hostname = request.getServerName();
        this.hostname = hostname;
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);

        this.hostnameField = new TextField<>("hostnameField", new PropertyModel<>(this, "hostname"));
        if (hostnameRecord != null) {
            this.hostnameField.setRequired(false);
            this.hostnameField.setVisible(false);
        } else {
            this.hostnameField.setRequired(true);
            this.hostnameField.setVisible(true);
        }
        this.hostnameField.setLabel(JooqUtils.lookup("hostname", this));
        this.hostnameField.add(new HostnameValidator());
        this.form.add(this.hostnameField);
        this.hostnameFeedback = new TextFeedbackPanel("hostnameFeedback", this.hostnameField);
        this.form.add(this.hostnameFeedback);

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

        this.loginButton = new Button("loginButton");
        this.loginButton.setOnSubmit(this::loginButtonOnSubmit);
        this.form.add(this.loginButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void loginButtonOnSubmit(Button button) {
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        DSLContext context = ApplicationUtils.getApplication().getDSLContext();
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);
        boolean signin = getSession().applicationSignIn(applicationRecord.getSecret(), this.login, this.password);
        if (signin) {
            setResponsePage(getApplication().getHomePage());
        } else {
            this.loginField.error("incorrect");
            this.passwordField.error("incorrect");
        }
    }
}
