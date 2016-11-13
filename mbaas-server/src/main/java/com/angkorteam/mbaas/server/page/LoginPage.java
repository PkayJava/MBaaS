package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.ValidationError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by socheat on 10/23/16.
 */
public class LoginPage extends AdminLTEPage {

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private List<String> languages = Arrays.asList("English", "ភាសាខ្មែរ");
    private String language;
    private DropDownChoice<String> languageField;
    private TextFeedbackPanel languageFeedback;


    private Form<Void> form;
    private Button loginButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        this.add(this.form);
        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.form.add(this.loginField);
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);
        this.form.add(this.loginFeedback);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setRequired(true);
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.languageField = new DropDownChoice<String>("languageField", new PropertyModel<>(this, "language"), new PropertyModel<>(this, "languages")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newSelection) {
                if (StringUtils.equalsIgnoreCase("ភាសាខ្មែរ", newSelection)) {
                    this.getSession().setLocale(new Locale("km"));
                } else {
                    this.getSession().setLocale(new Locale("en"));
                }
            }
        };
        this.languageField.setRequired(true);
        this.form.add(this.languageField);
        this.languageFeedback = new TextFeedbackPanel("languageFeedback", this.languageField);
        this.form.add(this.languageFeedback);

        this.loginButton = new Button("loginButton");
        this.loginButton.setOnSubmit(this::loginButtonOnSubmit);
        this.form.add(this.loginButton);

        if (AbstractAuthenticatedWebSession.get().isSignedIn()) {
            setResponsePage(getApplication().getHomePage());
        }
    }

    private void loginButtonOnSubmit(Button button) {
        boolean valid = Session.get().signIn(this.login, this.password);
        if (valid) {
            setResponsePage(getApplication().getHomePage());
        } else {
            this.loginField.error(new ValidationError().addKey("incorrect"));
            this.passwordField.error(new ValidationError().addKey("incorrect"));
        }
    }

}
