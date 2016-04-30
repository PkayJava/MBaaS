package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.page.collection.CollectionManagementPage;
import com.angkorteam.mbaas.server.template.TextFieldPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 3/7/16.
 */
@Mount("/document/create")
@AuthorizeInstantiation({"administrator", "backoffice"})
public class DocumentCreatePage extends MasterPage {

    private String collectionId;
    private CollectionPojo collection;

    private Map<String, Object> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Document :: " + this.collection.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.fields = new HashMap<>();
        this.collectionId = getPageParameters().get("collectionId").toString();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        DSLContext context = getDSLContext();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributePojo> attributes = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionId))
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            TextFieldPanel fieldPanel = new TextFieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
        }

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<Void>("closeLink", DocumentManagementPage.class, parameters);

        Button saveButton = new Button("saveButton");
        saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(fields);
        this.form.add(closeLink);
        this.form.add(saveButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        DSLContext context = getDSLContext();
        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(this.collection.getCollectionId())).fetchOneInto(collectionTable);
        if (getSession().isBackOffice() && !collectionRecord.getOwnerUserId().equals(getSession().getUserId())) {
            setResponsePage(CollectionManagementPage.class);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = getDSLContext().select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        requestBody.setDocument(fields);
        String documentId = UUID.randomUUID().toString();
        DocumentFunction.insertDocument(getDSLContext(), getJdbcTemplate(), getSession().getUserId(), documentId, collectionRecord.getName(), requestBody);
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}
