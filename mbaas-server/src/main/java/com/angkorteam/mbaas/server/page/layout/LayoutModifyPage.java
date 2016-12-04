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
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.GroovyScriptValidator;
import com.angkorteam.mbaas.server.validator.LayoutTitleValidator;
import com.angkorteam.mbaas.server.wicket.ProviderUtils;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class LayoutModifyPage extends MBaaSPage {

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
    }

    private void saveButtonOnSubmit(Button button) {
        PropertyResolver.destroy(org.apache.wicket.Application.get());
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
        classLoader.removeSourceCache(this.groovyId);
        classLoader.removeClassCache(this.javaClass);
        String cacheKey = this.javaClass + "_" + layoutRecord.getLayoutId() + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
        getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);

        GroovyCodeSource source = new GroovyCodeSource(this.groovy, this.layoutUuid, "/groovy/script");
        source.setCachable(true);
        Class<?> layoutClass = classLoader.parseClass(source, true);

        GroovyRecord groovyRecord = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(layoutRecord.getGroovyId())).fetchOneInto(groovyTable);
        groovyRecord.setJavaClass(layoutClass.getName());
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

        setResponsePage(LayoutBrowsePage.class);
    }

    @Override
    public String getPageUUID() {
        return LayoutModifyPage.class.getName();
    }

}
