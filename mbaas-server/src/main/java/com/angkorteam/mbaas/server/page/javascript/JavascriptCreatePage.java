package com.angkorteam.mbaas.server.page.javascript;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.JavascriptPathValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/javascript/create")
public class JavascriptCreatePage extends MasterPage {

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

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Javascript";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "pathText"));
        this.pathField.setRequired(true);
        this.pathField.add(new JavascriptPathValidator(getSession().getApplicationCode()));
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.script = getString("javascript.script");
        this.scriptField = new JavascriptTextArea("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Javascript.JAVASCRIPT_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.Javascript.SECURITY, SecurityEnum.Denied.getLiteral());
        fields.put(Jdbc.Javascript.PATH, this.pathText);
        fields.put(Jdbc.Javascript.SCRIPT, this.script);
        fields.put(Jdbc.Javascript.DATE_CREATED, new Date());
        fields.put(Jdbc.Javascript.USER_ID, getSession().getApplicationUserId());
        fields.put(Jdbc.Javascript.APPLICATION_CODE, getSession().getApplicationCode());
        fields.put(Jdbc.Javascript.DESCRIPTION, this.description);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.JAVASCRIPT);
        jdbcInsert.execute(fields);
        setResponsePage(JavascriptManagementPage.class);
    }

}
