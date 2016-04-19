package com.angkorteam.mbaas.server.page.javascript;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextField;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.records.JavascriptRecord;
import com.angkorteam.mbaas.server.validator.JavascriptPathValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation("administrator")
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
    private JavascriptTextField scriptField;
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
        DSLContext context = getDSLContext();
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");

        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.JAVASCRIPT_ID.eq(this.javascriptId)).fetchOneInto(javascriptTable);

        this.form = new Form<>("form");
        add(this.form);

        this.pathText = javascriptRecord.getPath();
        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "pathText"));
        this.pathField.setRequired(true);
        this.pathField.add(new JavascriptPathValidator(this.javascriptId));
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.description = javascriptRecord.getDescription();
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.script = javascriptRecord.getScript();
        this.scriptField = new JavascriptTextField("scriptField", new PropertyModel<>(this, "script"));
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
        DSLContext context = getDSLContext();
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");

        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.JAVASCRIPT_ID.eq(this.javascriptId)).fetchOneInto(javascriptTable);

        javascriptRecord.setPath(this.pathText);
        javascriptRecord.setScript(this.script);
        javascriptRecord.setDescription(this.description);
        javascriptRecord.update();

        setResponsePage(JavascriptManagementPage.class);
    }

    private void saveAndContinueButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");

        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.JAVASCRIPT_ID.eq(this.javascriptId)).fetchOneInto(javascriptTable);

        javascriptRecord.setPath(this.pathText);
        javascriptRecord.setScript(this.script);
        javascriptRecord.setDescription(this.description);
        javascriptRecord.update();

        PageParameters parameters = new PageParameters();
        parameters.add("javascriptId", this.javascriptId);

        setResponsePage(JavascriptModifyPage.class, parameters);
    }

}
