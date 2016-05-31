package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/login")
public class LoginPage extends AdminLTEPage {

    private String secret;
    private TextField<String> secretField;
    private TextFeedbackPanel secretFeedback;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private Button loginButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.secret = "d9495747-fbbc-40da-bbb2-b0302b44fbff";

        this.form = new Form<>("form");
        add(this.form);

        this.secretField = new TextField<>("secretField", new PropertyModel<>(this, "secret"));
        this.secretField.setRequired(true);
        this.secretField.setLabel(JooqUtils.lookup("secret", this));
        this.form.add(this.secretField);
        this.secretFeedback = new TextFeedbackPanel("secretFeedback", this.secretField);
        this.form.add(this.secretFeedback);

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

        this.loginButton = new Button("loginButton");
        this.loginButton.setOnSubmit(this::loginButtonOnSubmit);
        this.form.add(this.loginButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void loginButtonOnSubmit(Button button) {
        boolean signin = getSession().applicationSignIn(this.secret, this.login, this.password);
        if (signin) {
            setResponsePage(getApplication().getHomePage());
        } else {
            this.loginField.error("incorrect");
            this.passwordField.error("incorrect");
        }
    }
}
