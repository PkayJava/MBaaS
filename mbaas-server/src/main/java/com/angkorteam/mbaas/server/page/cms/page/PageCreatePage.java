package com.angkorteam.mbaas.server.page.cms.page;

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
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
@Mount("/cms/page/create")
public class PageCreatePage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageCreatePage.class);

    private String code;
    private TextField<String> codeField;
    private TextFeedbackPanel codeFeedback;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private Map<String, Object> masterPage;
    private Select2SingleChoice<Map<String, Object>> masterPageField;
    private TextFeedbackPanel masterPageFeedback;

    private List<Map<String, Object>> role;
    private Select2MultipleChoice<Map<String, Object>> roleField;
    private TextFeedbackPanel roleFeedback;

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
        return "Create New Page";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        String roleId = jdbcTemplate.queryForObject("SELECT " + Jdbc.User.ROLE_ID + " FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ?", String.class, getSession().getApplicationUserId());

        this.role = new LinkedList<>();
        this.role.add(jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " = ?", roleId));
        this.roleField = new Select2MultipleChoice<>("roleField", new PropertyModel<>(this, "role"), new RoleChoiceProvider(getSession().getApplicationCode()), new RoleChoiceRenderer());
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.menuField = new Select2SingleChoice<>("menuField", new PropertyModel<>(this, "menu"), new MenuChoiceProvider(getSession().getApplicationCode()), new MenuChoiceRenderer());
        this.menuField.setRequired(true);
        this.form.add(this.menuField);
        this.menuFeedback = new TextFeedbackPanel("menuFeedback", this.menuField);
        this.form.add(this.menuFeedback);

        this.masterPageField = new Select2SingleChoice<>("masterPageField", new PropertyModel<>(this, "masterPage"), new MasterPageChoiceProvider(getSession().getApplicationCode()), new MasterPageChoiceRenderer());
        this.masterPageField.setRequired(true);
        this.form.add(this.masterPageField);
        this.masterPageFeedback = new TextFeedbackPanel("masterPageFeedback", this.masterPageField);
        this.form.add(this.masterPageFeedback);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.codeField = new TextField<>("codeField", new PropertyModel<>(this, "code"));
        this.codeField.setRequired(true);
        this.form.add(this.codeField);
        this.codeFeedback = new TextFeedbackPanel("codeFeedback", this.codeField);
        this.form.add(this.codeFeedback);

        this.javascript = getString("page.script");
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

        this.html = getString("page.html");
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
        String pageId = UUID.randomUUID().toString();
        save(pageId);
        PageParameters parameters = new PageParameters();
        parameters.add("pageId", pageId);
        parameters.add("stage", true);
        setResponsePage(PagePage.class, parameters);
    }

    private void save(String pageId) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.Page.PAGE_ID, pageId);
            fields.put(Jdbc.Page.DATE_CREATED, new Date());
            fields.put(Jdbc.Page.DATE_MODIFIED, new Date());
            fields.put(Jdbc.Page.TITLE, this.title);
            fields.put(Jdbc.Page.CODE, this.code);
            fields.put(Jdbc.Page.MENU_ID, this.menu.get(Jdbc.Menu.MENU_ID));
            fields.put(Jdbc.Page.MASTER_PAGE_ID, this.masterPage.get(Jdbc.MasterPage.MASTER_PAGE_ID));
            fields.put(Jdbc.Page.DESCRIPTION, this.description);
            fields.put(Jdbc.Page.JAVASCRIPT, getString("page.blank.script"));
            fields.put(Jdbc.Page.HTML, getString("page.blank.html"));
            fields.put(Jdbc.Page.STAGE_JAVASCRIPT, this.javascript);
            fields.put(Jdbc.Page.STAGE_HTML, this.html);
            fields.put(Jdbc.Page.MODIFIED, true);
            fields.put(Jdbc.Page.USER_ID, getSession().getApplicationUserId());
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(Jdbc.PAGE);
            jdbcInsert.execute(fields);
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.PAGE_ROLE);
        for (Map<String, Object> role : this.role) {
            Map<String, Object> fields = new HashMap<>();
            fields.put(Jdbc.PageRole.PAGE_ROLE_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.PageRole.ROLE_ID, role.get(Jdbc.Role.ROLE_ID));
            fields.put(Jdbc.PageRole.PAGE_ID, pageId);
            jdbcInsert.execute(fields);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        save(UUID.randomUUID().toString());
        setResponsePage(PageManagementPage.class);
    }

}
