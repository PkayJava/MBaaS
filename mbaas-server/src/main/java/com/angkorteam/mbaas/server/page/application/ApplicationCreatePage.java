package com.angkorteam.mbaas.server.page.application;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.MariaDBFunction;
import com.angkorteam.mbaas.server.validator.ApplicationOAuthRoleValidator;
import com.angkorteam.mbaas.server.validator.PushApplicationValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/application/create")
public class ApplicationCreatePage extends MasterPage {

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

        this.autoRegistrationField = new DropDownChoice<>("autoRegistrationField", new PropertyModel<>(this, "autoRegistration"), Arrays.asList(true, false));
        this.form.add(this.autoRegistrationField);
        this.autoRegistrationFeedback = new TextFeedbackPanel("autoRegistrationFeedback", this.autoRegistrationField);
        this.form.add(this.autoRegistrationFeedback);

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

        List<String> oauthRoles = new ArrayList<>();
        List<String> oauthRolesSave = new ArrayList<>();
        if (this.oauthRoles != null && !"".equals(this.oauthRoles.trim())) {
            for (String oauthRole : StringUtils.split(this.oauthRoles, ',')) {
                String trimmed = oauthRole.trim();
                if (!"".equals(trimmed)) {
                    if (!oauthRoles.contains("oauth_role_" + trimmed)) {
                        oauthRoles.add("oauth_role_" + trimmed);
                        oauthRolesSave.add(trimmed);
                    }
                }
            }
        }

        DSLContext context = getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ApplicationRecord applicationRecord = context.newRecord(applicationTable);
        String applicationId = UUID.randomUUID().toString();
        applicationRecord.setApplicationId(applicationId);
        applicationRecord.setName(this.name);
        applicationRecord.setDescription(this.description);
        applicationRecord.setDateCreated(new Date());
        applicationRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        applicationRecord.setAutoRegistration(this.autoRegistration);
        applicationRecord.setOwnerUserId(getSession().getUserId());
        applicationRecord.setPushApplicationId(this.pushApplicationId);
        applicationRecord.setPushMasterSecret(this.pushMasterSecret);
        if (!oauthRoles.isEmpty()) {
            applicationRecord.setOauthRoles(StringUtils.join(oauthRolesSave, ", "));
        }
        applicationRecord.store();

        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        {
            Map<String, Object> attributes = new HashMap<>();
            Map<String, AttributeTypeEnum> attributeTypeEnums = new HashMap<>();
            attributes.put("__temp", true);
            attributeTypeEnums.put("__temp", AttributeTypeEnum.Boolean);
            jdbcTemplate.update("UPDATE `" + Tables.APPLICATION.getName() + "` SET " + Tables.APPLICATION.EXTRA.getName() + " = " + MariaDBFunction.columnCreate(attributes, attributeTypeEnums) + " WHERE " + Tables.APPLICATION.APPLICATION_ID.getName() + " = ?", applicationRecord.getApplicationId());
        }

        if (!oauthRoles.isEmpty()) {
            String collectionId = jdbcTemplate.queryForObject("SELECT " + Tables.COLLECTION.COLLECTION_ID.getName() + " FROM `" + Tables.COLLECTION.getName() + "` WHERE " + Tables.COLLECTION.NAME.getName() + " = ?", String.class, Tables.APPLICATION.getName());
            Map<String, AttributeRecord> attributeRecords = new HashMap<>();
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionId)).fetchInto(attributeTable)) {
                attributeRecords.put(attributeRecord.getName(), attributeRecord);
            }

            List<String> adds = new ArrayList<>();
            for (String oauthRole : oauthRoles) {
                if (!attributeRecords.containsKey(oauthRole)) {
                    adds.add(oauthRole);
                }
            }

            for (String add : adds) {
                CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
                request.setCollectionName(Tables.APPLICATION.getName());
                request.setNullable(true);
                request.setJavaType(AttributeTypeEnum.Boolean.getLiteral());
                request.setAttributeName(add);
                AttributeFunction.createAttribute(context, request);
            }

            Map<String, Object> attributes = new HashMap<>();

            for (String oauthRole : oauthRoles) {
                attributes.put(oauthRole, true);
            }

            if (!attributes.isEmpty()) {
                DocumentModifyRequest request = new DocumentModifyRequest();
                request.setDocument(attributes);
                DocumentFunction.modifyDocument(context, jdbcTemplate, Tables.APPLICATION.getName(), applicationId, request);
            }
        }

        setResponsePage(ApplicationManagementPage.class);
    }
}
