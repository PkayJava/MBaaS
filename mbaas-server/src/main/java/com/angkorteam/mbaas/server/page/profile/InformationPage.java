package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.UserEmailAddressValidator;
import com.angkorteam.mbaas.server.validator.UserMobileNumberValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 4/2/16.
 */
@AuthorizeInstantiation({"administrator", "registered"})
@Mount("/profile/information")
public class InformationPage extends MasterPage {

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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.APPLICATION_USER + " WHERE " + Jdbc.ApplicationUser.APPLICATION_USER_ID + " =?", getSession().getApplicationUserId());

        this.form = new Form<>("form");
        add(this.form);

        this.login = (String) userRecord.get(Jdbc.ApplicationUser.LOGIN);
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(this.loginLabel);

        this.authentication = (String) userRecord.get(Jdbc.ApplicationUser.AUTHENTICATION);
        this.authenticationLabel = new Label("authenticationLabel", new PropertyModel<>(this, "authentication"));
        this.form.add(this.authenticationLabel);

        this.emailAddress = (String) userRecord.get(Jdbc.ApplicationUser.EMAIL_ADDRESS);
        this.emailAddressField = new TextField<>("emailAddressField", new PropertyModel<>(this, "emailAddress"));
        if (AuthenticationEnum.TwoEMail.getLiteral().equals(this.authentication)) {
            this.emailAddressField.setRequired(true);
        } else {
            if (this.emailAddress != null && !"".equals(this.emailAddress)) {
                this.emailAddressField.setRequired(true);
            }
        }
        this.emailAddressField.add(EmailAddressValidator.getInstance());
        this.emailAddressField.add(new UserEmailAddressValidator(getSession().getApplicationCode(), getSession().getApplicationUserId()));
        this.form.add(this.emailAddressField);
        this.emailAddressFeedback = new TextFeedbackPanel("emailAddressFeedback", this.emailAddressField);
        this.form.add(this.emailAddressFeedback);

        this.mobileNumber = (String) userRecord.get(Jdbc.ApplicationUser.MOBILE_NUMBER);
        this.mobileNumberField = new TextField<>("mobileNumberField", new PropertyModel<>(this, "mobileNumber"));
        if (AuthenticationEnum.TwoSMS.getLiteral().equals(this.authentication)) {
            this.mobileNumberField.setRequired(true);
        } else {
            if (this.mobileNumber != null && !"".equals(this.mobileNumber)) {
                this.mobileNumberField.setRequired(true);
            }
        }
        this.mobileNumberField.add(new UserMobileNumberValidator(getSession().getApplicationCode(), getSession().getApplicationUserId()));
        this.form.add(this.mobileNumberField);
        this.mobileNumberFeedback = new TextFeedbackPanel("mobileNumberFeedback", this.mobileNumberField);
        this.form.add(this.mobileNumberFeedback);

        this.updateButton = new Button("updateButton");
        this.updateButton.setOnSubmit(this::updateButtonOnSubmit);
        this.form.add(this.updateButton);
    }

    private void updateButtonOnSubmit(Button button) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.ApplicationUser.EMAIL_ADDRESS, this.emailAddress);
        fields.put(Jdbc.ApplicationUser.MOBILE_NUMBER, this.mobileNumber);
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.ApplicationUser.APPLICATION_USER_ID, getSession().getApplicationUserId());
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.APPLICATION_USER);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(InformationPage.class);
    }
}
