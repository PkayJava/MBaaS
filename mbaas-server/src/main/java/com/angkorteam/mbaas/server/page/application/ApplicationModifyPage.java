package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.validator.PushApplicationValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.UrlValidator;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/application/modify")
public class ApplicationModifyPage extends MasterPage {

    private String applicationId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String pushServerUrl;
    private TextField<String> pushServerUrlField;
    private TextFeedbackPanel pushServerUrlFeedback;

    private String pushApplicationId;
    private TextField<String> pushApplicationIdField;
    private TextFeedbackPanel pushApplicationIdFeedback;

    private String pushMasterSecret;
    private TextField<String> pushMasterSecretField;
    private TextFeedbackPanel pushMasterSecretFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Application";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        this.applicationId = getPageParameters().get("applicationId").toString();
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);

        this.form = new Form<>("form");
        add(this.form);

        this.name = applicationRecord.getName();
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = applicationRecord.getDescription();
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.pushServerUrl = applicationRecord.getPushServerUrl();
        this.pushServerUrlField = new TextField<>("pushServerUrlField", new PropertyModel<>(this, "pushServerUrl"));
        this.pushServerUrlField.add(new UrlValidator());
        this.form.add(this.pushServerUrlField);
        this.pushServerUrlFeedback = new TextFeedbackPanel("pushServerUrlFeedback", this.pushServerUrlField);
        this.form.add(this.pushServerUrlFeedback);

        this.pushApplicationId = applicationRecord.getPushApplicationId();
        this.pushApplicationIdField = new TextField<>("pushApplicationIdField", new PropertyModel<>(this, "pushApplicationId"));
        this.form.add(this.pushApplicationIdField);
        this.pushApplicationIdFeedback = new TextFeedbackPanel("pushApplicationIdFeedback", this.pushApplicationIdField);
        this.form.add(this.pushApplicationIdFeedback);

        this.pushMasterSecret = applicationRecord.getPushMasterSecret();
        this.pushMasterSecretField = new TextField<>("pushMasterSecretField", new PropertyModel<>(this, "pushMasterSecret"));
        this.form.add(this.pushMasterSecretField);
        this.pushMasterSecretFeedback = new TextFeedbackPanel("pushMasterSecretFeedback", this.pushMasterSecretField);
        this.form.add(this.pushMasterSecretFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.form.add(new PushApplicationValidator(this.pushServerUrlField, this.pushApplicationIdField, this.pushMasterSecretField));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);
        applicationRecord.setName(this.name);
        applicationRecord.setDescription(this.description);
        applicationRecord.setPushServerUrl(this.pushServerUrl);
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setPushMasterSecret(this.pushMasterSecret);
        applicationRecord.update();

        setResponsePage(ApplicationManagementPage.class);
    }
}
