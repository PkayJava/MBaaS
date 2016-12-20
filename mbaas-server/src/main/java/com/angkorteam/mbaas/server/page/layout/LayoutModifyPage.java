package com.angkorteam.mbaas.server.page.layout;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.records.GroovyRecord;
import com.angkorteam.mbaas.model.entity.tables.records.LayoutRecord;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.GroovyScriptValidator;
import com.angkorteam.mbaas.server.validator.LayoutTitleValidator;
import com.google.common.base.Strings;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class LayoutModifyPage extends MBaaSPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutModifyPage.class);

    private String layoutUuid;
    private String groovyId;
    private String javaClass;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String html;
    private HtmlTextArea htmlField;
    private TextFeedbackPanel htmlFeedback;

    private String groovy;
    private JavascriptTextArea groovyField;
    private TextFeedbackPanel groovyFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Form<Void> form;
    private Button saveButton;
    private Button saveAndCloseButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    @Override
    protected void doInitialize(Border content) {
        add(content);
        PageParameters parameters = getPageParameters();

        this.layoutUuid = parameters.get("layoutId").toString("");

        DSLContext context = Spring.getBean(DSLContext.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        LayoutPojo layout = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(this.layoutUuid)).fetchOneInto(LayoutPojo.class);
        GroovyPojo groovy = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(layout.getGroovyId())).fetchOneInto(GroovyPojo.class);

        this.title = layout.getTitle();
        this.description = layout.getDescription();
        this.html = layout.getHtml();
        this.groovy = groovy.getScript();
        this.groovyId = groovy.getGroovyId();
        this.javaClass = groovy.getJavaClass();

        this.form = new Form<>("form");
        content.add(this.form);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.titleField.add(new LayoutTitleValidator(this.layoutUuid));
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(true);
        this.groovyField.add(new GroovyScriptValidator(this.groovyId));
        this.form.add(this.groovyField);
        this.groovyFeedback = new TextFeedbackPanel("groovyFeedback", this.groovyField);
        this.form.add(this.groovyFeedback);

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

        this.closeButton = new BookmarkablePageLink<>("closeButton", LayoutBrowsePage.class);
        this.form.add(this.closeButton);

        this.saveAndCloseButton = new Button("saveAndCloseButton");
        this.saveAndCloseButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveAndCloseButton);
    }

    private void saveButtonOnSubmit(Button button) {
        File htmlTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".html");
        try {
            FileUtils.write(htmlTemp, this.html, "UTF-8");
        } catch (IOException e) {
        }

        long htmlCrc32 = -1;
        try {
            htmlCrc32 = FileUtils.checksumCRC32(htmlTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.deleteQuietly(htmlTemp);

        File groovyTemp = new File(FileUtils.getTempDirectory(), java.lang.System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + ".groovy");
        try {
            FileUtils.write(groovyTemp, this.groovy, "UTF-8");
        } catch (IOException e) {
        }

        long groovyCrc32 = -1;
        try {
            groovyCrc32 = FileUtils.checksumCRC32(groovyTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.deleteQuietly(groovyTemp);

        DSLContext context = Spring.getBean(DSLContext.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        LayoutRecord layoutRecord = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(this.layoutUuid)).fetchOneInto(layoutTable);

        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);

        getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();

        GroovyCodeSource source = new GroovyCodeSource(this.groovy, this.layoutUuid, "/groovy/script");
        source.setCachable(false);
        Class<?> layoutClass = classLoader.parseClass(source, false);
        String javaClass = layoutClass.getName();

        classLoader.removeSourceCache(this.javaClass);
        classLoader.removeClassCache(this.javaClass);

        classLoader.writeGroovy(javaClass, this.groovy);
        classLoader.compileGroovy(javaClass);

        GroovyRecord groovyRecord = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(layoutRecord.getGroovyId())).fetchOneInto(groovyTable);
        groovyRecord.setJavaClass(javaClass);
        groovyRecord.setScript(this.groovy);
        groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
        groovyRecord.update();

        layoutRecord.setTitle(this.title);
        layoutRecord.setDescription(this.description);
        layoutRecord.setHtml(this.html);
        layoutRecord.setHtmlCrc32(String.valueOf(htmlCrc32));
        layoutRecord.setModified(true);
        layoutRecord.setDateModified(new Date());
        layoutRecord.update();

        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        Class<?> clazzes[] = classLoader.getLoadedClasses();
        for (int i = 0; i < clazzes.length; i++) {
            Class<?> clazz = null;
            String tempGroovyId = null;
            String path = null;
            try {
                clazz = clazzes[i];
                tempGroovyId = jdbcTemplate.queryForObject("SELECT groovy_id FROM groovy WHERE java_class = ?", String.class, clazz.getName());
                if (!Strings.isNullOrEmpty(tempGroovyId)) {
                    path = jdbcTemplate.queryForObject("SELECT path FROM page WHERE groovy_id = ?", String.class, tempGroovyId);
                    if (!Strings.isNullOrEmpty(path)) {
                        Application.get().mountPage(path, (Class<? extends org.apache.wicket.Page>) clazz);
                    }
                }
            } catch (Throwable e) {
                LOGGER.info("reload error {} class {} groovy id {} path {}", e.getMessage(), (clazz != null ? clazz.getSimpleName() : ""), tempGroovyId, path);
            }
        }

        if ("saveButton".equals(button.getId())) {
            PageParameters parameters = new PageParameters();
            parameters.add("layoutId", this.layoutUuid);
            setResponsePage(LayoutModifyPage.class, parameters);
        } else {
            setResponsePage(LayoutBrowsePage.class);
        }
    }

    @Override
    public String getPageUUID() {
        return LayoutModifyPage.class.getName();
    }

}
