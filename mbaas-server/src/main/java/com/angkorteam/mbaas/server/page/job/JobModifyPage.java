package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/job/modify")
public class JobModifyPage extends MasterPage {

    private String jobId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String cron;
    private TextField<String> cronField;
    private TextFeedbackPanel cronFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Job";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        this.form = new Form<>("form");
        add(this.form);

        this.jobId = getPageParameters().get("jobId").toString();
        JobTable jobTable = Tables.JOB.as("jobTable");

        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);

        this.name = jobRecord.getName();
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new JobNameValidator(this.jobId));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.javascript = jobRecord.getJavascript();
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.cron = jobRecord.getCron();
        this.cronField = new TextField<>("cronField", new PropertyModel<>(this, "cron"));
        this.cronField.setRequired(true);
        this.form.add(this.cronField);
        this.cronFeedback = new TextFeedbackPanel("cronFeedback", this.cronField);
        this.form.add(this.cronFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);
        jobRecord.setCron(this.cron);
        jobRecord.setJavascript(this.javascript);
        jobRecord.setName(this.name);
        jobRecord.store();
        setResponsePage(JobManagementPage.class);
    }

}
