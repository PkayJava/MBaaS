package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.EavDateTimeTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.AttributeExtraEnum;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.template.TextFieldPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.math.BigDecimal;
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
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        List<String> joins = new ArrayList<>();
        List<String> eavFields = new ArrayList<>();

        boolean hasEav = false;
        for (AttributePojo attribute : attributes) {
            if (attribute.getEav()) {
                hasEav = true;
                AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attribute.getAttributeType());
                String eavTable = null;
                String eavField = null;
                // eav datetime
                if (attributeType == AttributeTypeEnum.Time
                        || attributeType == AttributeTypeEnum.Date
                        || attributeType == AttributeTypeEnum.DateTime) {
                    eavTable = Tables.EAV_DATE_TIME.getName();
                    eavField = Tables.EAV_DATE_TIME.getName() + "." + Tables.EAV_DATE_TIME.DOCUMENT_ID.getName();
                }
                // eav varchar
                if (attributeType == AttributeTypeEnum.Character
                        || attributeType == AttributeTypeEnum.String) {
                    eavTable = Tables.EAV_VARCHAR.getName();
                    eavField = Tables.EAV_VARCHAR.getName() + "." + Tables.EAV_VARCHAR.DOCUMENT_ID.getName();
                }
                // eav decimal
                if (attributeType == AttributeTypeEnum.Float
                        || attributeType == AttributeTypeEnum.Double) {
                    eavTable = Tables.EAV_DECIMAL.getName();
                    eavField = Tables.EAV_DECIMAL.getName() + "." + Tables.EAV_DECIMAL.DOCUMENT_ID.getName();
                }
                // eav boolean
                if (attributeType == AttributeTypeEnum.Boolean) {
                    eavTable = Tables.EAV_BOOLEAN.getName();
                    eavField = Tables.EAV_BOOLEAN.getName() + "." + Tables.EAV_BOOLEAN.DOCUMENT_ID.getName();
                }
                // eav integer
                if (attributeType == AttributeTypeEnum.Byte
                        || attributeType == AttributeTypeEnum.Short
                        || attributeType == AttributeTypeEnum.Integer
                        || attributeType == AttributeTypeEnum.Long) {
                    eavTable = Tables.EAV_INTEGER.getName();
                    eavField = Tables.EAV_INTEGER.getName() + "." + Tables.EAV_INTEGER.DOCUMENT_ID.getName();
                }
                // eav text
                if (attributeType == AttributeTypeEnum.Text) {
                    eavTable = Tables.EAV_TEXT.getName();
                    eavField = Tables.EAV_TEXT.getName() + "." + Tables.EAV_TEXT.DOCUMENT_ID.getName();
                }
                String join = "LEFT JOIN " + eavTable + " ON " + collection + "." + collection + "_id" + " = " + eavField;
                if (!joins.contains(join)) {
                    joins.add(join);
                }
                eavFields.add("MAX(CASE WHEN attribute.name = '" + attribute.getName() + "' THEN eav_varchar.value ELSE '' END) AS " + attribute.getName());
            }
        }
        if (hasEav) {
            joins.add("LEFT JOIN " + Tables.ATTRIBUTE.getName() + " ON " + Tables.ATTRIBUTE.getName() + "." + Tables.ATTRIBUTE.ATTRIBUTE_ID.getName() + " = " + "");
        }


//        SELECT
//        aaa.aaa_id,
//                aaa.name AS NAME,
//        MAX(CASE WHEN attribute.name = 'email' THEN eav.value ELSE '' END) AS email,
//        MAX(CASE WHEN attribute.name = 'year' THEN eav.value ELSE '' END) AS `year`,
//        MAX(CASE WHEN attribute.name = 'day' THEN eav.value ELSE '' END) AS `day`,
//        MAX(CASE WHEN attribute.name = 'month' THEN eav.value ELSE '' END) AS `month`
//        FROM aaa
//        LEFT JOIN (SELECT eav_varchar.attribute_id AS attribute_id, eav_varchar.document_id AS document_id, eav_varchar.value AS VALUE FROM eav_varchar UNION SELECT eav_integer.attribute_id AS attribute_id, eav_integer.document_id AS document_id, eav_integer.value AS VALUE FROM eav_integer) eav ON aaa.aaa_id = eav.document_id
//        LEFT JOIN attribute ON eav.attribute_id = attribute.attribute_id
//        GROUP BY aaa.aaa_id


//        SELECT
//        abc.abc_id AS document_id,
//                collection.name AS collection_name,
//        MAX(CASE WHEN attribute.name = 'email' THEN eav_varchar.value ELSE '' END) AS email,
//        MAX(CASE WHEN attribute.name = 'dob' THEN eav_varchar.value ELSE '' END) AS dob
//        FROM abc  LEFT JOIN eav_varchar ON abc.abc_id = eav_varchar.document_id
//        LEFT JOIN collection ON collection.collection_id = eav_varchar.collection_id
//        LEFT JOIN attribute ON eav_varchar.attribute_id = attribute.attribute_id

        List<String> selectFields = new ArrayList<>();

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributes) {
            TextFieldPanel fieldPanel = new TextFieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
            selectFields.add(attribute.getName());
        }

        if (!selectFields.isEmpty()) {
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
            Map<String, Object> document = getJdbcTemplate().queryForMap("select " + StringUtils.join(selectFields, ", ") + " from `" + collectionRecord.getName() + "` where " + collectionRecord.getName() + "_id = ?", this.documentId);
            if (document != null && !document.isEmpty()) {
                for (Map.Entry<String, Object> entry : document.entrySet()) {
                    this.fields.put(entry.getKey(), entry.getValue());
                }
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
