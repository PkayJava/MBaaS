package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.GroovyPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
import com.angkorteam.mbaas.model.entity.tables.records.GroovyRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RestRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.RestNameValidator;
import com.angkorteam.mbaas.server.validator.RestPathMethodValidator;
import groovy.lang.GroovyCodeSource;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 8/3/16.
 */
public class RestModifyPage extends MBaaSPage {

    private String restId;
    private String groovyId;
    private String javaClass;

    private String requestPath;
    private TextField<String> requestPathField;
    private TextFeedbackPanel requestPathFeedback;

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
        return RestModifyPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        RestTable restTable = Tables.REST.as("restTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        this.restId = getPageParameters().get("restId").toString("");

        RestPojo restRecord = context.select(restTable.fields()).from(restTable).where(restTable.REST_ID.eq(this.restId)).fetchOneInto(RestPojo.class);
        GroovyPojo groovy = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(restRecord.getGroovyId())).fetchOneInto(GroovyPojo.class);
        this.groovyId = groovy.getGroovyId();
        this.javaClass = groovy.getJavaClass();

        this.form = new Form<>("form");
        layout.add(this.form);

        this.method = restRecord.getMethod();
        this.methods = Arrays.asList(HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.POST.name(), HttpMethod.PUT.name());
        this.methodField = new DropDownChoice<>("methodField", new PropertyModel<>(this, "method"), new PropertyModel<>(this, "methods"));
        this.methodField.setOutputMarkupId(true);
        this.methodField.setRequired(true);
        this.form.add(this.methodField);
        this.methodFeedback = new TextFeedbackPanel("methodFeedback", this.methodField);
        this.form.add(this.methodFeedback);

        this.requestPath = restRecord.getPath();
        this.requestPathField = new TextField<>("requestPathField", new PropertyModel<>(this, "requestPath"));
        this.requestPathField.setOutputMarkupId(true);
        this.requestPathField.setRequired(true);
        this.form.add(this.requestPathField);
        this.requestPathFeedback = new TextFeedbackPanel("requestPathFeedback", this.requestPathField);
        this.form.add(this.requestPathFeedback);

        this.description = restRecord.getDescription();
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setOutputMarkupId(true);
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.name = restRecord.getName();
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setOutputMarkupId(true);
        this.nameField.add(new RestNameValidator(this.restId));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.groovy = groovy.getScript();
        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(true);
        this.form.add(this.groovyField);
        this.groovyFeedback = new TextFeedbackPanel("groovyFeedback", this.groovyField);
        this.form.add(this.groovyFeedback);

        this.closeButton = new BookmarkablePageLink<>("closeButton", RestBrowsePage.class);
        this.form.add(this.closeButton);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.form.add(new RestPathMethodValidator(this.restId, this.requestPathField, this.methodField));
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

        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        classLoader.removeSourceCache(this.groovyId);
        classLoader.removeClassCache(this.javaClass);

        GroovyCodeSource source = new GroovyCodeSource(this.groovy, this.groovyId, "/groovy/script");
        source.setCachable(true);
        Class<?> serviceClass = classLoader.parseClass(source, true);

        GroovyRecord groovyRecord = context.select(groovyTable.fields()).from(groovyTable).where(groovyTable.GROOVY_ID.eq(this.groovyId)).fetchOneInto(groovyTable);
        groovyRecord.setScript(this.groovy);
        groovyRecord.setJavaClass(serviceClass.getName());
        groovyRecord.update();

        RestRecord restRecord = context.select(restTable.fields()).from(restTable).where(restTable.REST_ID.eq(this.restId)).fetchOneInto(restTable);
        restRecord.setName(this.name);
        restRecord.setDescription(this.description);
        restRecord.setPath(this.requestPath);
        restRecord.setMethod(this.method);
        restRecord.update();
        setResponsePage(RestBrowsePage.class);
    }

}
