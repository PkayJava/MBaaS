package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.page.javascript.JavascriptManagementPage;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/job/modify")
public class JobModifyPage extends MasterPage {

    private String jobId;

    private String name;
    private Label nameLabel;

    private String cron;
    private Label cronLabel;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Job";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.jobId = getPageParameters().get("jobId").toString();
        DSLContext context = getDSLContext();
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);

        this.name = jobRecord.getName();
        this.nameLabel = new Label("nameLabel", new PropertyModel<>(this, "name"));
        this.form.add(this.nameLabel);

        this.javascript = jobRecord.getJavascript();
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.cron = jobRecord.getCron();
        this.cronLabel = new Label("cronLabel", new PropertyModel<>(this, "cron"));
        this.form.add(this.cronLabel);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        DSLContext context = getDSLContext();
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);
        if (getSession().isBackOffice() && !jobRecord.getOwnerUserId().equals(getSession().getUserId())) {
            setResponsePage(JobManagementPage.class);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);
        jobRecord.setJavascript(this.javascript);
        jobRecord.store();
        getJavascriptService().schedule(jobRecord.getJobId());
        setResponsePage(JobManagementPage.class);
    }

}
