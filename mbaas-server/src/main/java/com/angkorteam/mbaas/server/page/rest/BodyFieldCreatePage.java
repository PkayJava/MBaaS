package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/body/field/create")
public class BodyFieldCreatePage extends MasterPage {

    private String jsonId;

    private String jsonName;
    private Label jsonNameLabel;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Body Field";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.jsonId = getPageParameters().get("jsonId").toString("");

        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> jsonRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", this.jsonId);

        this.form = new Form<>("form");
        this.add(this.form);

        this.jsonName = (String) jsonRecord.get(Jdbc.Json.NAME);
        this.jsonNameLabel = new Label("jsonNameLabel", new PropertyModel<>(this, "jsonName"));
        this.form.add(jsonNameLabel);

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

        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);

        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", BodyFieldManagementPage.class, parameters);
        this.form.add(closeLink);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.JSON_FIELD);
        jdbcInsert.usingColumns(Jdbc.JsonField.JSON_ID, Jdbc.JsonField.JSON_FIELD_ID, Jdbc.JsonField.NAME, Jdbc.JsonField.DESCRIPTION);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.JsonField.JSON_FIELD_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.JsonField.JSON_ID, this.jsonId);
        fields.put(Jdbc.JsonField.NAME, this.name);
        fields.put(Jdbc.JsonField.DESCRIPTION, this.description);
        jdbcInsert.execute(fields);
        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);
        setResponsePage(BodyFieldManagementPage.class, parameters);
    }

}
