package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.select2.EnumChoiceProvider;
import com.angkorteam.mbaas.server.validator.HttpQueryNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/14/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/http/query/modify")
public class HttpQueryModifyPage extends MasterPage {

    private String httpQueryId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String format;
    private TextField<String> formatField;
    private TextFeedbackPanel formatFeedback;

    private List<String> types;
    private String type;
    private DropDownChoice<String> typeField;
    private TextFeedbackPanel typeFeedback;

    private List<String> subTypes;
    private String subType;
    private DropDownChoice<String> subTypeField;
    private TextFeedbackPanel subTypeFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Map<String, Object> enumType;
    private Select2SingleChoice<Map<String, Object>> enumTypeField;
    private TextFeedbackPanel enumTypeFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Http Query";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.httpQueryId = getPageParameters().get("httpQueryId").toString("");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> httpQueryRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " = ?", this.httpQueryId);

        this.form = new Form<>("form");
        this.add(this.form);

        this.name = (String) httpQueryRecord.get(Jdbc.HttpQuery.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new HttpQueryNameValidator(getSession().getApplicationCode(), this.httpQueryId));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.format = (String) httpQueryRecord.get(Jdbc.HttpQuery.FORMAT);
        this.formatField = new TextField<>("formatField", new PropertyModel<>(this, "format"));
        this.form.add(this.formatField);
        this.formatFeedback = new TextFeedbackPanel("formatFeedback", this.formatField);
        this.form.add(this.formatFeedback);

        this.types = new ArrayList<>();
        for (TypeEnum type : TypeEnum.values()) {
            if (type.isHttpQueryType()) {
                this.types.add(type.getLiteral());
            }
        }
        this.type = (String) httpQueryRecord.get(Jdbc.HttpQuery.TYPE);
        this.typeField = new DropDownChoice<>("typeField", new PropertyModel<>(this, "type"), new PropertyModel<>(this, "types"));
        this.typeField.setOutputMarkupId(true);
        this.typeField.add(new OnChangeAjaxBehavior(this::typeFieldAjaxUpdate));
        this.typeField.setRequired(true);
        this.form.add(this.typeField);
        this.typeFeedback = new TextFeedbackPanel("typeFeedback", this.typeField);
        this.form.add(this.typeFeedback);

        this.subTypes = new ArrayList<>();
        for (TypeEnum type : TypeEnum.values()) {
            if (type.isHttpHeaderSubType()) {
                this.subTypes.add(type.getLiteral());
            }
        }
        this.subType = (String) httpQueryRecord.get(Jdbc.HttpQuery.SUB_TYPE);
        this.subTypeField = new DropDownChoice<>("subTypeField", new PropertyModel<>(this, "subType"), new PropertyModel<>(this, "subTypes"));
        this.subTypeField.setOutputMarkupId(true);
        this.form.add(this.subTypeField);
        this.subTypeFeedback = new TextFeedbackPanel("subTypeFeedback", this.subTypeField);
        this.form.add(this.subTypeFeedback);

        this.description = (String) httpQueryRecord.get(Jdbc.HttpQuery.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        if (httpQueryRecord.get(Jdbc.HttpQuery.ENUM_ID) != null) {
            this.enumType = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " = ?", httpQueryRecord.get(Jdbc.HttpQuery.ENUM_ID));
        }
        this.enumTypeField = new Select2SingleChoice<>("enumTypeField", new PropertyModel<>(this, "enumType"), new EnumChoiceProvider(getSession().getApplicationCode()));
        this.form.add(enumTypeField);
        this.enumTypeFeedback = new TextFeedbackPanel("enumTypeFeedback", this.enumTypeField);
        this.form.add(this.enumTypeFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void typeFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.subTypeField);
        if (TypeEnum.List.getLiteral().equals(this.type)) {
            this.subTypes.clear();
            for (TypeEnum type : TypeEnum.values()) {
                if (type.isHttpQuerySubType()) {
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
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.HTTP_QUERY);

        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.HttpQuery.HTTP_QUERY_ID, this.httpQueryId);

        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.HttpQuery.NAME, this.name);
        fields.put(Jdbc.HttpQuery.TYPE, this.type);
        fields.put(Jdbc.HttpQuery.SUB_TYPE, this.subType);
        fields.put(Jdbc.HttpQuery.DESCRIPTION, this.description);
        if (this.enumType != null) {
            fields.put(Jdbc.HttpQuery.ENUM_ID, this.enumType.get(Jdbc.HttpQuery.ENUM_ID));
        }
        jdbcUpdate.execute(fields, wheres);

        setResponsePage(HttpQueryManagementPage.class);
    }

}
