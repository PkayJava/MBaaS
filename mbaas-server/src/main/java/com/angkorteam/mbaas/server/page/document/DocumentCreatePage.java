package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.template.*;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
@Mount("/document/create")
@AuthorizeInstantiation({"administrator"})
public class DocumentCreatePage extends MasterPage {

    private String collectionId;
    private Map<String, Object> collection;

    private Map<String, Object> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Document :: " + this.collection.get(Jdbc.Collection.NAME);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.fields = new HashMap<>();
        this.collectionId = getPageParameters().get("collectionId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        this.collection = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", this.collectionId);

        List<Map<String, Object>> attributes = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.SYSTEM + " = ?", this.collectionId, false);

        RepeatingView fields = new RepeatingView("fields");
        for (Map<String, Object> attribute : attributes) {
            TypeEnum type = TypeEnum.valueOf((String) attribute.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
            String name = (String) attribute.get(Jdbc.Attribute.NAME);
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
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        requestBody.setDocument(fields);
        String documentId = UUID.randomUUID().toString();
        DocumentFunction.internalInsertDocument(jdbcTemplate, getSession().getApplicationUserId(), documentId, this.collectionId, (String) this.collection.get(Jdbc.Collection.NAME), requestBody);
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}
