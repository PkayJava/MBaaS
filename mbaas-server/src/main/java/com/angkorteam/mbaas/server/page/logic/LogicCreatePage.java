package com.angkorteam.mbaas.server.page.logic;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.renderer.MenuChoiceRenderer;
import com.angkorteam.mbaas.server.select2.MenuChoiceProvider;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
@Mount("/logic/create")
public class LogicCreatePage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogicCreatePage.class);

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

    @Override
    public String getPageHeader() {
        return "Create New Page";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        this.menuField = new Select2SingleChoice<>("menuField", new PropertyModel<>(this, "menu"), new MenuChoiceProvider(getSession().getApplicationCode()), new MenuChoiceRenderer());
        this.menuField.setRequired(true);
        this.form.add(this.menuField);
        this.menuFeedback = new TextFeedbackPanel("menuFeedback", this.menuField);
        this.form.add(this.menuFeedback);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.javascript = getString("logic.script");
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

        this.html = getString("logic.html");
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
        String pageId = UUID.randomUUID().toString();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Page.PAGE_ID, pageId);
        fields.put(Jdbc.Page.DATE_CREATED, new Date());
        fields.put(Jdbc.Page.DATE_MODIFIED, new Date());
        fields.put(Jdbc.Page.TITLE, this.title);
        fields.put(Jdbc.Page.MENU_ID, this.menu.get(Jdbc.Menu.MENU_ID));
        fields.put(Jdbc.Page.DESCRIPTION, this.description);
        fields.put(Jdbc.Page.JAVASCRIPT, this.javascript);
        fields.put(Jdbc.Page.HTML, this.html);
        fields.put(Jdbc.Page.SECURITY, SecurityEnum.Denied.getLiteral());
        fields.put(Jdbc.Page.USER_ID, getSession().getApplicationUserId());
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.PAGE);
        jdbcInsert.execute(fields);
        setResponsePage(LogicManagementPage.class);
    }

}
