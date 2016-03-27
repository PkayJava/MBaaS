package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.ApplicationPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.ClientPojo;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.server.renderer.ApplicationChoiceRenderer;
import com.angkorteam.mbaas.server.renderer.ClientChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/27/16.
 */
@Mount("/bearer")
public class BearerPage extends AdminLTEPage {

    private ApplicationPojo applicationText;
    private DropDownChoice<ApplicationPojo> applicationField;
    private TextFeedbackPanel applicationFeedback;

    private ClientPojo client;
    private DropDownChoice<ClientPojo> clientField;
    private TextFeedbackPanel clientFeedback;

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
        String secret = getPageParameters().get("secret").toString();

        DSLContext context = getSession().getDSLContext();
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        this.client = context.select(clientTable.fields()).from(clientTable).where(clientTable.SECRET.eq(secret)).fetchOneInto(ClientPojo.class);
        if (this.client != null) {
            this.applicationText = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(client.getApplicationId())).fetchOneInto(ApplicationPojo.class);
        }

        this.form = new Form<>("form");
        add(this.form);

        this.applicationField = new DropDownChoice<ApplicationPojo>("applicationField", new PropertyModel<>(this, "applicationText"), new PropertyModel<>(this, "applications"), new ApplicationChoiceRenderer()) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(ApplicationPojo newSelection) {
                super.onSelectionChanged(newSelection);
                client = null;
                clientField.clearInput();
            }
        };

        this.applicationField.setRequired(true);
        this.form.add(this.applicationField);
        this.applicationFeedback = new TextFeedbackPanel("applicationFeedback", this.applicationField);
        this.form.add(this.applicationFeedback);

        this.clientField = new DropDownChoice<>("clientField", new PropertyModel<>(this, "client"), new PropertyModel<>(this, "clients"), new ClientChoiceRenderer());
        this.clientField.setRequired(true);
        this.form.add(this.clientField);
        this.clientFeedback = new TextFeedbackPanel("clientFeedback", this.clientField);
        this.form.add(this.clientFeedback);

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

    public List<ApplicationPojo> getApplications() {
        DSLContext context = getSession().getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        return context.select(applicationTable.fields()).from(applicationTable).innerJoin(clientTable).on(applicationTable.APPLICATION_ID.eq(clientTable.APPLICATION_ID)).groupBy(applicationTable.APPLICATION_ID).fetchInto(ApplicationPojo.class);
    }


    public List<ClientPojo> getClients() {
        if (this.applicationText != null) {
            DSLContext context = getSession().getDSLContext();
            ClientTable clientTable = Tables.CLIENT.as("clientTable");
            return context.select(clientTable.fields()).from(clientTable).where(clientTable.APPLICATION_ID.eq(this.applicationText.getApplicationId())).fetchInto(ClientPojo.class);
        }
        return new ArrayList<>(0);
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
            MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
            MobileRecord mobileRecord = context.newRecord(mobileTable);
            String mobileId = UUID.randomUUID().toString();
            mobileRecord.setMobileId(mobileId);
            mobileRecord.setApplicationId(this.applicationText.getApplicationId());
            mobileRecord.setClientId(this.client.getClientId());
            mobileRecord.setDateCreated(new Date());
            mobileRecord.setUserAgent(getSession().getClientInfo().getUserAgent());
            mobileRecord.setClientIp(getSession().getClientInfo().getProperties().getRemoteAddress());
            mobileRecord.setUserId(userRecord.getUserId());
            mobileRecord.store();
            BearerTokenPage tokenPage = new BearerTokenPage(mobileId);
            setResponsePage(tokenPage);
        } else {
            this.loginField.error("incorrect");
            this.passwordField.error("incorrect");
        }
    }
}
