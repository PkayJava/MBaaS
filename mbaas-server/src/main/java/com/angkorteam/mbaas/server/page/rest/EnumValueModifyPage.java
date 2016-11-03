//package com.angkorteam.mbaas.server.page.rest;
//
//import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
//import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
//import com.angkorteam.mbaas.plain.enums.TypeEnum;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.validator.EnumItemValueValidator;
//import com.angkorteam.mbaas.server.wicket.*;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.html.form.TextField;
//import org.apache.wicket.markup.html.link.BookmarkablePageLink;
//import org.apache.wicket.model.PropertyModel;
//import org.apache.wicket.request.mapper.parameter.PageParameters;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by socheat on 8/3/16.
// */
//@AuthorizeInstantiation({"administrator"})
//@Mount("/enum/value/modify")
//public class EnumValueModifyPage extends MasterPage {
//
//    private String enumId;
//    private String enumItemId;
//
//    private String format;
//    private Label formatLabel;
//
//    private String name;
//    private Label nameLabel;
//
//    private String type;
//    private Label typeLabel;
//
//    private String value;
//    private TextField<String> valueField;
//    private TextFeedbackPanel valueFeedback;
//
//    private Form<Void> form;
//    private Button saveButton;
//
//    @Override
//    public String getPageHeader() {
//        return "Modify Enum Value";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        this.enumId = getPageParameters().get("enumId").toString("");
//        this.enumItemId = getPageParameters().get("enumItemId").toString("");
//
//        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
//        Map<String, Object> enumRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.ENUM_ID + " = ?", this.enumId);
//        Map<String, Object> enumItemRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ENUM_ITEM + " WHERE " + Jdbc.EnumItem.ENUM_ITEM_ID + " = ?", this.enumItemId);
//
//        this.form = new Form<>("form");
//        this.add(this.form);
//
//        this.format = (String) enumRecord.get(Jdbc.Enum.FORMAT);
//        this.formatLabel = new Label("formatLabel", new PropertyModel<>(this, "format"));
//        this.form.add(this.formatLabel);
//
//        this.name = (String) enumRecord.get(Jdbc.Enum.NAME);
//        this.nameLabel = new Label("nameLabel", new PropertyModel<>(this, "name"));
//        this.form.add(this.nameLabel);
//
//        this.type = (String) enumRecord.get(Jdbc.Enum.TYPE);
//        this.typeLabel = new Label("typeLabel", new PropertyModel<>(this, "type"));
//        this.form.add(this.typeLabel);
//
//        this.value = (String) enumItemRecord.get(Jdbc.EnumItem.VALUE);
//        this.valueField = new TextField<>("valueField", new PropertyModel<>(this, "value"));
//        this.valueField.setRequired(true);
//        if (TypeEnum.Boolean.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Boolean, this.enumItemId));
//        } else if (TypeEnum.Long.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Long, this.enumItemId));
//        } else if (TypeEnum.Double.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Double, this.enumItemId));
//        } else if (TypeEnum.Time.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Time, this.enumItemId));
//        } else if (TypeEnum.Date.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Date, this.enumItemId));
//        } else if (TypeEnum.DateTime.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.DateTime, this.enumItemId));
//        } else if (TypeEnum.String.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.String, this.enumItemId));
//        } else if (TypeEnum.Character.getLiteral().equals(this.type)) {
//            this.valueField.add(new EnumItemValueValidator(getSession().getApplicationCode(), this.enumId, TypeEnum.Character, this.enumItemId));
//        }
//        this.form.add(this.valueField);
//        this.valueFeedback = new TextFeedbackPanel("valueFeedback", this.valueField);
//        this.form.add(this.valueFeedback);
//
//        PageParameters parameters = new PageParameters();
//        parameters.add("enumId", this.enumId);
//
//        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", EnumValueManagementPage.class, parameters);
//        this.form.add(closeLink);
//
//        this.saveButton = new Button("saveButton");
//        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
//        this.form.add(this.saveButton);
//    }
//
//    private void saveButtonOnSubmit(Button button) {
//        Session session = getSession();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());
//        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
//        jdbcUpdate.withTableName(Jdbc.ENUM_ITEM);
//        Map<String, Object> wheres = new HashMap<>();
//        wheres.put(Jdbc.EnumItem.ENUM_ITEM_ID, this.enumItemId);
//        Map<String, Object> fields = new HashMap<>();
//        if (TypeEnum.Long.getLiteral().equals(this.type)) {
//            fields.put(Jdbc.EnumItem.VALUE, String.valueOf(Long.valueOf(this.value)));
//        } else if (TypeEnum.Double.getLiteral().equals(this.type)) {
//            fields.put(Jdbc.EnumItem.VALUE, String.valueOf(Double.valueOf(this.value)));
//        } else {
//            fields.put(Jdbc.EnumItem.VALUE, this.value);
//        }
//        jdbcUpdate.execute(fields, wheres);
//        PageParameters parameters = new PageParameters();
//        parameters.add("enumId", this.enumId);
//        setResponsePage(EnumValueManagementPage.class, parameters);
//    }
//}
