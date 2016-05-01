package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.page.client.ClientManagementPage;
import com.angkorteam.mbaas.server.validator.ClientNameValidator;
import com.angkorteam.mbaas.server.validator.JobCronValidator;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.validator.PushClientValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/job/create")
public class JobCreatePage extends MasterPage {

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
        return "Create New Job";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new JobNameValidator());
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.cronField = new TextField<>("cronField", new PropertyModel<>(this, "cron"));
        this.cronField.setRequired(true);
        this.cronField.add(new JobCronValidator());
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
        JobRecord jobRecord = context.newRecord(jobTable);
        jobRecord.setJobId(UUID.randomUUID().toString());
        jobRecord.setDateCreated(new Date());
        jobRecord.setCron(this.cron);
        jobRecord.setApplicationId(getSession().getApplicationId());
        jobRecord.setJavascript(this.javascript);
        jobRecord.setName(this.name);
        jobRecord.setOwnerUserId(getSession().getUserId());
        jobRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        jobRecord.store();
        getJavascriptService().schedule(jobRecord.getJobId());
        setResponsePage(JobManagementPage.class);
    }
}
