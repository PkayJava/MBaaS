package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.select2.EnumChoiceProvider;
import com.angkorteam.mbaas.server.select2.JsonChoiceProvider;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/body/field/create")
public class BodyFieldCreatePage extends MasterPage {

    private String jsonId;

    private String jsonName;
    private Label jsonNameLabel;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<String> types = new ArrayList<>();
    private String type;
    private DropDownChoice<String> typeField;
    private TextFeedbackPanel typeFeedback;

    private boolean required = true;
    private CheckBox requiredField;

    private List<String> subTypes = new ArrayList<>();
    private String subType;
    private DropDownChoice<String> subTypeField;
    private TextFeedbackPanel subTypeFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Map<String, Object> mapType;
    private Select2SingleChoice<Map<String, Object>> mapTypeField;
    private TextFeedbackPanel mapTypeFeedback;

    private Map<String, Object> enumType;
    private Select2SingleChoice<Map<String, Object>> enumTypeField;
    private TextFeedbackPanel enumTypeFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Body Field";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.jsonId = getPageParameters().get("jsonId").toString("");

        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> jsonRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", this.jsonId);

        this.form = new Form<>("form");
        this.add(this.form);

        this.jsonName = (String) jsonRecord.get(Jdbc.Json.NAME);
        this.jsonNameLabel = new Label("jsonNameLabel", new PropertyModel<>(this, "jsonName"));
        this.form.add(jsonNameLabel);

        this.required = true;
        this.requiredField = new CheckBox("requiredField", new PropertyModel<>(this, "required"));
        this.requiredField.setRequired(true);
        this.form.add(this.requiredField);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);
        for (TypeEnum type : TypeEnum.values()) {
            if (type.isBodyType()) {
                types.add(type.getLiteral());
            }
        }
        this.typeField = new DropDownChoice<>("typeField", new PropertyModel<>(this, "type"), new PropertyModel<>(this, "types"));
        this.typeField.setOutputMarkupId(true);
        this.typeField.add(new OnChangeAjaxBehavior(this::typeFieldAjaxUpdate));
        this.typeField.setRequired(true);
        this.form.add(this.typeField);
        this.typeFeedback = new TextFeedbackPanel("typeFeedback", this.typeField);
        this.form.add(this.typeFeedback);

        for (TypeEnum type : TypeEnum.values()) {
            if (type.isBodySubType()) {
                subTypes.add(type.getLiteral());
            }
        }
        this.subTypeField = new DropDownChoice<>("subTypeField", new PropertyModel<>(this, "subType"), new PropertyModel<>(this, "subTypes"));
        this.subTypeField.setOutputMarkupId(true);
        this.subTypeField.add(new OnChangeAjaxBehavior(this::subTypeFieldAjaxUpdate));
        this.form.add(this.subTypeField);
        this.subTypeFeedback = new TextFeedbackPanel("subTypeFeedback", this.subTypeField);
        this.form.add(this.subTypeFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.mapTypeField = new Select2SingleChoice<>("mapTypeField", new PropertyModel<>(this, "mapType"), new JsonChoiceProvider(getSession().getApplicationCode()));
        this.form.add(mapTypeField);
        this.mapTypeFeedback = new TextFeedbackPanel("mapTypeFeedback", this.mapTypeField);
        this.form.add(mapTypeFeedback);

        this.enumTypeField = new Select2SingleChoice<>("enumTypeField", new PropertyModel<>(this, "enumType"), new EnumChoiceProvider(getSession().getApplicationCode()));
        this.form.add(enumTypeField);
        this.enumTypeFeedback = new TextFeedbackPanel("enumTypeFeedback", this.enumTypeField);
        this.form.add(enumTypeFeedback);

        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);

        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", BodyFieldManagementPage.class, parameters);
        this.form.add(closeLink);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void subTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        if (TypeEnum.Enum.getLiteral().equals(this.subType)) {
            this.enumTypeField.setRequired(true);
        } else {
            this.enumTypeField.setRequired(false);
        }
        if (TypeEnum.Map.getLiteral().equals(this.subType)) {
            this.mapTypeField.setRequired(true);
        } else {
            this.mapTypeField.setRequired(false);
        }
    }

    private void typeFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.subTypeField);
        if (TypeEnum.List.getLiteral().equals(this.type)) {
            this.subTypes.clear();
            for (TypeEnum type : TypeEnum.values()) {
                if (type.isBodySubType()) {
                    this.subTypes.add(type.getLiteral());
                }
            }
            this.subTypeField.setRequired(true);
        } else {
            this.subTypeField.setRequired(false);
            this.subTypes.clear();
        }
        if (TypeEnum.Enum.getLiteral().equals(this.type)) {
            this.enumTypeField.setRequired(true);
        } else {
            this.enumTypeField.setRequired(false);
        }
        if (TypeEnum.Map.getLiteral().equals(this.type)) {
            this.mapTypeField.setRequired(true);
        } else {
            this.mapTypeField.setRequired(false);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.JSON_FIELD);
        jdbcInsert.usingColumns(Jdbc.JsonField.JSON_ID, Jdbc.JsonField.REQUIRED, Jdbc.JsonField.JSON_FIELD_ID, Jdbc.JsonField.NAME, Jdbc.JsonField.DESCRIPTION, Jdbc.JsonField.TYPE, Jdbc.JsonField.SUB_TYPE, Jdbc.JsonField.MAP_JSON_ID, Jdbc.JsonField.ENUM_ID);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.JsonField.JSON_FIELD_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.JsonField.JSON_ID, this.jsonId);
        fields.put(Jdbc.JsonField.NAME, this.name);
        fields.put(Jdbc.JsonField.REQUIRED, this.required);
        fields.put(Jdbc.JsonField.DESCRIPTION, this.description);
        fields.put(Jdbc.JsonField.TYPE, this.type);
        fields.put(Jdbc.JsonField.SUB_TYPE, this.subType);
        if (this.mapType != null) {
            fields.put(Jdbc.JsonField.MAP_JSON_ID, this.mapType.get(Jdbc.Json.JSON_ID));
        } else {
            fields.put(Jdbc.JsonField.MAP_JSON_ID, null);
        }
        if (this.enumType != null) {
            fields.put(Jdbc.JsonField.ENUM_ID, this.enumType.get(Jdbc.Enum.ENUM_ID));
        } else {
            fields.put(Jdbc.JsonField.ENUM_ID, null);
        }
        jdbcInsert.execute(fields);
        PageParameters parameters = new PageParameters();
        parameters.add("jsonId", this.jsonId);
        setResponsePage(BodyFieldManagementPage.class, parameters);
    }

}
