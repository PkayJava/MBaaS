package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.validator.ApplicationOAuthRoleValidator;
import com.angkorteam.mbaas.server.validator.PushApplicationValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.UrlValidator;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/application/modify")
public class ApplicationModifyPage extends MasterPage {

    private String applicationId;

    private Boolean autoRegistration;
    private DropDownChoice<Boolean> autoRegistrationField;
    private TextFeedbackPanel autoRegistrationFeedback;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String oauthRoles;
    private TextField<String> oauthRolesField;
    private TextFeedbackPanel oauthRolesFeedback;

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

        this.autoRegistration = applicationRecord.getAutoRegistration();
        this.autoRegistrationField = new DropDownChoice<>("autoRegistrationField", new PropertyModel<>(this, "autoRegistration"), Arrays.asList(true, false));
        this.form.add(this.autoRegistrationField);
        this.autoRegistrationFeedback = new TextFeedbackPanel("autoRegistrationFeedback", this.autoRegistrationField);
        this.form.add(this.autoRegistrationFeedback);

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

        this.oauthRoles = applicationRecord.getOauthRoles();
        this.oauthRolesField = new TextField<>("oauthRolesField", new PropertyModel<>(this, "oauthRoles"));
        this.oauthRolesField.add(new ApplicationOAuthRoleValidator());
        this.form.add(this.oauthRolesField);
        this.oauthRolesFeedback = new TextFeedbackPanel("oauthRolesFeedback", this.oauthRolesField);
        this.form.add(this.oauthRolesFeedback);

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

        List<String> oauthRoles = new ArrayList<>();
        if (this.oauthRoles != null && !"".equals(this.oauthRoles.trim())) {
            for (String oauthRole : StringUtils.split(this.oauthRoles, ',')) {
                String trimmed = oauthRole.trim();
                if (!"".equals(trimmed)) {
                    if (!oauthRoles.contains(trimmed)) {
                        oauthRoles.add(trimmed);
                    }
                }
            }
        }

        // recalculate test oauth_role_{name} to false and patch to true
        if (!oauthRoles.isEmpty()) {
            // String collectionId = jdbcTemplate.queryForObject("SELECT " + Tables.COLLECTION.COLLECTION_ID.getName() + " FROM `" + Tables.COLLECTION.getName() + "` WHERE " + Tables.COLLECTION.NAME.getName() + " = ?", String.class, Tables.APPLICATION.getName());
        }

        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);
        applicationRecord.setName(this.name);
        applicationRecord.setDescription(this.description);
        applicationRecord.setPushServerUrl(this.pushServerUrl);
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setPushMasterSecret(this.pushMasterSecret);
        applicationRecord.setAutoRegistration(this.autoRegistration);
        applicationRecord.update();

        setResponsePage(ApplicationManagementPage.class);
    }
}
