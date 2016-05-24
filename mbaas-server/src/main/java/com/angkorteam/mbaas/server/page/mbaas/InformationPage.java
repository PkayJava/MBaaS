package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.server.validator.MBaaSUserEmailAddressValidator;
import com.angkorteam.mbaas.server.validator.MBaaSUserMobileNumberValidator;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.jooq.DSLContext;

/**
 * Created by socheat on 4/2/16.
 */
@AuthorizeInstantiation({"mbaas.administrator", "mbaas.system"})
@Mount("/mbaas/profile/information")
public class InformationPage extends MBaaSPage {

    private String login;
    private Label loginLabel;

    private String mobileNumber;
    private TextField<String> mobileNumberField;
    private TextFeedbackPanel mobileNumberFeedback;

    private String emailAddress;
    private TextField<String> emailAddressField;
    private TextFeedbackPanel emailAddressFeedback;

    private String authentication;
    private Label authenticationLabel;

    private Form<Void> form;

    private Button updateButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        MbaasUserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.MBAAS_USER_ID.eq(getSession().getMbaasUserId())).fetchOneInto(userTable);

        this.form = new Form<>("form");
        add(this.form);

        this.login = userRecord.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(this.loginLabel);

        this.authentication = userRecord.getAuthentication();
        this.authenticationLabel = new Label("authenticationLabel", new PropertyModel<>(this, "authentication"));
        this.form.add(this.authenticationLabel);

        this.emailAddress = userRecord.getEmailAddress();
        this.emailAddressField = new TextField<>("emailAddressField", new PropertyModel<>(this, "emailAddress"));
        if (AuthenticationEnum.TwoEMail.getLiteral().equals(this.authentication)) {
            this.emailAddressField.setRequired(true);
        } else {
            if (this.emailAddress != null && !"".equals(this.emailAddress)) {
                this.emailAddressField.setRequired(true);
            }
        }
        this.emailAddressField.add(EmailAddressValidator.getInstance());
        this.emailAddressField.add(new MBaaSUserEmailAddressValidator(getSession().getMbaasUserId()));
        this.form.add(this.emailAddressField);
        this.emailAddressFeedback = new TextFeedbackPanel("emailAddressFeedback", this.emailAddressField);
        this.form.add(this.emailAddressFeedback);

        this.mobileNumber = userRecord.getMobileNumber();
        this.mobileNumberField = new TextField<>("mobileNumberField", new PropertyModel<>(this, "mobileNumber"));
        if (AuthenticationEnum.TwoSMS.getLiteral().equals(this.authentication)) {
            this.mobileNumberField.setRequired(true);
        } else {
            if (this.mobileNumber != null && !"".equals(this.mobileNumber)) {
                this.mobileNumberField.setRequired(true);
            }
        }
        // TODO
        this.mobileNumberField.add(new MBaaSUserMobileNumberValidator(getSession().getMbaasUserId()));
        this.form.add(this.mobileNumberField);
        this.mobileNumberFeedback = new TextFeedbackPanel("mobileNumberFeedback", this.mobileNumberField);
        this.form.add(this.mobileNumberFeedback);

        this.updateButton = new Button("updateButton");
        this.updateButton.setOnSubmit(this::updateButtonOnSubmit);
        this.form.add(this.updateButton);
    }

    private void updateButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        MbaasUserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.MBAAS_USER_ID.eq(getSession().getMbaasUserId())).fetchOneInto(userTable);
        userRecord.setEmailAddress(this.emailAddress);
        userRecord.setMobileNumber(this.mobileNumber);
        userRecord.update();
        setResponsePage(InformationPage.class);
    }
}
