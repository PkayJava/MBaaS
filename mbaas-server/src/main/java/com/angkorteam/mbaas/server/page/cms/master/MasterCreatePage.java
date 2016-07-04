package com.angkorteam.mbaas.server.page.cms.master;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.MasterPageCodeValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/master/create")
public class MasterCreatePage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterCreatePage.class);

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String code;
    private TextField<String> codeField;
    private TextFeedbackPanel codeFeedback;

    private String html;
    private HtmlTextArea htmlField;
    private TextFeedbackPanel htmlFeedback;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Layout";
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

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.codeField = new TextField<>("codeField", new PropertyModel<>(this, "code"));
        this.codeField.setRequired(true);
        this.codeField.add(new MasterPageCodeValidator(getSession().getApplicationCode()));
        this.form.add(this.codeField);
        this.codeFeedback = new TextFeedbackPanel("codeFeedback", this.codeField);
        this.form.add(this.codeFeedback);

        this.javascript = getString("master.script");
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.html = getString("master.html");
        this.htmlField = new HtmlTextArea("htmlField", new PropertyModel<>(this, "html"));
        this.htmlField.setRequired(true);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        String masterPageId = UUID.randomUUID().toString();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.MasterPage.MASTER_PAGE_ID, masterPageId);
        fields.put(Jdbc.MasterPage.DATE_CREATED, new Date());
        fields.put(Jdbc.MasterPage.DATE_MODIFIED, new Date());
        fields.put(Jdbc.MasterPage.TITLE, this.title);
        fields.put(Jdbc.MasterPage.CODE, this.code);
        fields.put(Jdbc.MasterPage.DESCRIPTION, this.description);
        fields.put(Jdbc.MasterPage.JAVASCRIPT, getString("master.blank.script"));
        fields.put(Jdbc.MasterPage.HTML, getString("master.blank.html"));
        fields.put(Jdbc.MasterPage.STAGE_JAVASCRIPT, this.javascript);
        fields.put(Jdbc.MasterPage.STAGE_HTML, this.html);
        fields.put(Jdbc.MasterPage.MODIFIED, true);
        fields.put(Jdbc.MasterPage.USER_ID, getSession().getApplicationUserId());
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.MASTER_PAGE);
        jdbcInsert.execute(fields);
        setResponsePage(MasterManagementPage.class);
    }

}
