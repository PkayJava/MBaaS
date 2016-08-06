package com.angkorteam.mbaas.server.page.client;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.ClientNameValidator;
import com.angkorteam.mbaas.server.validator.PushClientValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/client/modify")
public class ClientModifyPage extends MasterPage {

    private String clientId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String pushVariantId;
    private TextField<String> pushVariantIdField;
    private TextFeedbackPanel pushVariantIdFeedback;

    private String pushSecret;
    private TextField<String> pushSecretField;
    private TextFeedbackPanel pushSecretFeedback;

    private String pushGcmSenderId;
    private TextField<String> pushGcmSenderIdField;
    private TextFeedbackPanel pushGcmSenderIdFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Client";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        this.clientId = getPageParameters().get("clientId").toString();
        Map<String, Object> clientRecord = null;
        clientRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.CLIENT + " WHERE " + Jdbc.Client.CLIENT_ID + " = ?", this.clientId);

        this.form = new Form<>("form");
        add(this.form);

        this.name = (String) clientRecord.get(Jdbc.Client.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new ClientNameValidator(getSession().getApplicationCode(), this.clientId));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = (String) clientRecord.get(Jdbc.Client.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.pushVariantId = (String) clientRecord.get(Jdbc.Client.PUSH_VARIANT_ID);
        this.pushVariantIdField = new TextField<>("pushVariantIdField", new PropertyModel<>(this, "pushVariantId"));
        this.form.add(this.pushVariantIdField);
        this.pushVariantIdFeedback = new TextFeedbackPanel("pushVariantIdFeedback", this.pushVariantIdField);
        this.form.add(this.pushVariantIdFeedback);

        this.pushSecret = (String) clientRecord.get(Jdbc.Client.PUSH_SECRET);
        this.pushSecretField = new TextField<>("pushSecretField", new PropertyModel<>(this, "pushSecret"));
        this.form.add(this.pushSecretField);
        this.pushSecretFeedback = new TextFeedbackPanel("pushSecretFeedback", this.pushSecretField);
        this.form.add(this.pushSecretFeedback);

        this.pushGcmSenderId = (String) clientRecord.get(Jdbc.Client.PUSH_GCM_SENDER_ID);
        this.pushGcmSenderIdField = new TextField<>("pushGcmSenderIdField", new PropertyModel<>(this, "pushGcmSenderId"));
        this.form.add(this.pushGcmSenderIdField);
        this.pushGcmSenderIdFeedback = new TextFeedbackPanel("pushGcmSenderIdFeedback", this.pushGcmSenderIdField);
        this.form.add(this.pushGcmSenderIdFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.form.add(new PushClientValidator(this.pushVariantIdField, this.pushSecretField));
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Client.CLIENT_ID, this.clientId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Client.NAME, this.name);
        fields.put(Jdbc.Client.DESCRIPTION, this.description);
        fields.put(Jdbc.Client.PUSH_GCM_SENDER_ID, this.pushGcmSenderId);
        fields.put(Jdbc.Client.PUSH_SECRET, this.pushSecret);
        fields.put(Jdbc.Client.PUSH_VARIANT_ID, this.pushVariantId);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(ClientManagementPage.class);
    }
}
