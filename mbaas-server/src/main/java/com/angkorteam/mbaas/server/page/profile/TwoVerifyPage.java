package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 4/3/16.
 */
@AuthorizeInstantiation({"administrator", "registered"})
@Mount("/profile/two/verify")
public class TwoVerifyPage extends MasterPage {

    private String type;
    private Integer verify;
    private String recipient;

    private Integer otp;
    private TextField<Integer> otpField;
    private TextFeedbackPanel otpFeedback;

    private Form<Void> form;

    private Button verifyButton;

    public TwoVerifyPage(String type, Integer verify, String recipient) {
        this.type = type;
        this.verify = verify;
        this.recipient = recipient;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.otpField = new TextField<>("otpField", new PropertyModel<>(this, "otp"));
        this.otpField.setRequired(true);
        this.otpField.setLabel(JooqUtils.lookup("One Time Password", this));
        this.form.add(this.otpField);
        this.otpFeedback = new TextFeedbackPanel("otpFeedback", this.otpField);
        this.form.add(this.otpFeedback);

        this.verifyButton = new Button("verifyButton");
        this.verifyButton.setOnSubmit(this::verifyButtonOnSubmit);
        this.form.add(this.verifyButton);
    }

    private void verifyButtonOnSubmit(Button button) {
        if (!this.verify.equals(this.otp)) {
            if (AuthenticationEnum.TwoEMail.getLiteral().equals(this.type)) {
                setResponsePage(TwoMailPage.class);
                return;
            } else if (AuthenticationEnum.TwoSMS.getLiteral().equals(this.type)) {
//                setResponsePage(TwoSMSPage.class);
                return;
            }
        }

        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.User.USER_ID, getSession().getApplicationUserId());
        Map<String, Object> fields = new HashMap<>();
        if (AuthenticationEnum.TwoEMail.getLiteral().equals(this.type)) {
            fields.put(Jdbc.User.EMAIL_ADDRESS, this.recipient);
        } else if (AuthenticationEnum.TwoSMS.getLiteral().equals(this.type)) {
            fields.put(Jdbc.User.MOBILE_NUMBER, this.recipient);
        }
        fields.put(Jdbc.User.AUTHENTICATION, this.type);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.USER);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(InformationPage.class);
    }

}
