package com.angkorteam.mbaas.server.page.client;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.validator.ClientNameValidator;
import com.angkorteam.mbaas.server.validator.PushClientValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/client/create")
public class ClientCreatePage extends MasterPage {

    private String applicationId;

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
        return "Create New Client";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.applicationId = getPageParameters().get("applicationId").toString();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new ClientNameValidator(this.applicationId));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.pushVariantIdField = new TextField<>("pushVariantIdField", new PropertyModel<>(this, "pushVariantId"));
        this.form.add(this.pushVariantIdField);
        this.pushVariantIdFeedback = new TextFeedbackPanel("pushVariantIdFeedback", this.pushVariantIdField);
        this.form.add(this.pushVariantIdFeedback);

        this.pushSecretField = new TextField<>("pushSecretField", new PropertyModel<>(this, "pushSecret"));
        this.form.add(this.pushSecretField);
        this.pushSecretFeedback = new TextFeedbackPanel("pushSecretFeedback", this.pushSecretField);
        this.form.add(this.pushSecretFeedback);

        this.pushGcmSenderIdField = new TextField<>("pushGcmSenderIdField", new PropertyModel<>(this, "pushGcmSenderId"));
        this.form.add(this.pushGcmSenderIdField);
        this.pushGcmSenderIdFeedback = new TextFeedbackPanel("pushGcmSenderIdFeedback", this.pushGcmSenderIdField);
        this.form.add(this.pushGcmSenderIdFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
        this.form.add(new PushClientValidator(this.pushVariantIdField, this.pushSecretField));

        PageParameters parameters = new PageParameters();
        parameters.add("applicationId", this.applicationId);
        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", ClientManagementPage.class, parameters);
        this.form.add(closeLink);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        this.applicationId = getPageParameters().get("applicationId").toString();
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);

        ClientRecord clientRecord = context.newRecord(clientTable);
        clientRecord.setDateCreated(new Date());
        clientRecord.setUserId(getSession().getUserId());
        clientRecord.setApplicationId(this.applicationId);
        clientRecord.setApplicationUserId(applicationRecord.getOwnerUserId());
        clientRecord.setClientSecret(UUID.randomUUID().toString());
        clientRecord.setClientId(UUID.randomUUID().toString());
        clientRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        clientRecord.setName(this.name);
        clientRecord.setDescription(this.description);
        clientRecord.setPushGcmSenderId(this.pushGcmSenderId);
        clientRecord.setPushSecret(this.pushSecret);
        clientRecord.setPushVariantId(this.pushVariantId);
        clientRecord.store();

        PageParameters parameters = new PageParameters();
        parameters.add("applicationId", this.applicationId);

        setResponsePage(ClientManagementPage.class, parameters);
    }
}
