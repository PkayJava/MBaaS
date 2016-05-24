package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRoleRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.function.ApplicationFunction;
import com.angkorteam.mbaas.server.function.CommonFunction;
import com.angkorteam.mbaas.server.validator.ApplicationCodeValidator;
import com.angkorteam.mbaas.server.validator.ApplicationOAuthRoleValidator;
import com.angkorteam.mbaas.server.validator.PushApplicationValidator;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation({"mbaas.administrator"})
@Mount("/mbaas/application/create")
public class ApplicationCreatePage extends MBaaSPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String code;
    private TextField<String> codeField;
    private TextFeedbackPanel codeFeedback;

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

        this.codeField = new TextField<>("codeField", new PropertyModel<>(this, "code"));
        this.codeField.setRequired(true);
        this.form.add(this.codeField);
        this.codeField.add(new ApplicationCodeValidator());
        this.codeFeedback = new TextFeedbackPanel("codeFeedback", this.codeField);
        this.form.add(this.codeFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.oauthRolesField = new TextField<>("oauthRolesField", new PropertyModel<>(this, "oauthRoles"));
        this.oauthRolesField.add(new ApplicationOAuthRoleValidator());
        this.form.add(this.oauthRolesField);
        this.oauthRolesFeedback = new TextFeedbackPanel("oauthRolesFeedback", this.oauthRolesField);
        this.form.add(this.oauthRolesFeedback);

        this.pushApplicationIdField = new TextField<>("pushApplicationIdField", new PropertyModel<>(this, "pushApplicationId"));
        this.form.add(this.pushApplicationIdField);
        this.pushApplicationIdFeedback = new TextFeedbackPanel("pushApplicationIdFeedback", this.pushApplicationIdField);
        this.form.add(this.pushApplicationIdFeedback);

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

        List<String> oauthRoles = CommonFunction.splitNoneWhite(this.oauthRoles);

        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ApplicationRecord applicationRecord = context.newRecord(applicationTable);
        String applicationId = UUID.randomUUID().toString();
        applicationRecord.setApplicationId(applicationId);
        applicationRecord.setName(this.name);
        applicationRecord.setCode(this.code);
        applicationRecord.setDescription(this.description);
        applicationRecord.setDateCreated(new Date());
        applicationRecord.setSecret(UUID.randomUUID().toString());
        applicationRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        applicationRecord.setMbaasUserId(getSession().getMbaasUserId());
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setPushMasterSecret(this.pushMasterSecret);
        if (!oauthRoles.isEmpty()) {
            applicationRecord.setOauthRoles(StringUtils.join(oauthRoles, ", "));
        }
        applicationRecord.store();

        for (String oauthRole : oauthRoles) {
            ApplicationRoleRecord applicationRoleRecord = context.newRecord(Tables.APPLICATION_ROLE);
            applicationRoleRecord.setApplicationRoleId(UUID.randomUUID().toString());
            applicationRoleRecord.setApplicationId(applicationId);
            applicationRoleRecord.setName(oauthRole);
            applicationRoleRecord.store();
        }

        DbSupport dbSupport = getDbSupport();
        ServletContext servletContext = getServletContext();
        ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource = getApplicationDataSource();

        ApplicationFunction.createApplication(this.code, applicationDataSource, dbSupport, servletContext);

        setResponsePage(ApplicationManagementPage.class);
    }
}
