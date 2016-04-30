package com.angkorteam.mbaas.server.page.javascript;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.records.JavascriptRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.validator.JavascriptPathValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
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
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.script = getString("javascript.script");

        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "pathText"));
        this.pathField.setRequired(true);
        this.pathField.add(new JavascriptPathValidator());
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

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
        DSLContext context = getDSLContext();
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");

        String uuid = UUID.randomUUID().toString();

        JavascriptRecord javascriptRecord = context.newRecord(javascriptTable);

        javascriptRecord.setJavascriptId(uuid);
        javascriptRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        javascriptRecord.setPath(this.pathText);
        javascriptRecord.setScript(this.script);
        javascriptRecord.setOwnerUserId(getSession().getUserId());
        javascriptRecord.setDateCreated(new Date());
        javascriptRecord.setDescription(this.description);
        javascriptRecord.store();

        setResponsePage(JavascriptManagementPage.class);
    }

}
