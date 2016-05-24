package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.template.TextFieldPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
@Mount("/document/modify")
@AuthorizeInstantiation({"administrator"})
public class DocumentModifyPage extends MasterPage {

    private String collectionId;
    private Map<String, Object> collection;

    private String documentId;

    private Map<String, Object> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Document :: " + collection.get(Jdbc.Collection.NAME);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.documentId = getPageParameters().get("documentId").toString();
        this.collectionId = getPageParameters().get("collectionId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        this.collection = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", this.collectionId);

        this.fields = new HashMap<>();

        List<Map<String, Object>> attributes = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.SYSTEM + " = ?", this.collectionId, false);

        List<String> joins = new ArrayList<>();
        List<String> attributeJoins = new ArrayList<>();
        List<String> names = new ArrayList<>();

        boolean hasEav = false;
        for (Map<String, Object> attribute : attributes) {
            if ((boolean) attribute.get(Jdbc.Attribute.EAV)) {
                hasEav = true;
                AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf((String) attribute.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
                String eavTable = null;
                String eavField = null;
                // eav time
                if (attributeType == AttributeTypeEnum.Time) {
                    eavTable = Jdbc.EAV_TIME;
                    eavField = Jdbc.EAV_TIME + "." + Jdbc.EavTime.DOCUMENT_ID;
                }
                // eav date
                if (attributeType == AttributeTypeEnum.Date) {
                    eavTable = Jdbc.EAV_DATE;
                    eavField = Jdbc.EAV_DATE + "." + Jdbc.EavDate.DOCUMENT_ID;
                }
                // eav datetime
                if (attributeType == AttributeTypeEnum.DateTime) {
                    eavTable = Jdbc.EAV_DATE_TIME;
                    eavField = Jdbc.EAV_DATE_TIME + "." + Jdbc.EavDateTime.DOCUMENT_ID;
                }
                // eav varchar
                if (attributeType == AttributeTypeEnum.String) {
                    eavTable = Jdbc.EAV_VARCHAR;
                    eavField = Jdbc.EAV_VARCHAR + "." + Jdbc.EavVarchar.DOCUMENT_ID;
                }
                // eav character
                if (attributeType == AttributeTypeEnum.Character) {
                    eavTable = Jdbc.EAV_CHARACTER;
                    eavField = Jdbc.EAV_CHARACTER + "." + Jdbc.EavCharacter.DOCUMENT_ID;
                }
                // eav decimal
                if (attributeType == AttributeTypeEnum.Float
                        || attributeType == AttributeTypeEnum.Double) {
                    eavTable = Jdbc.EAV_DECIMAL;
                    eavField = Jdbc.EAV_DECIMAL + "." + Jdbc.EavDecimal.DOCUMENT_ID;
                }
                // eav boolean
                if (attributeType == AttributeTypeEnum.Boolean) {
                    eavTable = Jdbc.EAV_BOOLEAN;
                    eavField = Jdbc.EAV_BOOLEAN + "." + Jdbc.EavBoolean.DOCUMENT_ID;
                }
                // eav integer
                if (attributeType == AttributeTypeEnum.Byte
                        || attributeType == AttributeTypeEnum.Short
                        || attributeType == AttributeTypeEnum.Integer
                        || attributeType == AttributeTypeEnum.Long) {
                    eavTable = Jdbc.EAV_INTEGER;
                    eavField = Jdbc.EAV_INTEGER + "." + Jdbc.EavInteger.DOCUMENT_ID;
                }
                // eav text
                if (attributeType == AttributeTypeEnum.Text) {
                    eavTable = Jdbc.EAV_TEXT;
                    eavField = Jdbc.EAV_TEXT + "." + Jdbc.EavText.DOCUMENT_ID;
                }
                String join = "LEFT JOIN " + eavTable + " ON " + collection.get(Jdbc.Collection.NAME) + "." + collection.get(Jdbc.Collection.NAME) + "_id" + " = " + eavField;
                if (!joins.contains(join)) {
                    joins.add(join);
                }

                String attributeJoin = "LEFT JOIN attribute " + eavTable + "_attribute ON " + eavTable + "_attribute.attribute_id = " + eavTable + ".attribute_id";
                if (!attributeJoins.contains(attributeJoin)) {
                    attributeJoins.add(attributeJoin);
                }
                names.add("MAX( IF(" + eavTable + "_attribute.name = '" + attribute.get(Jdbc.Attribute.NAME) + "', " + eavTable + ".eav_value, NULL) ) AS " + attribute.get(Jdbc.Attribute.NAME));
            } else {
                names.add(this.collection.get(Jdbc.Collection.NAME) + "." + attribute.get(Jdbc.Attribute.NAME) + " AS " + attribute.get(Jdbc.Attribute.NAME));
            }
        }

        List<String> selectFields = new ArrayList<>();
        RepeatingView fields = new RepeatingView("fields");
        for (Map<String, Object> attribute : attributes) {
            TextFieldPanel fieldPanel = new TextFieldPanel(fields.newChildId(), (String) attribute.get(Jdbc.Attribute.ATTRIBUTE_TYPE), (String) attribute.get(Jdbc.Attribute.NAME), this.fields);
            fields.add(fieldPanel);
            selectFields.add((String) attribute.get(Jdbc.Attribute.NAME));
        }

        String documentIdField = collection.get(Jdbc.Collection.NAME) + "." + collection.get(Jdbc.Collection.NAME) + "_id";
        if (!selectFields.isEmpty()) {
            if (hasEav) {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collection.get(Jdbc.Collection.NAME) + " " + StringUtils.join(joins, " ") + " " + StringUtils.join(attributeJoins, " ") + " WHERE " + documentIdField + " = ? GROUP BY " + documentIdField;
                this.fields.putAll(jdbcTemplate.queryForMap(query, documentId));
            } else {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collection.get(Jdbc.Collection.NAME) + " WHERE " + documentIdField + " = ?";
                this.fields.putAll(jdbcTemplate.queryForMap(query, documentId));
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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> collectionRecord = null;
        collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", this.collectionId);

        DocumentModifyRequest requestBody = new DocumentModifyRequest();
        requestBody.setDocument(this.fields);

        DocumentFunction.modifyDocument(jdbcTemplate, (String) collectionRecord.get(Jdbc.Collection.COLLECTION_ID), (String) collectionRecord.get(Jdbc.Collection.NAME), this.documentId, requestBody);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}
