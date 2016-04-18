package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.function.CommonFunction;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.MariaDBFunction;
import com.angkorteam.mbaas.server.validator.ApplicationOAuthRoleValidator;
import com.angkorteam.mbaas.server.validator.PushApplicationValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

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

    private String oauthRoles;
    private TextField<String> oauthRolesField;
    private TextFeedbackPanel oauthRolesFeedback;

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
        return "Modify Application";
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

        this.oauthRoles = applicationRecord.getOauthRoles();
        this.oauthRolesField = new TextField<>("oauthRolesField", new PropertyModel<>(this, "oauthRoles"));
        this.oauthRolesField.add(new ApplicationOAuthRoleValidator());
        this.form.add(this.oauthRolesField);
        this.oauthRolesFeedback = new TextFeedbackPanel("oauthRolesFeedback", this.oauthRolesField);
        this.form.add(this.oauthRolesFeedback);

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

        this.form.add(new PushApplicationValidator(this.pushApplicationIdField, this.pushMasterSecretField));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");


        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        List<String> oauthRoles = CommonFunction.splitNoneWhite(this.oauthRoles);

        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);
        applicationRecord.setName(this.name);
        applicationRecord.setDescription(this.description);
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setPushMasterSecret(this.pushMasterSecret);
        applicationRecord.setOauthRoles(StringUtils.join(oauthRoles, ", "));
        applicationRecord.update();

        context.delete(Tables.APPLICATION_ROLE).where(Tables.APPLICATION_ROLE.APPLICATION_ID.eq(applicationRecord.getApplicationId())).execute();

        for (String oauthRole : oauthRoles) {
            ApplicationRoleRecord applicationRoleRecord = context.newRecord(Tables.APPLICATION_ROLE);
            applicationRoleRecord.setApplicationRoleId(UUID.randomUUID().toString());
            applicationRoleRecord.setApplicationId(applicationId);
            applicationRoleRecord.setName(oauthRole);
            applicationRoleRecord.store();
        }

        setResponsePage(ApplicationManagementPage.class);
    }
}
