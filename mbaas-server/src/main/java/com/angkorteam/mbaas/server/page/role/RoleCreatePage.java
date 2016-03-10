package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.server.validator.RoleNameValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.UUID;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/role/create")
public class RoleCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Role";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new RoleNameValidator());
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.descriptionField.setLabel(JooqUtils.lookup("description", this));
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(this.nameField);
        this.form.add(this.nameFeedback);

        this.form.add(this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        RoleTable roleTable = Tables.ROLE.as("roleTable");

        RoleRecord roleRecord = context.newRecord(roleTable);
        roleRecord.setRoleId(UUID.randomUUID().toString());
        roleRecord.setSystem(false);
        roleRecord.setDeleted(false);
        roleRecord.setName(this.name);
        roleRecord.setDescription(this.description);
        roleRecord.store();

        setResponsePage(RoleManagementPage.class);
    }

}
