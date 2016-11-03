package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.Spring;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentModifyPage extends MBaaSPage {

    private String collectionId;
    private CollectionPojo collection;

    private String documentId;

    private Map<String, Object> fields;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return DocumentModifyPage.class.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        this.documentId = getPageParameters().get("documentId").toString();
        this.collectionId = getPageParameters().get("collectionId").toString();

        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(this.collectionId)).fetchOneInto(CollectionPojo.class);

        this.fields = new HashMap<>();

        List<AttributePojo> attributes = context
                .select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(this.collectionId))
                .and(attributeTable.SYSTEM.eq(false))
                .and(attributeTable.NAME.notEqual("system"))
                .fetchInto(AttributePojo.class);


        List<String> joins = new ArrayList<>();
        List<String> attributeJoins = new ArrayList<>();
        List<String> names = new ArrayList<>();

        boolean hasEav = false;
        for (AttributePojo attribute : attributes) {
            boolean eav = attribute.getEav();
            if (eav) {
                hasEav = true;
                TypeEnum type = TypeEnum.valueOf(attribute.getType());
                String eavTable = null;
                String eavField = null;
                // eav time
                if (type == TypeEnum.Time) {
                    eavTable = Tables.EAV_TIME.getName();
                    eavField = Tables.EAV_TIME + "." + Tables.EAV_TIME.DOCUMENT_ID.getName();
                }
                // eav date
                if (type == TypeEnum.Date) {
                    eavTable = Tables.EAV_DATE.getName();
                    eavField = Tables.EAV_DATE.getName() + "." + Tables.EAV_DATE.DOCUMENT_ID.getName();
                }
                // eav datetime
                if (type == TypeEnum.DateTime) {
                    eavTable = Tables.EAV_DATE_TIME.getName();
                    eavField = Tables.EAV_DATE_TIME.getName() + "." + Tables.EAV_DATE_TIME.DOCUMENT_ID.getName();
                }
                // eav varchar
                if (type == TypeEnum.String) {
                    eavTable = Tables.EAV_VARCHAR.getName();
                    eavField = Tables.EAV_VARCHAR.getName() + "." + Tables.EAV_VARCHAR.DOCUMENT_ID.getName();
                }
                // eav character
                if (type == TypeEnum.Character) {
                    eavTable = Tables.EAV_CHARACTER.getName();
                    eavField = Tables.EAV_CHARACTER.getName() + "." + Tables.EAV_CHARACTER.DOCUMENT_ID.getName();
                }
                // eav decimal
                if (type == TypeEnum.Double) {
                    eavTable = Tables.EAV_DECIMAL.getName();
                    eavField = Tables.EAV_DECIMAL.getName() + "." + Tables.EAV_DECIMAL.DOCUMENT_ID.getName();
                }
                // eav boolean
                if (type == TypeEnum.Boolean) {
                    eavTable = Tables.EAV_BOOLEAN.getName();
                    eavField = Tables.EAV_BOOLEAN.getName() + "." + Tables.EAV_BOOLEAN.DOCUMENT_ID.getName();
                }
                // eav integer
                if (type == TypeEnum.Long) {
                    eavTable = Tables.EAV_INTEGER.getName();
                    eavField = Tables.EAV_INTEGER.getName() + "." + Tables.EAV_INTEGER.DOCUMENT_ID.getName();
                }
                // eav text
                if (type == TypeEnum.Text) {
                    eavTable = Tables.EAV_TEXT.getName();
                    eavField = Tables.EAV_TEXT.getName() + "." + Tables.EAV_TEXT.DOCUMENT_ID.getName();
                }
                String join = "LEFT JOIN " + eavTable + " ON " + collection.getName() + "." + collection.getName() + "_id" + " = " + eavField;
                if (!joins.contains(join)) {
                    joins.add(join);
                }

                String attributeJoin = "LEFT JOIN attribute " + eavTable + "_attribute ON " + eavTable + "_attribute.attribute_id = " + eavTable + ".attribute_id";
                if (!attributeJoins.contains(attributeJoin)) {
                    attributeJoins.add(attributeJoin);
                }
                names.add("MAX( IF(" + eavTable + "_attribute.name = '" + attribute.getName() + "', " + eavTable + ".eav_value, NULL) ) AS " + attribute.getName());
            } else {
                names.add(this.collection.getName() + "." + attribute.getName() + " AS " + attribute.getName());
            }
        }

        List<String> selectFields = new ArrayList<>();
        for (AttributePojo attribute : attributes) {
            selectFields.add(attribute.getName());
        }

        String documentIdField = this.collection.getName() + "." + this.collection.getName() + "_id";
        if (!selectFields.isEmpty()) {
            if (hasEav) {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + this.collection.getName() + " " + StringUtils.join(joins, " ") + " " + StringUtils.join(attributeJoins, " ") + " WHERE " + documentIdField + " = ? GROUP BY " + documentIdField;
                this.fields.putAll(jdbcTemplate.queryForMap(query, this.documentId));
            } else {
                String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + this.collection.getName() + " WHERE " + documentIdField + " = ?";
                this.fields.putAll(jdbcTemplate.queryForMap(query, this.documentId));
            }
        }

        this.form = new Form<>("form");
        add(this.form);

        RepeatingView fields = new RepeatingView("fields");
        this.form.add(fields);
        for (AttributePojo attribute : attributes) {
            TypeEnum type = TypeEnum.valueOf(attribute.getType());
            String name = attribute.getName();
            if ("Boolean".equals(type.getLiteral())) {
                BooleanPanel fieldPanel = new BooleanPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Character".equals(type.getLiteral())) {
                CharacterPanel fieldPanel = new CharacterPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, ((String) this.fields.get(name)).charAt(0));
                }
            } else if ("String".equals(type.getLiteral())) {
                StringPanel fieldPanel = new StringPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Text".equals(type.getLiteral())) {
                TextPanel fieldPanel = new TextPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
            } else if ("Long".equals(type.getLiteral())) {
                LongPanel fieldPanel = new LongPanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, ((Number) this.fields.get(name)).longValue());
                }
            } else if ("Double".equals(type.getLiteral())) {
                DoublePanel fieldPanel = new DoublePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, ((Number) this.fields.get(name)).doubleValue());
                }
            } else if ("DateTime".equals(type.getLiteral())) {
                DateTimePanel fieldPanel = new DateTimePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, new Date(((Date) this.fields.get(name)).getTime()));
                }
            } else if ("Date".equals(type.getLiteral())) {
                DatePanel fieldPanel = new DatePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, new Date(((Date) this.fields.get(name)).getTime()));
                }
            } else if ("Time".equals(type.getLiteral())) {
                TimePanel fieldPanel = new TimePanel(fields.newChildId(), name, this.fields);
                fields.add(fieldPanel);
                if (this.fields.get(name) != null) {
                    this.fields.put(name, new Date(((Date) this.fields.get(name)).getTime()));
                }
            }
        }

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        this.closeButton = new BookmarkablePageLink<>("closeButton", DocumentBrowsePage.class, parameters);
        this.form.add(this.closeButton);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DocumentModifyRequest requestBody = new DocumentModifyRequest();
        requestBody.setDocument(this.fields);

        DocumentFunction.internalModifyDocument(this.collectionId, this.documentId, requestBody);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentBrowsePage.class, parameters);
    }
}
