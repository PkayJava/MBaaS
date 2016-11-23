package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.RestRoleTable;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.GroovyRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RestRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RestRoleRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.select2.RolesChoiceProvider;
import com.angkorteam.mbaas.server.validator.GroovyScriptValidator;
import com.angkorteam.mbaas.server.validator.RestNameValidator;
import com.angkorteam.mbaas.server.validator.RestPathMethodValidator;
import com.google.common.collect.Lists;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.elasticsearch.common.Strings;
import org.jooq.DSLContext;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 8/3/16.
 */
public class RestCreatePage extends MBaaSPage {

    private String restUuid;

    private String requestPath;
    private TextField<String> requestPathField;
    private TextFeedbackPanel requestPathFeedback;

    private List<RolePojo> role;
    private Select2MultipleChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private List<String> methods;
    private String method;
    private DropDownChoice<String> methodField;
    private TextFeedbackPanel methodFeedback;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String groovy;
    private JavascriptTextArea groovyField;
    private TextFeedbackPanel groovyFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return RestCreatePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
        System system = Spring.getBean(System.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        Configuration configuration = system.getConfiguration();

        this.restUuid = system.randomUUID();

        this.form = new Form<>("form");
        layout.add(this.form);

        this.role = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Configuration.ROLE_SERVICE))).fetchInto(RolePojo.class);
        this.roleField = new Select2MultipleChoice<>("roleField", new PropertyModel<>(this, "role"), new RolesChoiceProvider());
        this.form.add(this.roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(this.roleFeedback);

        this.methods = Arrays.asList(HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.POST.name(), HttpMethod.PUT.name());
        this.methodField = new DropDownChoice<>("methodField", new PropertyModel<>(this, "method"), new PropertyModel<>(this, "methods"));
        this.methodField.setOutputMarkupId(true);
        this.methodField.setRequired(true);
        this.form.add(this.methodField);
        this.methodFeedback = new TextFeedbackPanel("methodFeedback", this.methodField);
        this.form.add(this.methodFeedback);

        this.requestPathField = new TextField<>("requestPathField", new PropertyModel<>(this, "requestPath"));
        this.requestPathField.setOutputMarkupId(true);
        this.requestPathField.setRequired(true);
        this.form.add(this.requestPathField);
        this.requestPathFeedback = new TextFeedbackPanel("requestPathFeedback", this.requestPathField);
        this.form.add(this.requestPathFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setOutputMarkupId(true);
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setOutputMarkupId(true);
        this.nameField.setRequired(true);
        this.nameField.add(new RestNameValidator());
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.groovy = String.format(getString("groovy.script"), this.restUuid);
        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(true);
        this.groovyField.add(new GroovyScriptValidator());
        this.form.add(this.groovyField);
        this.groovyFeedback = new TextFeedbackPanel("groovyFeedback", this.groovyField);
        this.form.add(this.groovyFeedback);

        this.closeButton = new BookmarkablePageLink<>("closeButton", RestBrowsePage.class);
        this.form.add(this.closeButton);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.form.add(new RestPathMethodValidator(this.requestPathField, this.methodField));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        System system = Spring.getBean(System.class);
        RestTable restTable = Tables.REST.as("restTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

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
        Class<?> serviceClass = classLoader.parseClass(source, true);

        GroovyRecord groovyRecord = context.newRecord(groovyTable);
        groovyRecord.setGroovyId(groovyId);
        groovyRecord.setSystem(false);
        groovyRecord.setJavaClass(serviceClass.getName());
        groovyRecord.setScript(this.groovy);
        groovyRecord.setScriptCrc32(String.valueOf(groovyCrc32));
        groovyRecord.store();

        String[] segments = StringUtils.split(this.requestPath, "/");
        List<String> newSegments = Lists.newLinkedList();
        for (String segment : segments) {
            if (!Strings.isNullOrEmpty(segment)) {
                if (StringUtils.startsWithIgnoreCase(segment, "{") && StringUtils.endsWithIgnoreCase(segment, "}")) {
                    newSegments.add(RestPathMethodValidator.PATH);
                } else {
                    newSegments.add(segment);
                }
            }
        }

        RestRecord restRecord = context.newRecord(restTable);
        restRecord.setRestId(this.restUuid);
        restRecord.setSystem(false);
        restRecord.setName(this.name);
        restRecord.setPath(this.requestPath);
        restRecord.setPathVariable("/" + StringUtils.join(newSegments, "/"));
        restRecord.setSegment(StringUtils.countMatches(this.requestPath, '/'));
        restRecord.setSecurity(SecurityEnum.Granted.getLiteral());
        restRecord.setDescription(this.description);
        restRecord.setMethod(this.method);
        restRecord.setGroovyId(groovyId);
        restRecord.store();

        RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
        for (RolePojo role : this.role) {
            RestRoleRecord restRoleRecord = context.newRecord(restRoleTable);
            restRoleRecord.setRestRoleId(system.randomUUID());
            restRoleRecord.setRoleId(role.getRoleId());
            restRoleRecord.setRestId(this.restUuid);
            restRoleRecord.store();
        }

        setResponsePage(RestBrowsePage.class);
    }

}
