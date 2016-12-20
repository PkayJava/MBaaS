package com.angkorteam.mbaas.server.page.page;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
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
import com.angkorteam.mbaas.server.validator.PagePathValidator;
import com.google.common.base.Strings;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class PageModifyPage extends MBaaSPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageModifyPage.class);

    private String pageUuid;
    private String groovyId;
    private String javaClass;
    private Boolean pageCms;

    private String mountPath;
    private TextField<String> pathField;
    private TextFeedbackPanel pathFeedback;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private List<RolePojo> role;
    private Select2MultipleChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

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
    private Button saveAndCloseButton;

    @Override
    public String getPageUUID() {
        return PageModifyPage.class.getName();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        PageParameters parameters = getPageParameters();

        this.pageUuid = parameters.get("pageId").toString("");

        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        PagePojo page = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(this.pageUuid)).fetchOneInto(PagePojo.class);
        this.title = page.getTitle();
        this.pageCms = page.getCmsPage();
        this.description = page.getDescription();
        this.mountPath = page.getPath();

        GroovyPojo groovy = null;

        if (this.pageCms) {
            this.html = page.getHtml();
            groovy = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(page.getGroovyId())).fetchOneInto(GroovyPojo.class);
            this.groovyId = groovy.getGroovyId();
            this.javaClass = groovy.getJavaClass();
            this.groovy = groovy.getScript();
        }

        if (page.getLayoutId() != null) {
            this.layout = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(page.getLayoutId())).fetchOneInto(LayoutPojo.class);
        }
        this.role = context.select(roleTable.fields()).from(roleTable).innerJoin(pageRoleTable).on(roleTable.ROLE_ID.eq(pageRoleTable.ROLE_ID)).where(pageRoleTable.PAGE_ID.eq(this.pageUuid)).fetchInto(RolePojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.roleField = new Select2MultipleChoice<>("roleField", new PropertyModel<>(this, "role"), new RolesChoiceProvider());
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "mountPath"));
        this.pathField.setRequired(this.pageCms);
        this.pathField.setEnabled(this.pageCms);
        this.pathField.add(new PagePathValidator(this.pageUuid));
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(this.pageCms);
        this.groovyField.setEnabled(this.pageCms);
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
        this.htmlField.setRequired(this.pageCms);
        this.htmlField.setEnabled(this.pageCms);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.layouts = context.select(layoutTable.fields()).from(layoutTable).orderBy(layoutTable.TITLE.asc()).fetchInto(LayoutPojo.class);
        this.layoutField = new DropDownChoice<>("layoutField", new PropertyModel<>(this, "layout"), new PropertyModel<>(this, "layouts"), new LayoutChoiceRenderer());
        this.layoutField.setRequired(this.pageCms);
        this.layoutField.setEnabled(this.pageCms);
        this.form.add(this.layoutField);
        this.layoutFeedback = new TextFeedbackPanel("layoutFeedback", this.layoutField);
        this.form.add(this.layoutFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", PageBrowsePage.class);
        this.form.add(this.closeButton);

        this.saveAndCloseButton = new Button("saveAndCloseButton");
        this.saveAndCloseButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveAndCloseButton);
    }

    private void saveButtonOnSubmit(Button button) {
        System system = Spring.getBean(System.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        if (this.pageCms) {
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

            PageRecord pageRecord = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(this.pageUuid)).fetchOneInto(pageTable);
            Application.get().unmount(pageRecord.getPath());

            GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);

            // pre-compile to get class name
            GroovyCodeSource source = new GroovyCodeSource(this.groovy, this.groovyId, "/groovy/script");
            source.setCachable(false);
            Class<?> pageClass = classLoader.parseClass(source, false);
            String javaClassName = pageClass.getName();

            classLoader.removeSourceCache(this.javaClass);
            classLoader.removeClassCache(this.javaClass);

            classLoader.writeGroovy(pageClass.getName(), this.groovy);
            pageClass = classLoader.compileGroovy(pageClass.getName());

            GroovyRecord groovyRecord = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(pageRecord.getGroovyId())).fetchOneInto(groovyTable);
            groovyRecord.setScript(this.groovy);
            groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
            groovyRecord.setJavaClass(javaClassName);
            groovyRecord.update();

            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();

            context.delete(pageRoleTable).where(pageRoleTable.PAGE_ID.eq(this.pageUuid)).execute();

            Application.get().unmount(this.mountPath);
            JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
            for (Class<?> clazz : classLoader.getLoadedClasses()) {
                try {
                    String groovyId = jdbcTemplate.queryForObject("SELECT groovy_id FROM groovy WHERE java_class = ?", String.class, clazz.getName());
                    if (!Strings.isNullOrEmpty(groovyId)) {
                        String path = jdbcTemplate.queryForObject("SELECT path FROM page WHERE groovy_id = ?", String.class, groovyId);
                        if (!Strings.isNullOrEmpty(path)) {
                            Application.get().mountPage(path, (Class<? extends Page>) clazz);
                        }
                    }
                } catch (EmptyResultDataAccessException e) {
                }
            }

            pageRecord.setTitle(this.title);
            pageRecord.setDescription(this.description);
            pageRecord.setHtml(this.html);
            pageRecord.setHtmlCrc32(String.valueOf(htmlCrc32));
            pageRecord.setModified(true);
            pageRecord.setDateModified(new Date());
            pageRecord.setPath(this.mountPath);
            if (this.layout != null) {
                pageRecord.setLayoutId(this.layout.getLayoutId());
            } else {
                pageRecord.setLayoutId(null);
            }
            pageRecord.update();

            if (this.role != null) {
                for (RolePojo role : this.role) {
                    PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
                    pageRoleRecord.setPageRoleId(system.randomUUID());
                    pageRoleRecord.setRoleId(role.getRoleId());
                    pageRoleRecord.setPageId(this.pageUuid);
                    pageRoleRecord.store();
                }
            }

            Class<?> clazzes[] = classLoader.getLoadedClasses();
            for (int i = 0; i < clazzes.length; i++) {
                Class<?> clazz = null;
                String groovyId = null;
                String path = null;
                try {
                    clazz = clazzes[i];
                    groovyId = jdbcTemplate.queryForObject("SELECT groovy_id FROM groovy WHERE java_class = ?", String.class, clazz.getName());
                    if (!Strings.isNullOrEmpty(groovyId)) {
                        path = jdbcTemplate.queryForObject("SELECT path FROM page WHERE groovy_id = ?", String.class, groovyId);
                        if (!Strings.isNullOrEmpty(path)) {
                            Application.get().mountPage(path, (Class<? extends org.apache.wicket.Page>) clazz);
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.info("reload error {} class {} groovy id {} path {}", e.getMessage(), (clazz != null ? clazz.getSimpleName() : ""), groovyId, path);
                }
            }
        } else {
            PageRecord pageRecord = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(this.pageUuid)).fetchOneInto(pageTable);
            pageRecord.setTitle(this.title);
            pageRecord.setDescription(this.description);
            pageRecord.setDateModified(new Date());
            pageRecord.update();

            if (this.role != null) {
                for (RolePojo role : this.role) {
                    PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
                    pageRoleRecord.setPageRoleId(system.randomUUID());
                    pageRoleRecord.setRoleId(role.getRoleId());
                    pageRoleRecord.setPageId(this.pageUuid);
                    pageRoleRecord.store();
                }
            }
        }

        if ("saveButton".equals(button.getId())) {
            PageParameters parameters = new PageParameters();
            parameters.add("pageId", this.pageUuid);
            setResponsePage(PageModifyPage.class, parameters);
        } else {
            setResponsePage(PageBrowsePage.class);
        }
    }
}
