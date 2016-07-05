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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;
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

    private Map<String, Object> homePage;
    private Select2SingleChoice<Map<String, Object>> pageField;
    private TextFeedbackPanel pageFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Role";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new RoleNameValidator(getSession().getApplicationCode()));
        this.nameField.setLabel(JooqUtils.lookup("name", this));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.descriptionField.setLabel(JooqUtils.lookup("description", this));
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

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

        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Role.ROLE_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.Role.SYSTEM, false);
        fields.put(Jdbc.Role.DESCRIPTION, this.description);
        fields.put(Jdbc.Role.HOME_PAGE_ID, this.homePage.get(Jdbc.Page.PAGE_ID));
        fields.put(Jdbc.Role.NAME, this.name);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.ROLE);
        jdbcInsert.execute(fields);

        setResponsePage(RoleManagementPage.class);
    }

}
