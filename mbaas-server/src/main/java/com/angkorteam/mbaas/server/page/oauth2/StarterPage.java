package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.ApplicationPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.ClientPojo;
import com.angkorteam.mbaas.plain.enums.GrantTypeEnum;
import com.angkorteam.mbaas.plain.response.oauth2.OAuth2ClientResponse;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.oauth2.OAuth2Client;
import com.angkorteam.mbaas.server.oauth2.OAuth2DTO;
import com.angkorteam.mbaas.server.renderer.ApplicationChoiceRenderer;
import com.angkorteam.mbaas.server.renderer.ClientChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/27/16.
 */
@Mount("/oauth2/starter")
public class StarterPage extends AdminLTEPage {

    private String oauth2;
    private DropDownChoice<String> oauth2Field;
    private TextFeedbackPanel oauth2Feedback;

    private ApplicationPojo applicationText;
    private DropDownChoice<ApplicationPojo> applicationField;
    private TextFeedbackPanel applicationFeedback;

    private ClientPojo client;
    private DropDownChoice<ClientPojo> clientField;
    private TextFeedbackPanel clientFeedback;

    private Button okayButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        String secret = getPageParameters().get("secret").toString();

        DSLContext context = getSession().getDSLContext();
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        this.client = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_SECRET.eq(secret)).fetchOneInto(ClientPojo.class);
        if (this.client != null) {
            this.applicationText = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(client.getApplicationId())).fetchOneInto(ApplicationPojo.class);
        }

        this.form = new Form<>("form");
        add(this.form);

        this.applicationField = new DropDownChoice<ApplicationPojo>("applicationField", new PropertyModel<>(this, "applicationText"), new PropertyModel<>(this, "applications"), new ApplicationChoiceRenderer()) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(ApplicationPojo newSelection) {
                super.onSelectionChanged(newSelection);
                client = null;
                clientField.clearInput();
            }
        };

        this.applicationField.setRequired(true);
        this.form.add(this.applicationField);
        this.applicationFeedback = new TextFeedbackPanel("applicationFeedback", this.applicationField);
        this.form.add(this.applicationFeedback);

        this.oauth2Field = new DropDownChoice<String>("oauth2Field", new PropertyModel<>(this, "oauth2"), new PropertyModel<>(this, "oauth2s")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newSelection) {
                super.onSelectionChanged(newSelection);
                if (GrantTypeEnum.Implicit.getLiteral().equals(newSelection)) {
                    applicationField.setRequired(true);
                    clientField.setRequired(true);
                } else if (GrantTypeEnum.Authorization.getLiteral().equals(newSelection)) {
                    applicationField.setRequired(true);
                    clientField.setRequired(true);
                } else if (GrantTypeEnum.Password.getLiteral().equals(newSelection)) {
                    applicationField.setRequired(false);
                    clientField.setRequired(false);
                } else if (GrantTypeEnum.Client.getLiteral().equals(newSelection)) {
                    applicationField.setRequired(false);
                    clientField.setRequired(false);
                }
            }
        };
        this.oauth2Field.setRequired(true);
        this.form.add(this.oauth2Field);
        this.oauth2Feedback = new TextFeedbackPanel("oauth2Feedback", this.oauth2Field);
        this.form.add(this.oauth2Feedback);

        this.clientField = new DropDownChoice<>("clientField", new PropertyModel<>(this, "client"), new PropertyModel<>(this, "clients"), new ClientChoiceRenderer());
        this.clientField.setRequired(true);
        this.form.add(this.clientField);
        this.clientFeedback = new TextFeedbackPanel("clientFeedback", this.clientField);
        this.form.add(this.clientFeedback);

        this.okayButton = new Button("okayButton");
        this.okayButton.setOnSubmit(this::okayButtonOnSubmit);
        this.form.add(this.okayButton);
    }

    public List<ApplicationPojo> getApplications() {
        DSLContext context = getSession().getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        return context.select(applicationTable.fields()).from(applicationTable).innerJoin(clientTable).on(applicationTable.APPLICATION_ID.eq(clientTable.APPLICATION_ID)).groupBy(applicationTable.APPLICATION_ID).fetchInto(ApplicationPojo.class);
    }

    public List<String> getOauth2s() {
        List<String> oauth2s = new ArrayList<>();
        for (GrantTypeEnum grantType : GrantTypeEnum.values()) {
            oauth2s.add(grantType.getLiteral());
        }
        return oauth2s;
    }

    public List<ClientPojo> getClients() {
        if (this.applicationText != null) {
            DSLContext context = getSession().getDSLContext();
            ClientTable clientTable = Tables.CLIENT.as("clientTable");
            return context.select(clientTable.fields()).from(clientTable).where(clientTable.APPLICATION_ID.eq(this.applicationText.getApplicationId())).fetchInto(ClientPojo.class);
        }
        return new ArrayList<>(0);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void okayButtonOnSubmit(Button components) {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        if (GrantTypeEnum.Authorization.getLiteral().equals(this.oauth2)
                || GrantTypeEnum.Implicit.getLiteral().equals(this.oauth2)) {
            OAuth2DTO oauth2DTO = new OAuth2DTO();
            oauth2DTO.setState(UUID.randomUUID().toString());
            oauth2DTO.setResponseType("code");
            oauth2DTO.setClientId(this.client.getClientId());
            oauth2DTO.setGrantType(this.oauth2);
            oauth2DTO.setScope("");
            getSession().setAttribute(oauth2DTO.getState(), oauth2DTO);
            if (GrantTypeEnum.Authorization.getLiteral().equals(this.oauth2)) {
                String redirectUri = HttpFunction.getHttpAddress(request) + "/web" + CodePage.class.getAnnotation(Mount.class).value();
                oauth2DTO.setRedirectUri(redirectUri);
                PageParameters parameters = new PageParameters();
                parameters.add("client_id", oauth2DTO.getClientId());
                parameters.add("response_type", oauth2DTO.getResponseType());
                parameters.add("state", oauth2DTO.getState());
                parameters.add("scope", oauth2DTO.getScope());
                parameters.add("redirect_uri", oauth2DTO.getRedirectUri());
                setResponsePage(AuthorizePage.class, parameters);
            } else if (GrantTypeEnum.Implicit.getLiteral().equals(this.oauth2)) {
                String redirectUri = HttpFunction.getHttpAddress(request) + "/web" + AccessTokenPage.class.getAnnotation(Mount.class).value();
                oauth2DTO.setRedirectUri(redirectUri);
                List<String> parameters = new ArrayList<>();
                parameters.add("client_id=" + oauth2DTO.getClientId());
                parameters.add("response_type=" + oauth2DTO.getResponseType());
                parameters.add("state=" + oauth2DTO.getState());
                parameters.add("scope=" + oauth2DTO.getScope());
                parameters.add("redirect_uri=" + oauth2DTO.getRedirectUri());
                RedirectPage redirectPage = new RedirectPage(HttpFunction.getHttpAddress(request) + "/api/oauth2/implicit?" + StringUtils.join(parameters, "&"));
                setResponsePage(redirectPage);
            }
        } else if (GrantTypeEnum.Password.getLiteral().equals(this.oauth2)) {
            setResponsePage(PasswordPage.class);
        } else if (GrantTypeEnum.Client.getLiteral().equals(this.oauth2)) {
            String httpAddress = HttpFunction.getHttpAddress(request) + "/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(httpAddress)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            String grantType = "client_credentials";

            OAuth2Client client = retrofit.create(OAuth2Client.class);
            Call<OAuth2ClientResponse> responseCall = client.oauth2Client(grantType, "");
            try {
                retrofit2.Response<OAuth2ClientResponse> response = responseCall.execute();
                if (response.code() == HttpStatus.SC_OK) {
                    OAuth2DTO oauth2DTO = new OAuth2DTO();
                    oauth2DTO.setState(UUID.randomUUID().toString());
                    oauth2DTO.setAccessToken(response.body().getAccessToken());
                    oauth2DTO.setExpiresIn(response.body().getExpiresIn());
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
}
