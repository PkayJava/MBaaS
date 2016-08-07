package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.EnumItemValueValidator;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/enum/value/create")
public class EnumValueCreatePage extends MasterPage {

    private String enumId;

    private String format;
    private Label formatLabel;

    private String name;
    private Label nameLabel;

    private String type;
    private Label typeLabel;

    private String value;
    private TextField<String> valueField;
    private TextFeedbackPanel valueFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Enum Value";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.enumId = getPageParameters().get("enumId").toString("");
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> enumRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " = ?", this.enumId);

        this.form = new Form<>("form");
        this.add(this.form);

        this.format = (String) enumRecord.get(Jdbc.Enum.FORMAT);
        this.formatLabel = new Label("formatLabel", new PropertyModel<>(this, "format"));
        this.form.add(this.formatLabel);

        this.name = (String) enumRecord.get(Jdbc.Enum.NAME);
        this.nameLabel = new Label("nameLabel", new PropertyModel<>(this, "name"));
        this.form.add(this.nameLabel);

        this.type = (String) enumRecord.get(Jdbc.Enum.TYPE);
        this.typeLabel = new Label("typeLabel", new PropertyModel<>(this, "type"));
        this.form.add(this.typeLabel);

        this.valueField = new TextField<>("valueField", new PropertyModel<>(this, "value"));
        this.valueField.setRequired(true);
        if (TypeEnum.Boolean.getLiteral().equals(this.type)) {
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Boolean));
        } else if (TypeEnum.Long.getLiteral().equals(this.type)) {
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Long));
        } else if (TypeEnum.Double.getLiteral().equals(this.type)) {
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Double));
        } else if (TypeEnum.Time.getLiteral().equals(this.type)) {
            this.value = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(new Date());
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Time));
        } else if (TypeEnum.Date.getLiteral().equals(this.type)) {
            this.value = DateFormatUtils.ISO_DATE_FORMAT.format(new Date());
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Date));
        } else if (TypeEnum.DateTime.getLiteral().equals(this.type)) {
            this.value = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date());
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.DateTime));
        } else if (TypeEnum.String.getLiteral().equals(this.type)) {
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.String));
        } else if (TypeEnum.Character.getLiteral().equals(this.type)) {
            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Character));
        }
        this.form.add(this.valueField);
        this.valueFeedback = new TextFeedbackPanel("valueFeedback", this.valueField);
        this.form.add(this.valueFeedback);

        PageParameters parameters = new PageParameters();
        parameters.add("enumId", this.enumId);

        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", EnumValueManagementPage.class, parameters);
        this.form.add(closeLink);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.ENUM_ITEM);
        jdbcInsert.usingColumns(Jdbc.EnumItem.ENUM_ID, Jdbc.EnumItem.ENUM_ITEM_ID, Jdbc.EnumItem.VALUE);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.EnumItem.ENUM_ITEM_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.EnumItem.ENUM_ID, this.enumId);
        fields.put(Jdbc.EnumItem.VALUE, this.value);
        jdbcInsert.execute(fields);
        PageParameters parameters = new PageParameters();
        parameters.add("enumId", this.enumId);
        setResponsePage(EnumValueManagementPage.class, parameters);
    }

}
