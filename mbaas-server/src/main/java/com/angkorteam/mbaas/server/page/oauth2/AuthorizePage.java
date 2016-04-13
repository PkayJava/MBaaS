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
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserTotpStatusEnum;
import com.angkorteam.mbaas.plain.security.otp.Totp;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValue;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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

    private Button loginButton;

    private Button cancelButton;

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
        this.form.setOutputMarkupId(true);
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

        this.loginButton = new Button("loginButton");
        this.loginButton.setOnSubmit(this::loginButtonOnSubmit);
        this.form.add(this.loginButton);

        this.cancelButton = new Button("cancelButton");
        this.cancelButton.setOnSubmit(this::cancelButtonOnSubmit);
        this.cancelButton.setDefaultFormProcessing(false);
        this.form.add(this.cancelButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void cancelButtonOnSubmit(Button button) {
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

    private void loginButtonOnSubmit(Button button) {
        DSLContext context = getSession().getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(this.login)).and(userTable.PASSWORD.eq(DSL.md5(this.password))).fetchOneInto(userTable);
        if (userRecord != null && !AuthenticationEnum.TOTP.getLiteral().equals(userRecord.getAuthentication())) {
            if (AuthenticationEnum.TwoEMail.getLiteral().equals(userRecord.getAuthentication())) {
                String verify = RandomStringUtils.randomNumeric(6);
                MailSender mailSender = getSession().getMailSender();
                SimpleMailMessage message = new SimpleMailMessage();
                message.setSubject("One Time Password");
                message.setText(verify);
                message.setTo(userRecord.getEmailAddress());
                try {
                    mailSender.send(message);
                } catch (MailAuthenticationException e) {
                }
                VerifyPage verifyPage = new VerifyPage(this.applicationId, this.clientId, userRecord.getUserId(), this.responseType, this.redirectUri, this.state, this.scope, userRecord.getEmailAddress(), userRecord.getAuthentication(), Integer.valueOf(verify));
                setResponsePage(verifyPage);
                return;
            } else if (AuthenticationEnum.TwoSMS.getLiteral().equals(userRecord.getAuthentication())) {
                return;
            } else {
                PermissionPage permissionPage = new PermissionPage(this.applicationId, this.clientId, userRecord.getUserId(), this.responseType, this.redirectUri, this.state, this.scope);
                setResponsePage(permissionPage);
                return;
            }
        } else {
            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(this.login)).fetchOneInto(userTable);
            if (userRecord != null
                    && AuthenticationEnum.TOTP.getLiteral().equals(userRecord.getAuthentication())
                    && UserTotpStatusEnum.Granted.getLiteral().equals(userRecord.getTotpStatus())) {
                String hash = userRecord.getTotpHash();
                Totp totp = new Totp(hash);
                try {
                    if (totp.verify(this.password)) {
                        PermissionPage permissionPage = new PermissionPage(this.applicationId, this.clientId, userRecord.getUserId(), this.responseType, this.redirectUri, this.state, this.scope);
                        setResponsePage(permissionPage);
                        return;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        this.loginField.error("incorrect");
        this.passwordField.error("incorrect");
    }
}
