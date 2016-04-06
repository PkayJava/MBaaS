package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserTotpStatusEnum;
import com.angkorteam.mbaas.plain.security.otp.Totp;
import com.angkorteam.mbaas.plain.security.otp.api.Base32;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by socheat on 4/3/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice", "registered"})
@Mount("/profile/otp")
public class TimeOTPPage extends MasterPage {

    private Form<Void> form;

    private ExternalImage secretImage;

    private Integer otp;
    private TextField<Integer> otpField;
    private TextFeedbackPanel otpFeedback;

    private Button revokeButton;
    private Button verifyButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(getSession().getUserId())).fetchOneInto(userTable);

        boolean granted = false;
        if (userRecord.getTotpSecret() != null
                && !"".equals(userRecord.getTotpSecret())
                && AuthenticationEnum.TOTP.getLiteral().equals(userRecord.getAuthentication())
                && UserTotpStatusEnum.Granted.getLiteral().equals(userRecord.getTotpStatus())) {
            granted = true;
        }

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

        String secret = UUID.randomUUID().toString();
        String hash = Base32.random();
        String api = HttpFunction.getHttpAddress(request) + "/api/qr?secret=" + secret + "||" + hash;

        this.form = new Form<>("form");
        add(this.form);

        if (!granted) {
            userRecord.setTotpSecret(secret);
            userRecord.setTotpHash(hash);
            userRecord.setTotpStatus(UserTotpStatusEnum.Denied.getLiteral());
            userRecord.update();
        }

        this.otpField = new TextField<>("otpField", new PropertyModel<>(this, "otp"));
        this.otpField.setRequired(true);
        this.otpField.setLabel(JooqUtils.lookup("One Time Password", this));
        this.otpField.setVisible(!granted);
        this.form.add(this.otpField);
        this.otpFeedback = new TextFeedbackPanel("otpFeedback", this.otpField);
        this.form.add(this.otpFeedback);

        this.secretImage = new ExternalImage("secretImage", api);
        this.secretImage.setVisible(!granted);
        this.form.add(secretImage);

        this.revokeButton = new Button("revokeButton");
        this.revokeButton.setOnSubmit(this::revokeButtonOnSubmit);
        this.form.add(this.revokeButton);
        this.revokeButton.setVisible(granted);

        this.verifyButton = new Button("verifyButton");
        this.verifyButton.setOnSubmit(this::verifyButtonOnSubmit);
        this.form.add(this.verifyButton);
        this.verifyButton.setVisible(!granted);
    }

    private void verifyButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(getSession().getUserId())).fetchOneInto(userTable);
        Totp totp = new Totp(StringUtils.split(userRecord.getTotpSecret(), "||")[1]);
        if (totp.verify(String.valueOf(this.otp))) {
            userRecord.setTotpStatus(UserTotpStatusEnum.Granted.getLiteral());
            userRecord.setAuthentication(AuthenticationEnum.TOTP.getLiteral());
            userRecord.update();
            setResponsePage(InformationPage.class);
        } else {
            this.otpField.error("invalid");
        }
    }

    private void revokeButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(getSession().getUserId())).fetchOneInto(userTable);
        userRecord.setTotpSecret(null);
        userRecord.setTotpStatus(UserTotpStatusEnum.Denied.getLiteral());
        userRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
        userRecord.update();
        setResponsePage(InformationPage.class);
    }

}
