package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/1/16.
 */

public class UserPasswordPage extends MBaaSPage {

    private String userId;

    private String fullName;
    private Label fullNameLabel;

    private String login;
    private Label loginLabel;

    private String password;
    private TextField<String> passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private TextField<String> retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private Button saveButton;
    private Form<Void> form;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return UserPasswordPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        UserTable userTable = Tables.USER.as("userTable");

        PageParameters parameters = getPageParameters();
        this.userId = parameters.get("userId").toString("");

        UserPojo user = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(this.userId)).fetchOneInto(UserPojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.fullName = user.getFullName();
        this.fullNameLabel = new Label("fullNameLabel", new PropertyModel<>(this, "fullName"));
        this.form.add(fullNameLabel);

        this.login = user.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(loginLabel);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.form.add(retypePasswordField);
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);
        this.form.add(retypePasswordFeedback);

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", UserBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        jdbcTemplate.update("UPDATE " + Tables.USER.getName() + " SET " + Tables.USER.PASSWORD.getName() + " = MD5(?) WHERE " + Tables.USER.USER_ID.getName() + " = ?", this.password);
        setResponsePage(UserBrowsePage.class);
    }
}
