package com.angkorteam.mbaas.server.page.section;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.SectionRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class SectionModifyPage extends MBaaSPage {

    private String sectionId;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private Integer order;
    private TextField<Integer> orderField;
    private TextFeedbackPanel orderFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = Spring.getBean(DSLContext.class);
        SectionTable sectionTable = Tables.SECTION.as("sectionTable");

        PageParameters parameters = getPageParameters();
        this.sectionId = parameters.get("sectionId").toString("");
        SectionPojo section = context.select(sectionTable.fields()).from(sectionTable).where(sectionTable.SECTION_ID.eq(this.sectionId)).fetchOneInto(SectionPojo.class);
        this.order = section.getOrder();
        this.title = section.getTitle();

        this.form = new Form<>("form");
        add(this.form);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.orderField = new TextField<>("orderField", new PropertyModel<>(this, "order"));
        this.orderField.setRequired(true);
        this.form.add(this.orderField);
        this.orderFeedback = new TextFeedbackPanel("orderFeedback", this.orderField);
        this.form.add(this.orderFeedback);

        this.closeButton = new BookmarkablePageLink<>("closeButton", SectionBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        SectionTable sectionTable = Tables.SECTION.as("sectionTable");
        SectionRecord sectionRecord = context.select(sectionTable.fields()).from(sectionTable).where(sectionTable.SECTION_ID.eq(this.sectionId)).fetchOneInto(sectionTable);
        sectionRecord.setTitle(this.title);
        sectionRecord.setOrder(this.order);
        sectionRecord.update();
        setResponsePage(SectionBrowsePage.class);
    }

    @Override
    public String getPageUUID() {
        return SectionModifyPage.class.getName();
    }

}
