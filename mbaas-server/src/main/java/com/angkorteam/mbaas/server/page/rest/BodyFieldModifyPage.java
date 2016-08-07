package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/body/field/modify")
public class BodyFieldModifyPage extends MasterPage {

    private String jsonId;
    private String jsonFieldId;

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
        return "Modify Body Field";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.jsonId = getPageParameters().get("jsonId").toString("");
        this.jsonFieldId = getPageParameters().get("jsonFieldId").toString("");

        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> jsonRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", this.jsonId);
        Map<String, Object> jsonFieldRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_FIELD_ID + " = ?", this.jsonFieldId);

        this.form = new Form<>("form");
        this.add(this.form);

        this.jsonName = (String) jsonRecord.get(Jdbc.Json.NAME);
        this.jsonNameLabel = new Label("jsonNameLabel", new PropertyModel<>(this, "jsonName"));
        this.form.add(jsonNameLabel);

        this.name = (String) jsonFieldRecord.get(Jdbc.JsonField.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = (String) jsonFieldRecord.get(Jdbc.JsonField.DESCRIPTION);
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
        Session session = getSession();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.JSON_FIELD);
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.JsonField.JSON_FIELD_ID, this.jsonFieldId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.JsonField.NAME, this.name);
        fields.put(Jdbc.JsonField.DESCRIPTION, this.description);
        jdbcUpdate.execute(fields, wheres);
        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);
        setResponsePage(BodyFieldManagementPage.class, parameters);
    }
}
