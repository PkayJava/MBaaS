package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.template.BooleanPanel;
import com.angkorteam.mbaas.server.template.CharacterPanel;
import com.angkorteam.mbaas.server.template.DatePanel;
import com.angkorteam.mbaas.server.template.DateTimePanel;
import com.angkorteam.mbaas.server.template.DoublePanel;
import com.angkorteam.mbaas.server.template.LongPanel;
import com.angkorteam.mbaas.server.template.StringPanel;
import com.angkorteam.mbaas.server.template.TextPanel;
import com.angkorteam.mbaas.server.template.TimePanel;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentCreatePage extends MBaaSPage {

    private CollectionPojo collection;

    private Map<String, Object> fields;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageUUID() {
        return DocumentCreatePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        this.fields = new HashMap<>();

        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        String collectionId = getPageParameters().get("collectionId").toString();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);
        List<AttributePojo> attributes = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionId))
                .and(attributeTable.NAME.notEqual(this.collection.getName() + "_id"))
                .and(attributeTable.NAME.notEqual("system"))
                .fetchInto(AttributePojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            TypeEnum type = TypeEnum.valueOf(attribute.getType());
            String name = attribute.getName();
            if ("Boolean".equals(type.getLiteral())) {
                BooleanPanel fieldPanel = new BooleanPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Character".equals(type.getLiteral())) {
                CharacterPanel fieldPanel = new CharacterPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("String".equals(type.getLiteral())) {
                StringPanel fieldPanel = new StringPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Text".equals(type.getLiteral())) {
                TextPanel fieldPanel = new TextPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Long".equals(type.getLiteral())) {
                LongPanel fieldPanel = new LongPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Double".equals(type.getLiteral())) {
                DoublePanel fieldPanel = new DoublePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("DateTime".equals(type.getLiteral())) {
                this.fields.put(name, new Date());
                DateTimePanel fieldPanel = new DateTimePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Date".equals(type.getLiteral())) {
                this.fields.put(name, new Date());
                DatePanel fieldPanel = new DatePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Time".equals(type.getLiteral())) {
                TimePanel fieldPanel = new TimePanel(fields.newChildId(), name, this.fields);
                this.fields.put(name, new Date());
                fields.add(fieldPanel);
            }
        }
        this.form.add(fields);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collection.getCollectionId());
        BookmarkablePageLink<Void> closeButton = new BookmarkablePageLink<>("closeButton", DocumentBrowsePage.class, parameters);
        this.form.add(closeButton);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        System system = Spring.getBean(System.class);
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        fields.put(this.collection.getName() + "_id", system.randomUUID());
        fields.put("system", false);
        requestBody.setDocument(fields);
        DocumentFunction.internalInsertDocument(this.collection.getCollectionId(), requestBody);
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collection.getCollectionId());
        setResponsePage(DocumentBrowsePage.class, parameters);
    }
}
