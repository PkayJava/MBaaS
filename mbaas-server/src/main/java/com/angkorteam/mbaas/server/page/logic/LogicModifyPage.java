package com.angkorteam.mbaas.server.page.logic;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.flow.FlowPage;
import com.angkorteam.mbaas.server.renderer.MenuChoiceRenderer;
import com.angkorteam.mbaas.server.select2.MenuChoiceProvider;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/logic/modify")
public class LogicModifyPage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogicModifyPage.class);

    private String pageId;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String html;
    private JavascriptTextArea htmlField;
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

    @Override
    public String getPageHeader() {
        return "Modify Logic";
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

        this.menu = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.MENU_ID + " = ?", pageRecord.get(Jdbc.Page.MENU_ID));
        this.menuField = new Select2SingleChoice<>("menuField", new PropertyModel<>(this, "menu"), new MenuChoiceProvider(getSession().getApplicationCode()), new MenuChoiceRenderer());
        this.menuField.setRequired(true);
        this.form.add(this.menuField);
        this.menuFeedback = new TextFeedbackPanel("menuFeedback", this.menuField);
        this.form.add(this.menuFeedback);

        this.title = (String) pageRecord.get(Jdbc.Page.TITLE);
        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new JobNameValidator(getSession().getApplicationCode()));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.javascript = (String) pageRecord.get(Jdbc.Page.JAVASCRIPT);
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

        this.html = (String) pageRecord.get(Jdbc.Page.HTML);
        this.htmlField = new JavascriptTextArea("htmlField", new PropertyModel<>(this, "html"));
        this.htmlField.setRequired(true);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Page.PAGE_ID, this.pageId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Page.DATE_MODIFIED, new Date());
        fields.put(Jdbc.Page.TITLE, this.title);
        fields.put(Jdbc.Page.MENU_ID, this.menu.get(Jdbc.Menu.MENU_ID));
        fields.put(Jdbc.Page.DESCRIPTION, this.description);
        fields.put(Jdbc.Page.JAVASCRIPT, this.javascript);
        fields.put(Jdbc.Page.HTML, this.html);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.PAGE);
        jdbcUpdate.execute(fields, wheres);
        setResponsePage(LogicManagementPage.class);
        String cacheKey = FlowPage.class.getName() + "_" + this.pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
        String filename = FlowPage.class.getName().replaceAll("\\.", "/") + "_" + this.pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
        File temp = new File(FileUtils.getTempDirectory(), filename);
        FileUtils.deleteQuietly(temp);
        getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
    }

}
