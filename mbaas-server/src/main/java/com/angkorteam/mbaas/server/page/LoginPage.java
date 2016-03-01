package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/login")
public class LoginPage extends Page {

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

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setRequired(true);
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);

        this.okayButton = new Button("okayButton");
        this.okayButton.setOnSubmit(this::okayButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(this.loginField);
        this.form.add(this.loginFeedback);

        this.form.add(this.passwordField);
        this.form.add(this.passwordFeedback);

        this.form.add(this.okayButton);
    }

    private void okayButtonOnSubmit(Button components) {
        boolean signin = getSession().signIn(this.login, this.password);
        if (signin) {
            continueToOriginalDestination();
        } else {
            this.loginField.error("incorrect");
            this.passwordField.error("incorrect");
        }
    }
}
