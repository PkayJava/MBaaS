package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation({"administrator"})
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

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> jobRecord = null;
        jobRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", this.jobId);

        this.name = (String) jobRecord.get(Jdbc.Job.NAME);
        this.nameLabel = new Label("nameLabel", new PropertyModel<>(this, "name"));
        this.form.add(this.nameLabel);

        this.javascript = (String) jobRecord.get(Jdbc.Job.JAVASCRIPT);
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.cron = (String) jobRecord.get(Jdbc.Job.CRON);
        this.cronLabel = new Label("cronLabel", new PropertyModel<>(this, "cron"));
        this.form.add(this.cronLabel);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        jdbcTemplate.update("UPDATE " + Jdbc.JOB + " SET " + Jdbc.Job.JAVASCRIPT + " = ? WHERE " + Jdbc.Job.JOB_ID + " = ?", this.javascript, this.jobId);
        getJavascriptService().schedule(getSession().getApplicationCode(), this.jobId);
        setResponsePage(JobManagementPage.class);
    }

}
