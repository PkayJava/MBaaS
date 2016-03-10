package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.function.MariaDBFunction;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
@Mount("/document/modify")
@AuthorizeInstantiation("administrator")
public class DocumentModifyPage extends MasterPage {

    private String collectionId;
    private CollectionPojo collection;

    private String documentId;

    private Map<String, Object> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Document :: " + collection.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.documentId = getPageParameters().get("documentId").toString();
        this.collectionId = getPageParameters().get("collectionId").toString();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        DSLContext context = getDSLContext();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        this.fields = new HashMap<>();
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributePojo> attributes = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionId))
                .and(attributeTable.JAVA_TYPE.eq(String.class.getName())
                        .or(attributeTable.JAVA_TYPE.eq(Date.class.getName()))
                        .or(attributeTable.JAVA_TYPE.eq(Integer.class.getName())))
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        Map<String, AttributePojo> virtualAttributes = new HashMap<>();
        for (AttributePojo attribute : context.select(attributeTable.fields()).from(attributeTable).fetchInto(AttributePojo.class)) {
            virtualAttributes.put(attribute.getAttributeId(), attribute);
        }

        List<String> selectFields = new ArrayList<>();

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            FieldPanel fieldPanel = new FieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
            if (attribute.getVirtual()) {
                AttributePojo masterAttribute = virtualAttributes.get(attribute.getVirtualAttributeId());
                String column = MariaDBFunction.columnGet(masterAttribute.getName(), attribute.getName(), attribute.getJavaType(), attribute.getName());
                selectFields.add(column);
            } else {
                selectFields.add(attribute.getName());
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        Map<String, Object> document = getJdbcTemplate().queryForMap("select " + StringUtils.join(selectFields, ", ") + " from `" + collectionRecord.getName() + "` where " + collectionRecord.getName() + "_id = ?", this.documentId);
        if (document != null && !document.isEmpty()) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                this.fields.put(entry.getKey(), entry.getValue());
            }
        }


        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", DocumentManagementPage.class, parameters);

        Button saveButton = new Button("saveButton");
        saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(fields);
        this.form.add(closeLink);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

        DocumentModifyRequest requestBody = new DocumentModifyRequest();
        requestBody.setDocument(this.fields);

        DocumentFunction.modifyDocument(getDSLContext(), getJdbcTemplate(), collectionRecord.getName(), this.documentId, requestBody);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}
