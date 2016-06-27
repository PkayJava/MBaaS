package com.angkorteam.mbaas.server.page.cms.page;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.PagePage;
import com.angkorteam.mbaas.server.renderer.MasterPageChoiceRenderer;
import com.angkorteam.mbaas.server.renderer.MenuChoiceRenderer;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.select2.MasterPageChoiceProvider;
import com.angkorteam.mbaas.server.select2.MenuChoiceProvider;
import com.angkorteam.mbaas.server.select2.RoleChoiceProvider;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/page/modify")
public class PageModifyPage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageModifyPage.class);

    private String pageId;

    private String code;
    private Label codeLabel;

    private List<Map<String, Object>> role;
    private Select2MultipleChoice<Map<String, Object>> roleField;
    private TextFeedbackPanel roleFeedback;

    private Map<String, Object> masterPage;
    private Select2SingleChoice<Map<String, Object>> masterPageField;
    private TextFeedbackPanel masterPageFeedback;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String html;
    private HtmlTextArea htmlField;
    private TextFeedbackPanel htmlFeedback;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Map<String, Object> menu;
    private Select2SingleChoice<Map<String, Object>> menuField;
    private TextFeedbackPanel menuFeedback;

    private Form<Void> form;
    private Button saveButton;
    private Button previewButton;

    @Override
    public String getPageHeader() {
        return "Modify Page";
    }

    public Map<String, Object> getMenu() {
        return this.menu;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        this.pageId = getPageParameters().get("pageId").toString("");
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);

        this.role = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " IN (SELECT " + Jdbc.PageRole.ROLE_ID + " FROM " + Jdbc.PAGE_ROLE + " WHERE " + Jdbc.PageRole.PAGE_ID + " = ?)", this.pageId);
        this.roleField = new Select2MultipleChoice<>("roleField", new PropertyModel<>(this, "role"), new RoleChoiceProvider(getSession().getApplicationCode()), new RoleChoiceRenderer());
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.masterPage = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", pageRecord.get(Jdbc.Page.MASTER_PAGE_ID));
        this.masterPageField = new Select2SingleChoice<>("masterPageField", new PropertyModel<>(this, "masterPage"), new MasterPageChoiceProvider(getSession().getApplicationCode()), new MasterPageChoiceRenderer());
        this.masterPageField.setRequired(true);
        this.form.add(this.masterPageField);
        this.masterPageFeedback = new TextFeedbackPanel("masterPageFeedback", this.masterPageField);
        this.form.add(this.masterPageFeedback);

        this.menu = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.MENU_ID + " = ?", pageRecord.get(Jdbc.Page.MENU_ID));
        this.menuField = new Select2SingleChoice<>("menuField", new PropertyModel<>(this, "menu"), new MenuChoiceProvider(getSession().getApplicationCode()), new MenuChoiceRenderer());
        this.menuField.setRequired(true);
        this.form.add(this.menuField);
        this.menuFeedback = new TextFeedbackPanel("menuFeedback", this.menuField);
        this.form.add(this.menuFeedback);

        this.code = (String) pageRecord.get(Jdbc.Page.CODE);
        this.codeLabel = new Label("codeLabel", new PropertyModel<>(this, "code"));
        this.form.add(codeLabel);

        this.title = (String) pageRecord.get(Jdbc.Page.TITLE);
        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new JobNameValidator(getSession().getApplicationCode()));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.javascript = (String) pageRecord.get(Jdbc.Page.STAGE_JAVASCRIPT);
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.description = (String) pageRecord.get(Jdbc.Page.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.html = (String) pageRecord.get(Jdbc.Page.STAGE_HTML);
        this.htmlField = new HtmlTextArea("htmlField", new PropertyModel<>(this, "html"));
        this.htmlField.setRequired(true);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.previewButton = new Button("previewButton");
        this.previewButton.setOnSubmit(this::previewButtonOnSubmit);
        this.form.add(this.previewButton);
    }

    private void previewButtonOnSubmit(Button button) {
        save();
        PageParameters parameters = new PageParameters();
        parameters.add("pageId", this.pageId);
        parameters.add("stage", true);
        setResponsePage(PagePage.class, parameters);
    }

    private void save() {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        {
            Map<String, Object> wheres = new HashMap<>();
            wheres.put(Jdbc.Page.PAGE_ID, this.pageId);
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Page.DATE_MODIFIED, new Date());
            fields.put(Jdbc.Page.TITLE, this.title);
            fields.put(Jdbc.Page.MENU_ID, this.menu.get(Jdbc.Menu.MENU_ID));
            fields.put(Jdbc.Page.MASTER_PAGE_ID, this.masterPage.get(Jdbc.MasterPage.MASTER_PAGE_ID));
            fields.put(Jdbc.Page.DESCRIPTION, this.description);
            fields.put(Jdbc.Page.STAGE_JAVASCRIPT, this.javascript);
            fields.put(Jdbc.Page.STAGE_HTML, this.html);
            fields.put(Jdbc.Page.MODIFIED, true);
            SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
            jdbcUpdate.withTableName(Jdbc.PAGE);
            jdbcUpdate.execute(fields, wheres);
        }

        jdbcTemplate.update("DELETE FROM " + Jdbc.PAGE_ROLE + " WHERE " + Jdbc.PageRole.PAGE_ID + " = ?", this.pageId);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.PAGE_ROLE);
        for (Map<String, Object> role : this.role) {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.PageRole.PAGE_ROLE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.PageRole.ROLE_ID, role.get(Jdbc.Role.ROLE_ID));
            fields.put(Jdbc.PageRole.PAGE_ID, pageId);
            jdbcInsert.execute(fields);
        }

        {
            String cacheKey = PagePage.class.getName() + "_" + this.pageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
        }
        {
            String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + this.pageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        save();
        setResponsePage(PageManagementPage.class);
    }

}
