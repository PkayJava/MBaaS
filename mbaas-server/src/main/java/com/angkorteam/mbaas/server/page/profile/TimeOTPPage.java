package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserTotpStatusEnum;
import com.angkorteam.mbaas.plain.security.otp.api.Base32;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
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

    private String status;
    private Label statusLabel;

    private String otp;
    private TextField<String> otpField;
    private TextFeedbackPanel otpFeedback;

    private Button revokeButton;
    private Button activateButton;

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

        String secret = UUID.randomUUID().toString() + "||" + Base32.random();
        String api = HttpFunction.getHttpAddress(request) + "/api/qr?secret=" + secret;

        this.form = new Form<>("form");
        add(this.form);

        if (!granted) {
            userRecord.setTotpSecret(secret);
            userRecord.setTotpStatus(UserTotpStatusEnum.Denied.getLiteral());
            userRecord.update();
        }

        this.status = userRecord.getTotpStatus();
        this.statusLabel = new Label("statusLabel", new PropertyModel<>(this, "status"));
        this.form.add(this.statusLabel);

        this.otpField = new PasswordTextField("otpField", new PropertyModel<>(this, "otp"));
        this.otpField.setRequired(true);
        this.otpField.setLabel(JooqUtils.lookup("currentPassword", this));
        this.form.add(this.otpField);
        this.otpFeedback = new TextFeedbackPanel("otpFeedback", this.otpField);
        this.form.add(this.otpField);

        this.secretImage = new ExternalImage("secretImage", api);
        this.form.add(secretImage);

        this.revokeButton = new Button("revokeButton");
        this.revokeButton.setOnSubmit(this::revokeButtonOnSubmit);
        this.form.add(this.revokeButton);
        this.revokeButton.setVisible(granted);
    }

    private void revokeButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(getSession().getUserId())).fetchOneInto(userTable);
        userRecord.setTotpSecret(null);
        userRecord.setTotpStatus(UserTotpStatusEnum.Denied.getLiteral());
        userRecord.update();
        setResponsePage(TimeOTPPage.class);
    }

}
