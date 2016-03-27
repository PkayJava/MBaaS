package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.validator.PushValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.UrlValidator;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/application/create")
public class ApplicationCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String serverUrl;
    private TextField<String> serverUrlField;
    private TextFeedbackPanel serverUrlFeedback;

    private String pushApplicationId;
    private TextField<String> pushApplicationIdField;
    private TextFeedbackPanel pushApplicationIdFeedback;

    private String masterSecret;
    private TextField<String> masterSecretField;
    private TextFeedbackPanel masterSecretFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Application";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.serverUrlField = new TextField<>("serverUrlField", new PropertyModel<>(this, "serverUrl"));
        this.serverUrlField.add(new UrlValidator());
        this.form.add(this.serverUrlField);
        this.serverUrlFeedback = new TextFeedbackPanel("serverUrlFeedback", this.serverUrlField);
        this.form.add(this.serverUrlFeedback);

        this.pushApplicationIdField = new TextField<>("pushApplicationIdField", new PropertyModel<>(this, "pushApplicationId"));
        this.form.add(this.pushApplicationIdField);
        this.pushApplicationIdFeedback = new TextFeedbackPanel("pushApplicationIdFeedback", this.pushApplicationIdField);
        this.form.add(this.pushApplicationIdFeedback);

        this.masterSecretField = new TextField<>("masterSecretField", new PropertyModel<>(this, "masterSecret"));
        this.form.add(this.masterSecretField);
        this.masterSecretFeedback = new TextFeedbackPanel("masterSecretFeedback", this.masterSecretField);
        this.form.add(this.masterSecretFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.form.add(new PushValidator(this.serverUrlField, this.pushApplicationIdField, this.masterSecretField));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ApplicationRecord applicationRecord = context.newRecord(applicationTable);
        String applicationId = UUID.randomUUID().toString();
        applicationRecord.setApplicationId(applicationId);
        applicationRecord.setName(this.name);
        applicationRecord.setDescription(this.description);
        applicationRecord.setDateCreated(new Date());
        applicationRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        applicationRecord.setOwnerUserId(getSession().getUserId());
        applicationRecord.setServerUrl(this.serverUrl);
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setMasterSecret(this.masterSecret);
        applicationRecord.store();

        setResponsePage(ApplicationManagementPage.class);
    }
}
