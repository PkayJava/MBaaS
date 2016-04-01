package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.response.oauth2.OAuth2PasswordResponse;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.oauth2.OAuth2Client;
import com.angkorteam.mbaas.server.oauth2.OAuth2DTO;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.http.HttpStatus;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by socheat on 4/1/16.
 */
@Mount("/oauth2/password")
public class PasswordPage extends AdminLTEPage {

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

        this.form = new Form<>("form");
        add(this.form);

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

        this.okayButton = new Button("okayButton");
        this.okayButton.setOnSubmit(this::okayButtonOnSubmit);
        this.form.add(this.okayButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void okayButtonOnSubmit(Button button) {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

        String httpAddress = HttpFunction.getHttpAddress(request);
        if (!httpAddress.endsWith("/")) {
            httpAddress = httpAddress + "/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(httpAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String grantType = "password";

        OAuth2Client client = retrofit.create(OAuth2Client.class);
        Call<OAuth2PasswordResponse> responseCall = client.oauth2Password(grantType, this.login, this.password, "");
        try {
            retrofit2.Response<OAuth2PasswordResponse> response = responseCall.execute();
            if (response.code() == HttpStatus.SC_OK) {
                OAuth2DTO oauth2DTO = new OAuth2DTO();
                oauth2DTO.setState(UUID.randomUUID().toString());
                oauth2DTO.setAccessToken(response.body().getAccessToken());
                oauth2DTO.setExpiresIn(response.body().getExpiresIn());
                oauth2DTO.setRefreshToken(response.body().getRefreshToken());
                oauth2DTO.setTokenType(response.body().getTokenType());
                getSession().setAttribute(oauth2DTO.getState(), oauth2DTO);
                PageParameters parameters = new PageParameters();
                parameters.add("state", oauth2DTO.getState());
                setResponsePage(AccessTokenPage.class, parameters);
            }
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
    }
}
