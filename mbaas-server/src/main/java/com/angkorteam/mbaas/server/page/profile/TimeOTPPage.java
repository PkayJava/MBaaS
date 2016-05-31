package com.angkorteam.mbaas.server.page.profile;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserTotpStatusEnum;
import com.angkorteam.mbaas.plain.security.otp.Totp;
import com.angkorteam.mbaas.plain.security.otp.api.Base32;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 4/3/16.
 */
@AuthorizeInstantiation({"administrator", "registered"})
@Mount("/profile/otp")
public class TimeOTPPage extends MasterPage {

    private Form<Void> form;

    private ExternalImage secretImage;

    private Integer otp;
    private TextField<Integer> otpField;
    private TextFeedbackPanel otpFeedback;

    private String api;

    private Button revokeButton;
    private Button verifyButton;

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

        this.secretImage = new ExternalImage("secretImage", new PropertyModel<>(this, "api"));
        this.form.add(secretImage);

        this.revokeButton = new Button("revokeButton");
        this.revokeButton.setOnSubmit(this::revokeButtonOnSubmit);
        this.form.add(this.revokeButton);

        this.verifyButton = new Button("verifyButton");
        this.verifyButton.setOnSubmit(this::verifyButtonOnSubmit);
        this.form.add(this.verifyButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ?", getSession().getApplicationUserId());

        boolean granted = false;
        String totpSecret = (String) userRecord.get(Jdbc.User.TOTP_SECRET);
        if (totpSecret != null
                && !"".equals(totpSecret)
                && AuthenticationEnum.TOTP.getLiteral().equals(userRecord.get(Jdbc.User.AUTHENTICATION))
                && UserTotpStatusEnum.Granted.getLiteral().equals(userRecord.get(Jdbc.User.TOTP_STATUS))) {
            granted = true;
        }

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

        String secret = UUID.randomUUID().toString();
        String hash = Base32.random();
        this.api = HttpFunction.getHttpAddress(request) + "/api/qr?secret=" + secret + "||" + hash + "||" + getSession().getApplicationCode();

        if (!granted) {
            jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.TOTP_SECRET + " = ?, " + Jdbc.User.TOTP_HASH + " = ?, " + Jdbc.User.TOTP_STATUS + " = ? WHERE " + Jdbc.User.USER_ID + " = ?", secret, hash, UserTotpStatusEnum.Denied.getLiteral(), getSession().getApplicationUserId());
        }

        this.otpField.setVisible(!granted);
        this.secretImage.setVisible(!granted);
        this.revokeButton.setVisible(granted);
        this.verifyButton.setVisible(!granted);
    }

    private void verifyButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ?", getSession().getApplicationUserId());
        Totp totp = new Totp(StringUtils.split((String) userRecord.get(Jdbc.User.TOTP_SECRET), "||")[1]);
        if (totp.verify(String.valueOf(this.otp))) {
            jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.TOTP_STATUS + " = ?, " + Jdbc.User.AUTHENTICATION + " = ? WHERE " + Jdbc.User.USER_ID + " = ?", UserTotpStatusEnum.Granted.getLiteral(), AuthenticationEnum.TOTP.getLiteral(), getSession().getApplicationUserId());
            setResponsePage(InformationPage.class);
        } else {
            this.otpField.error("invalid");
        }
    }

    private void revokeButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ?", getSession().getApplicationUserId());
        jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.TOTP_SECRET + " = ?, " + Jdbc.User.TOTP_STATUS + " = ?, " + Jdbc.User.AUTHENTICATION + " = ? WHERE " + Jdbc.User.USER_ID + " = ?", null, UserTotpStatusEnum.Denied.getLiteral(), AuthenticationEnum.None.getLiteral(), getSession().getApplicationUserId());
        setResponsePage(InformationPage.class);
    }

}
