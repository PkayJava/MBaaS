package com.angkorteam.mbaas.server.page.javascript;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.JavascriptPathValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/javascript/modify")
public class JavascriptModifyPage extends MasterPage {

    private String javascriptId;

    private String pathText;
    private TextField<String> pathField;
    private TextFeedbackPanel pathFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String script;
    private JavascriptTextArea scriptField;
    private TextFeedbackPanel scriptFeedback;

    private Button saveButton;
    private Button saveAndContinueButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Javascript";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.javascriptId = getPageParameters().get("javascriptId").toString();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> javascriptRecord = null;
        javascriptRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JAVASCRIPT + " WHERE " + Jdbc.Javascript.JAVASCRIPT_ID + " = ?", this.javascriptId);

        this.form = new Form<>("form");
        add(this.form);

        this.pathText = (String) javascriptRecord.get(Jdbc.Javascript.PATH);
        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "pathText"));
        this.pathField.setRequired(true);
        this.pathField.add(new JavascriptPathValidator(getSession().getApplicationCode(), this.javascriptId));
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.description = (String) javascriptRecord.get(Jdbc.Javascript.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.script = (String) javascriptRecord.get(Jdbc.Javascript.SCRIPT);
        this.scriptField = new JavascriptTextArea("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.saveAndContinueButton = new Button("saveAndContinueButton");
        this.saveAndContinueButton.setOnSubmit(this::saveAndContinueButtonOnSubmit);
        this.form.add(this.saveAndContinueButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Javascript.JAVASCRIPT_ID, this.javascriptId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Javascript.PATH, this.pathText);
        fields.put(Jdbc.Javascript.SCRIPT, this.script);
        fields.put(Jdbc.Javascript.DESCRIPTION, this.description);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.JAVASCRIPT);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(JavascriptManagementPage.class);
    }

    private void saveAndContinueButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Javascript.JAVASCRIPT_ID, this.javascriptId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Javascript.PATH, this.pathText);
        fields.put(Jdbc.Javascript.SCRIPT, this.script);
        fields.put(Jdbc.Javascript.DESCRIPTION, this.description);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.JAVASCRIPT);
        jdbcUpdate.execute(fields, wheres);
        PageParameters parameters = new PageParameters();
        parameters.add("javascriptId", this.javascriptId);
        setResponsePage(JavascriptModifyPage.class, parameters);
    }

}
