package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.renderer.PageChoiceRenderer;
import com.angkorteam.mbaas.server.select2.PageChoiceProvider;
import com.angkorteam.mbaas.server.validator.RoleNameValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.dao.DataAccessException;
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

    private Map<String, Object> homePage;
    private Select2SingleChoice<Map<String, Object>> pageField;
    private TextFeedbackPanel pageFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Role";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        PageParameters parameters = getPageParameters();
        this.roleId = parameters.get("roleId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> roleRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " = ?", this.roleId);

        this.name = (String) roleRecord.get(Jdbc.Role.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        if ((Boolean) roleRecord.get(Jdbc.Role.SYSTEM)) {
            this.nameField.setEnabled(false);
        }
        this.nameField.add(new RoleNameValidator(getSession().getApplicationCode(), this.roleId));
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = (String) roleRecord.get(Jdbc.Role.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        if ((Boolean) roleRecord.get(Jdbc.Role.SYSTEM)) {
            this.descriptionField.setEnabled(false);
        }
        this.descriptionField.setRequired(true);
        this.descriptionField.setLabel(JooqUtils.lookup("description", this));
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        try {
            this.homePage = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", roleRecord.get(Jdbc.Role.HOME_PAGE_ID));
        } catch (DataAccessException e) {
        }
        this.pageField = new Select2SingleChoice<>("pageField", new PropertyModel<>(this, "homePage"), new PageChoiceProvider(getSession().getApplicationCode()), new PageChoiceRenderer());
        this.pageField.setLabel(JooqUtils.lookup("page", this));
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if (this.homePage != null) {
            jdbcTemplate.update("UPDATE " + Jdbc.ROLE + " SET " + Jdbc.Role.NAME + " = ?, " + Jdbc.Role.DESCRIPTION + " = ?" + Jdbc.Role.HOME_PAGE_ID + " = ? WHERE " + Jdbc.Role.ROLE_ID + " = ?", this.name, this.description, this.homePage.get(Jdbc.Page.PAGE_ID), this.roleId);
        } else {
            jdbcTemplate.update("UPDATE " + Jdbc.ROLE + " SET " + Jdbc.Role.NAME + " = ?, " + Jdbc.Role.DESCRIPTION + " = ?" + Jdbc.Role.HOME_PAGE_ID + " = ? WHERE " + Jdbc.Role.ROLE_ID + " = ?", this.name, this.description, null, this.roleId);
        }
        setResponsePage(RoleManagementPage.class);
    }

}
