package com.angkorteam.mbaas.server.page.cms.block;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.BlockPanel;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/block/modify")
public class BlockModifyPage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockModifyPage.class);

    private String blockId;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String code;
    private Label codeLabel;

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
        return "Modify Block";
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

        this.blockId = getPageParameters().get("blockId").toString("");

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new JobNameValidator(getSession().getApplicationCode()));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.codeLabel = new Label("codeLabel", new PropertyModel<>(this, "code"));
        this.form.add(codeLabel);

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

        this.htmlField = new HtmlTextArea("htmlField", new PropertyModel<>(this, "html"));
        this.htmlField.setRequired(true);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.BLOCK + " WHERE " + Jdbc.Block.BLOCK_ID + " = ?", this.blockId);
        this.title = (String) pageRecord.get(Jdbc.MasterPage.TITLE);
        this.code = (String) pageRecord.get(Jdbc.MasterPage.CODE);
        this.javascript = (String) pageRecord.get(Jdbc.MasterPage.STAGE_JAVASCRIPT);
        this.description = (String) pageRecord.get(Jdbc.MasterPage.DESCRIPTION);
        this.html = (String) pageRecord.get(Jdbc.MasterPage.STAGE_HTML);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Block.BLOCK_ID, this.blockId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Block.TITLE, this.title);
        fields.put(Jdbc.Block.DESCRIPTION, this.description);
        fields.put(Jdbc.Block.STAGE_JAVASCRIPT, this.javascript);
        fields.put(Jdbc.Block.STAGE_HTML, this.html);
        fields.put(Jdbc.Block.MODIFIED, true);
        fields.put(Jdbc.Block.DATE_MODIFIED, new Date());

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.BLOCK);
        jdbcUpdate.execute(fields, wheres);

        String cacheKey = BlockPanel.class.getName() + "_" + this.blockId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
        getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
        setResponsePage(BlockManagementPage.class);
    }

}
