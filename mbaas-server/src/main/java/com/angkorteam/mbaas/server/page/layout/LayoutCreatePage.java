package com.angkorteam.mbaas.server.page.layout;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.records.LayoutRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.CmsLayout;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import groovy.lang.GroovyCodeSource;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Date;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class LayoutCreatePage extends MBaaSPage {

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
    public String getPageUUID() {
        return LayoutCreatePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.groovy = getString("layout.groovy");
        this.groovyField = new JavascriptTextArea("groovyField", new PropertyModel<>(this, "groovy"));
        this.groovyField.setRequired(true);
        this.form.add(this.groovyField);
        this.groovyFeedback = new TextFeedbackPanel("groovyFeedback", this.groovyField);
        this.form.add(this.groovyFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.html = getString("layout.html");
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
        System system = Spring.getBean(System.class);
        String uuid = system.randomUUID();

        StringBuffer newGroovy = new StringBuffer(this.groovy.substring(0, this.groovy.lastIndexOf("}")));
        newGroovy.append("\n @Override\n" +
                "        public final String getLayoutUUID () {\n" +
                "            return \"" + uuid + "\";\n" +
                "        } }");

        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        GroovyCodeSource source = new GroovyCodeSource(newGroovy.toString(), GroovyClassLoader.LAYOUT + uuid, "/groovy/script");
        source.setCachable(true);
        Class<? extends CmsLayout> layout = classLoader.parseClass(source, true);

        DSLContext context = Spring.getBean(DSLContext.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        LayoutRecord layoutRecord = context.newRecord(layoutTable);
        layoutRecord.setLayoutId(uuid);
        layoutRecord.setDateCreated(new Date());
        layoutRecord.setDateModified(new Date());
        layoutRecord.setTitle(this.title);
        layoutRecord.setHtml(this.html);
        layoutRecord.setJavaClass(layout.getName());
        layoutRecord.setGroovy(this.groovy);
        layoutRecord.setDescription(this.description);
        layoutRecord.setSystem(false);
        layoutRecord.setModified(true);
        layoutRecord.store();
        setResponsePage(LayoutBrowsePage.class);
    }

}
