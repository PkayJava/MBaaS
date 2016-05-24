package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

/**
 * Created by socheat on 4/3/16.
 */
@AuthorizeInstantiation({"mbaas.administrator", "mbaas.system"})
@Mount("/mbaas/profile/two/verify")
public class TwoVerifyPage extends MBaaSPage {

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
                //setResponsePage(TwoSMSPage.class);
                return;
            }
        }
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        MbaasUserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.MBAAS_USER_ID.eq(getSession().getMbaasUserId())).fetchOneInto(userTable);
        if (AuthenticationEnum.TwoEMail.getLiteral().equals(this.type)) {
            userRecord.setEmailAddress(this.recipient);
        } else if (AuthenticationEnum.TwoSMS.getLiteral().equals(this.type)) {
            userRecord.setMobileNumber(this.recipient);
        }
        userRecord.setAuthentication(this.type);
        userRecord.update();
        setResponsePage(InformationPage.class);
    }

}
