package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.JobCronValidator;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation({"administrator"})
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
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new JobNameValidator(getSession().getApplicationCode()));
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
        String jobId = UUID.randomUUID().toString();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Job.JOB_ID, jobId);
        fields.put(Jdbc.Job.DATE_CREATED, new Date());
        fields.put(Jdbc.Job.CRON, this.cron);
        fields.put(Jdbc.Job.APPLICATION_CODE, getSession().getApplicationCode());
        fields.put(Jdbc.Job.JAVASCRIPT, this.javascript);
        fields.put(Jdbc.Job.NAME, this.name);
        fields.put(Jdbc.Job.SECURITY, SecurityEnum.Denied.getLiteral());
        fields.put(Jdbc.Job.USER_ID, getSession().getApplicationUserId());
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.JOB);
        jdbcInsert.execute(fields);
        getJavascriptService().schedule(getSession().getApplicationCode(), jobId);
        setResponsePage(JobManagementPage.class);
    }
}
