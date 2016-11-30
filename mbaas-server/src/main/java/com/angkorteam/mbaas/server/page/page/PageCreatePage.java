package com.angkorteam.mbaas.server.page.page;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.PageRoleTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.GroovyRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PageRecord;
import com.angkorteam.mbaas.model.entity.tables.records.PageRoleRecord;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.choice.LayoutChoiceRenderer;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.select2.RolesChoiceProvider;
import com.angkorteam.mbaas.server.validator.GroovyScriptValidator;
import com.angkorteam.mbaas.server.validator.PageCodeValidator;
import com.angkorteam.mbaas.server.validator.PagePathValidator;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class PageCreatePage extends MBaaSPage {

    private String pageUuid;

    private String mountPath;
    private TextField<String> pathField;
    private TextFeedbackPanel pathFeedback;

    private List<RolePojo> role;
    private Select2MultipleChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String code;
    private TextField<String> codeField;
    private TextFeedbackPanel codeFeedback;

    private String html;
    private HtmlTextArea htmlField;
    private TextFeedbackPanel htmlFeedback;

    private String groovy;
    private JavascriptTextArea groovyField;
    private TextFeedbackPanel groovyFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private List<LayoutPojo> layouts;
    private LayoutPojo layout;
    private DropDownChoice<LayoutPojo> layoutField;
    private TextFeedbackPanel layoutFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return PageCreatePage.class.getName();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        System system = Spring.getBean(System.class);
        this.pageUuid = system.randomUUID();

        this.form = new Form<>("form");
        layout.add(this.form);

        this.roleField = new Select2MultipleChoice<>("roleField", new PropertyModel<>(this, "role"), new RolesChoiceProvider());
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "mountPath"));
        this.pathField.setRequired(true);
        this.pathField.add(new PagePathValidator());
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.codeField = new TextField<>("codeField", new PropertyModel<>(this, "code"));
        this.codeField.setRequired(true);
        this.codeField.add(new PageCodeValidator());
        this.form.add(this.codeField);
        this.codeFeedback = new TextFeedbackPanel("codeFeedback", this.codeField);
        this.form.add(this.codeFeedback);

        this.groovy = String.format(getString("page.groovy"), "ExamplePage", this.pageUuid);
        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(true);
        this.groovyField.add(new GroovyScriptValidator());
        this.form.add(this.groovyField);
        this.groovyFeedback = new TextFeedbackPanel("groovyFeedback", this.groovyField);
        this.form.add(this.groovyFeedback);

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

        DSLContext context = Spring.getBean(DSLContext.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        this.layouts = context.select(layoutTable.fields()).from(layoutTable).fetchInto(LayoutPojo.class);
        this.layoutField = new DropDownChoice<>("layoutField", new PropertyModel<>(this, "layout"), new PropertyModel<>(this, "layouts"), new LayoutChoiceRenderer());
        this.layoutField.setRequired(true);
        this.form.add(this.layoutField);
        this.layoutFeedback = new TextFeedbackPanel("layoutFeedback", this.layoutField);
        this.form.add(this.layoutFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", PageBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        System system = Spring.getBean(System.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

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

        String groovyId = system.randomUUID();

        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        GroovyCodeSource source = new GroovyCodeSource(this.groovy, groovyId, "/groovy/script");
        source.setCachable(true);
        Class<?> pageClass = classLoader.parseClass(source, true);

        GroovyRecord groovyRecord = context.newRecord(groovyTable);
        groovyRecord.setGroovyId(groovyId);
        groovyRecord.setScript(this.groovy);
        groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
        groovyRecord.setSystem(false);
        groovyRecord.setJavaClass(pageClass.getName());
        groovyRecord.store();

        Application.get().mountPage(this.mountPath, (Class<? extends Page>) pageClass);

        PageRecord pageRecord = context.newRecord(pageTable);
        pageRecord.setPageId(this.pageUuid);
        if (this.layout != null) {
            pageRecord.setLayoutId(this.layout.getLayoutId());
        } else {
            pageRecord.setLayoutId(null);
        }
        pageRecord.setDateCreated(new Date());
        pageRecord.setDateModified(new Date());
        pageRecord.setGroovyId(groovyId);
        pageRecord.setTitle(this.title);
        pageRecord.setHtml(this.html);
        pageRecord.setHtmlCrc32(String.valueOf(htmlCrc32));
        pageRecord.setCode(this.code);
        pageRecord.setPath(this.mountPath);
        pageRecord.setDescription(this.description);
        pageRecord.setSystem(false);
        pageRecord.setModified(true);
        pageRecord.setCmsPage(true);
        pageRecord.store();

        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        for (RolePojo role : this.role) {
            PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
            pageRoleRecord.setPageRoleId(system.randomUUID());
            pageRoleRecord.setRoleId(role.getRoleId());
            pageRoleRecord.setPageId(this.pageUuid);
            pageRoleRecord.store();
        }

        setResponsePage(PageBrowsePage.class);
    }
}
