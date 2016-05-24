package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.RoleNameValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/role/modify")
public class RoleModifyPage extends MasterPage {

    private String roleId;

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
        return "Modify Role";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters parameters = getPageParameters();
        this.roleId = parameters.get("roleId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> roleRecord = null;
        roleRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " = ?", this.roleId);

        this.name = (String) roleRecord.get(Jdbc.Role.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new RoleNameValidator(getSession().getApplicationCode(), this.roleId));
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);

        this.description = (String) roleRecord.get(Jdbc.Role.DESCRIPTION);
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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        jdbcTemplate.update("UPDATE " + Jdbc.ROLE + " SET " + Jdbc.Role.NAME + " = ?, " + Jdbc.Role.DESCRIPTION + " = ? WHERE " + Jdbc.Role.ROLE_ID + " = ?", this.name, this.description, this.roleId);
        setResponsePage(RoleManagementPage.class);
    }

}
